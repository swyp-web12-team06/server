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

        // 1. 구매 여부 확인
        Purchase purchase = purchaseRepository.findByUserIdAndPromptId(userId, promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PURCHASED_ITEM));

        // 2. promptEntity 조회 및 프롬프트 치환
        Prompt promptEntity = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        String finalPrompt = promptEntity.getMasterPrompt();

        if (request.getVariableValues() != null) {
            for (var v : request.getVariableValues()) {
                PromptVariable pv = promptVariableRepository.findById(v.getVariableId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));

                finalPrompt = finalPrompt.replace("[" + pv.getKeyName() + "]", v.getValue());
            }
        }

        // 3. 가격 계산 (크레딧 단위 조정 100:1)
        int totalPrice = calculateTotalPrice(promptEntity, request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.decreaseCredit(totalPrice);

        // 4. KieAiClient 모델명 예외처리 및 AI 호출
        if (request.getAiModel() == null || request.getAiModel().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_MODEL_NAME);
        }

        String resolution = (request.getResolution() != null && !request.getResolution().isBlank())
                ? request.getResolution() : null;

        String taskId = kieAiClient.generateAndSaveImage(
                finalPrompt,
                request.getAiModel(),
                resolution,
                request.getAspectRatio()
        );

        // 5. 생성 이력 저장
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .imageUrl(taskId)
                .imageQuality(request.getResolution())
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 6. 사용된 옵션 기록
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

        // 응답 시에도 Presigned URL 적용
        return GenerationResponse.builder()
                .imageId(savedImage.getId())
                .imageUrl(taskId)
                .totalPrice(totalPrice)
                .currentCredit(user.getCreditBalance().longValue())
                .build();
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
}