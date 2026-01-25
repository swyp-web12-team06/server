package com.redot.service;

import com.redot.domain.*;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.repository.user.UserRepository;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public GenerationResponse generateHighQualityImage(Long userId, Long promptId, GenerationRequest request) {

        // 1. 구매 여부 확인 (
        Purchase purchase = purchaseRepository.findByUserIdAndPromptId(userId, promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PURCHASED_ITEM));

        // 2. promptEntity 직접 조회
        Prompt promptEntity = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        String promptTemplate = promptEntity.getMasterPrompt();
        String finalPrompt = promptEntity.getMasterPrompt();

        if (request.getVariableValues() != null) {
            for (var v : request.getVariableValues()) {
                PromptVariable pv = promptVariableRepository.findById(v.getVariableId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));
                finalPrompt = finalPrompt.replace("{{" + pv.getName() + "}}", v.getValue());
            }
        }

        // 3. 가격 계산
        int totalPrice = calculateTotalPrice(promptEntity, request);

        // 4. AI 호출 및 R2 저장 (KieAiClient에서 uploadFromUrl 호출됨)
        String finalR2Path = kieAiClient.generateAndSaveImage(
                finalPrompt,
                request.getAiModel(),
                request.getResolution(),
                request.getAspectRatio() // "16:9" 등으로 합쳐진 문자열
        );

        // 5. 생성 이력 저장
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .imageUrl(finalR2Path)
                .imageQuality(request.getResolution())
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 6. 사용된 옵션 기록
        if (request.getVariableValues() != null) {
            request.getVariableValues().forEach(v -> {
                PromptVariable pv = promptVariableRepository.findById(v.getVariableId()).get();
                imageValueRepository.save(GeneratedImageVariableValue.builder()
                        .generatedImage(savedImage)
                        .promptVariable(pv)
                        .value(v.getValue())
                        .build());
            });
        }

        return GenerationResponse.builder()
                .image_id(savedImage.getId())
                .image_url(imageManager.getPublicUrl(savedImage.getImageUrl()))
                .total_price(totalPrice)
                .build();
    }

    private int calculateTotalPrice(Prompt prompt, GenerationRequest request) {
        int price = prompt.getPrice();
        if ("Nanobana Pro".equals(request.getAiModel())) price += 200;
        if ("2048".equals(request.getResolution())) price += 300;
        return price;
    }

    public DownloadResponse getDownloadUrl(Long imageId) {
        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));
        return DownloadResponse.builder()
                .download_url(imageManager.getPublicUrl(image.getImageUrl()))
                .build();
    }

    public String generateImage(String prompt) {
        return null;
    }
}