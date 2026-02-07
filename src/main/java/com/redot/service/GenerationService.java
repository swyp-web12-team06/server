package com.redot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redot.domain.*;
import com.redot.domain.user.User;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.dto.prompt.PriceCheckRequest;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.repository.user.UserRepository;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

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

    @Value("${app.callback-url:https://redot.store/callback/kie-ai}")
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

        // 3. 서버 내에서 프롬프트 치환 (masterPrompt 기반)
        String finalPrompt = promptEntity.getMasterPrompt();
        if (request.getVariableValues() != null) {
            for (var v : request.getVariableValues()) {
                PromptVariable pv = promptVariableRepository.findById(v.getVariableId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));
                finalPrompt = finalPrompt.replace("[" + pv.getKeyName() + "]", v.getValue());
            }
        }

        // 4. 가격 계산 및 크레딧 차감
        int totalPrice = calculateTotalPrice(promptEntity, request);
        user.decreaseCredit(totalPrice);

        // 5. AI 서버 호출
        String taskId = kieAiClient.generateAndSaveImage(
                finalPrompt,
                promptEntity.getAiModel().getName(),
                request.getResolution(),
                request.getAspectRatio(),
                this.callbackUrl
        );

        // 6. 생성 이력 저장 (상태: PROCESSING)
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .taskId(taskId)
                .imageQuality(request.getResolution())
                .status(GeneratedImageStatus.PROCESSING)
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 7. 사용된 변수 값 저장
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
        String aspectRatio = null;
        int variableSize = 0;

        // DTO 타입별 값 추출
        if (requestDto instanceof GenerationRequest req) {
            resolution = req.getResolution();
            aspectRatio = req.getAspectRatio();
            variableSize = (req.getVariableValues() != null) ? req.getVariableValues().size() : 0;
        } else if (requestDto instanceof PriceCheckRequest req) {
            resolution = req.getResolution();
            aspectRatio = req.getAspectRatio();
            variableSize = (req.getVariableValues() != null) ? req.getVariableValues().size() : 0;
        }

        if (aspectRatio != null) {
            price += modelOptionRepository.findByAiModel_IdAndModelOptionTypeAndOptionValueAndIsActiveTrue(
                            prompt.getAiModel().getId(), ModelOptionType.ASPECT_RATIO, aspectRatio)
                    .map(ModelOption::getAdditionalCost).orElse(0);
        }

        if (resolution != null) {
            price += modelOptionRepository.findByAiModel_IdAndModelOptionTypeAndOptionValueAndIsActiveTrue(
                            prompt.getAiModel().getId(), ModelOptionType.RESOLUTION, resolution)
                    .map(ModelOption::getAdditionalCost).orElse(0);
        }

        price += variableSize;
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
                generatedImage.updateStatus(GeneratedImageStatus.FAILED); // 실패 상태로 변경
                return;
            }

            String tempAiUrl = resultUrls.get(0).asText();

            // 2. R2 업로드
            String r2StoredUrl = imageManager.uploadFromUrl(
                    tempAiUrl,
                    "generated-images/" + taskId,
                    false
            );

            generatedImage.updateImageUrl(r2StoredUrl);
            generatedImage.updateStatus(GeneratedImageStatus.COMPLETED);

            log.info(">>> [R2 업로드 및 생성 완료] TaskID: {}, URL: {}", taskId, r2StoredUrl);

        } catch (Exception e) {
            log.error(">>> [콜백 처리 에러] TaskID: {}, 사유: {}", taskId, e.getMessage());
            // 예외 발생 시 트랜잭션 롤백되겠지만, 로그는 남겨야 함
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}