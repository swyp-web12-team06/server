package com.redot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redot.domain.*;
import com.redot.domain.user.User;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.dto.prompt.ImageStatusResponse;
import com.redot.dto.prompt.PriceCheckRequest;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.repository.user.UserRepository;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationService {

    private final PurchaseRepository purchaseRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final PromptVariableRepository promptVariableRepository;
    private final GeneratedImageVariableValueRepository imageValueRepository;
    private final ImageManager imageManager;
    private final KieAiClient kieAiClient;
    private final PromptRepository promptRepository;
    private final UserRepository userRepository;
    private final ModelOptionRepository modelOptionRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.callback-url}")
    private String callbackUrl;

    @Transactional
    public GenerationResponse generateHighQualityImage(Long userId, Long promptId, GenerationRequest request) {

        // 1. DB에서 프롬프트 원본 조회 (보안 강화)
        Prompt promptEntity = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        // 2. 유저 조회 및 즉시 구매 레코드 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Purchase purchase = Purchase.builder()
                .user(user)
                .prompt(promptEntity)
                .price(promptEntity.getPrice())
                .build();
        purchaseRepository.save(purchase);

        // 3. 변수 검증: 프롬프트에 변수가 있으면 반드시 값이 있어야 함
        List<PromptVariable> requiredVariables = promptEntity.getPromptVariables();
        List<GenerationRequest.VariableSelection> submittedValues =
                request.getVariableValues() != null ? request.getVariableValues() : List.of();

        if (!requiredVariables.isEmpty() && requiredVariables.size() != submittedValues.size()) {
            throw new BusinessException(ErrorCode.MISSING_VARIABLE_VALUES);
        }

        // 4. 서버 내에서 프롬프트 치환 (masterPrompt 기반)
        String finalPrompt = promptEntity.getMasterPrompt();
        for (var v : submittedValues) {
            PromptVariable pv = promptVariableRepository.findById(v.getVariableId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));
            finalPrompt = finalPrompt.replace("[" + pv.getKeyName() + "]", v.getValue());
        }

        // 5. 가격 계산 및 크레딧 차감
        int totalPrice = calculateTotalPrice(promptEntity, request);
        user.decreaseCredit(totalPrice);

        // 6. AI 서버 호출 (Grok 모델은 1장 생성 기준)
        String modelName = promptEntity.getAiModel().getName();

        log.info(">>> [AI 생성 요청] 모델: {}, 해상도: {}, 비율: {}",
                modelName, request.getResolution(), request.getAspectRatio());

        String taskId = kieAiClient.generateAndSaveImage(
                finalPrompt,
                modelName,
                request.getResolution(),
                request.getAspectRatio(),
                this.callbackUrl
        );

        // 7. 생성 이력 저장 (상태: PROCESSING)
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .taskId(taskId)
                .imageQuality(request.getResolution())
                .status(GeneratedImageStatus.PROCESSING)
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 8. 사용된 변수 값 저장
        saveVariableValues(savedImage, request);

        return GenerationResponse.builder()
                .imageId(savedImage.getId())
                .taskId(taskId)
                .totalPrice(totalPrice)
                .currentCredit(user.getCreditBalance().longValue())
                .build();
    }

    private void saveVariableValues(GeneratedImage savedImage, GenerationRequest request) {
        if (request.getVariableValues() != null) {
            for (var v : request.getVariableValues()) {
                PromptVariable pv = promptVariableRepository.findById(v.getVariableId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));
                imageValueRepository.save(GeneratedImageVariableValue.builder()
                        .generatedImage(savedImage)
                        .promptVariable(pv)
                        .value(v.getValue())
                        .build());
            }
        }
    }

    private int calculateTotalPrice(Prompt prompt, Object requestDto) {
        int price = prompt.getPrice();
        String resolution = null;

        if (requestDto instanceof GenerationRequest req) {
            resolution = req.getResolution();
        } else if (requestDto instanceof PriceCheckRequest req) {
            resolution = req.getResolution();
        }

        if (resolution != null) {
            price += modelOptionRepository.findByAiModel_IdAndModelOptionTypeAndOptionValueAndIsActiveTrue(
                            prompt.getAiModel().getId(),
                            ModelOptionType.RESOLUTION,
                            resolution)
                    .map(ModelOption::getAdditionalCost)
                    .orElse(0);
        }

        return price;
    }

    public int getEstimatedPrice(Long promptId, PriceCheckRequest request) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        return calculateTotalPrice(prompt, request);
    }

    public DownloadResponse getDownloadUrl(Long imageId) {

        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        String secureUrl = imageManager.getPresignedGetUrl(image.getImageUrl());

        return DownloadResponse.builder()
                .downloadUrl(secureUrl)
                .build();
    }
    public ImageStatusResponse getImageStatus(Long imageId, Long userId) {
        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        if (!image.getPurchase().getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        String downloadUrl = null;
        if (image.getStatus() == GeneratedImageStatus.COMPLETED) {
            downloadUrl = imageManager.getPresignedGetUrl(image.getImageUrl());
        }

        return ImageStatusResponse.builder()
                .imageId(image.getId())
                .status(image.getStatus().name())
                .downloadUrl(downloadUrl)
                .build();
    }

    @Transactional
    public void failImageGeneration(String taskId, String failMsg) {
        generatedImageRepository.findByTaskId(taskId).ifPresent(image -> {
            image.updateStatus(GeneratedImageStatus.FAILED);

            // 크레딧 환불
            Purchase purchase = image.getPurchase();
            purchase.getUser().addCredit(purchase.getPrice());
            log.info(">>> [크레딧 환불] TaskID: {}, 유저ID: {}, 환불액: {}",
                    taskId, purchase.getUser().getId(), purchase.getPrice());

            log.error(">>> [이미지 생성 실패] TaskID: {}, 사유: {}", taskId, failMsg);
        });
    }

    /**
     * [비동기 완료 처리]
     * @param taskId AI 서버 작업 ID
     * @param resultJson Kie AI에서 보낸 JSON 문자열 ("{\"resultUrls\":[\"...\"]}")
     */
    @Transactional
    public void completeImageGeneration(String taskId, String resultJson) {
        try {
            GeneratedImage generatedImage = generatedImageRepository.findByTaskId(taskId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

            if (generatedImage.getStatus() == GeneratedImageStatus.COMPLETED) {
                log.warn(">>> [중복 콜백] 이미 완료된 TaskID입니다: {}", taskId);
                return;
            }

            JsonNode root = objectMapper.readTree(resultJson);
            JsonNode resultUrls = root.path("resultUrls");
            if (resultUrls.isMissingNode() || resultUrls.isEmpty()) {
                log.error(">>> [콜백 데이터 오류] resultUrls가 비어있습니다. TaskID: {}", taskId);
                generatedImage.updateStatus(GeneratedImageStatus.FAILED);
                return;
            }

            String tempAiUrl = resultUrls.get(0).asText();

            // 2. R2 업로드 (isSecret: true로 시크릿 버킷 사용)
            // R2ImageManager.uploadFromUrl은 이미 순수 Key(경로)를 반환하도록 설계되어 있음
            String r2StoredKey = imageManager.uploadFromUrl(
                    tempAiUrl,
                    "generated-images/" + taskId,
                    true
            );

            // 3. DB에는 도메인이 없는 순수 Key만 저장
            generatedImage.updateImageUrl(r2StoredKey);
            generatedImage.updateStatus(GeneratedImageStatus.COMPLETED);

            // 로그에서도 URL 대신 Key임을 명시
            log.info(">>> [R2 업로드 완료] TaskID: {}, 저장된 Key: {}", taskId, r2StoredKey);

        } catch (Exception e) {
            log.error(">>> [콜백 처리 에러] TaskID: {}, 사유: {}", taskId, e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}