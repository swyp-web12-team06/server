package com.redot.service;

import com.redot.domain.*;
import com.redot.domain.user.User;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.repository.user.UserRepository;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public GenerationResponse generateHighQualityImage(Long userId, Long promptId, GenerationRequest request) {

        // 1. [보안 강화] DB에서 프롬프트 원본 조회 (유저가 보낸 prompt_text 대신 DB 데이터 사용)
        Prompt promptEntity = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        // 2. [로직 통합] 별도 확인 없이 즉시 구매 레코드 생성
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Purchase purchase = Purchase.builder()
                .user(user)
                .prompt(promptEntity)
                .price(promptEntity.getPrice())
                .build();
        purchaseRepository.save(purchase);

        // 3. [보안 강화] 서버 내에서 프롬프트 치환 (masterPrompt 기반)
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

        // 5. [비동기 처리] AI 서버 호출 (콜백 URL 포함)
        String callbackUrl = "https://redot.store/api/v1/callbacks/kie-ai";
        String taskId = kieAiClient.generateAndSaveImage(
                finalPrompt,
                promptEntity.getAiModel().getName(), // DB의 모델명 사용
                request.getResolution(),
                request.getAspectRatio(),
                callbackUrl
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

    private int calculateTotalPrice(Prompt prompt, GenerationRequest request) {
        int price = prompt.getPrice();

        // 모델 추가 비용
        if (request.getAiModel() != null) {
            price += modelOptionRepository.findByOptionValue(request.getAiModel())
                    .map(ModelOption::getAdditionalCost).orElse(0);
        }

        // 해상도 추가 비용
        if (request.getResolution() != null) {
            price += modelOptionRepository.findByOptionValue(request.getResolution())
                    .map(ModelOption::getAdditionalCost).orElse(0);
        }

        // 변수당 1크레딧 추가
        if (request.getVariableValues() != null) {
            price += request.getVariableValues().size();
        }
        return price;
    }

    public int getEstimatedPrice(Long promptId, GenerationRequest request) {
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
    @Transactional
    public void completeImageGeneration(String taskId, String imageUrl) {
        // 1. taskId로 생성 중인 이미지 레코드 찾기
        GeneratedImage generatedImage = generatedImageRepository.findByTaskId(taskId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TASK_NOT_FOUND));

        // 2. 상태 변경 및 URL 저장 (임시 URL -> 실제 AI 생성 URL)
        generatedImage.updateImageUrl(imageUrl);
        generatedImage.updateStatus(GeneratedImageStatus.COMPLETED);

        log.info(">>> [비동기 완료] TaskID: {}의 이미지 생성이 완료되어 URL이 업데이트되었습니다.", taskId);
    }
}