package com.tn.server.service;

import com.tn.server.domain.user.User;
import com.tn.server.exception.BusinessException;
import com.tn.server.domain.*;
import com.tn.server.dto.prompt.DownloadResponse;
import com.tn.server.dto.prompt.GenerationRequest;
import com.tn.server.dto.prompt.GenerationResponse;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.*;
import com.tn.server.repository.user.UserRepository;
import com.tn.server.service.image.ImageManager;
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
    private final UserRepository userRepository;
    private final ImageManager imageManager;

    @Transactional
    public GenerationResponse generateHighQualityImage(Long userId, Long promptId, GenerationRequest request) {

        // 1. 구매 여부 확인
        Purchase purchase = purchaseRepository.findByUserIdAndPromptId(userId, promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PURCHASED_ITEM));

        // 2. 유저 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 만약 생성 시 100 크레딧이 소모된다면 (명세서 Status 400: INSUFFICIENT_CREDIT 대응)
        // user.decreaseCredit(100);

        // 3. AI 서버 호출 결과 시뮬레이션 (현재는 임시 URL, 실제로는 uploadFromUrl 등을 통해 Key가 반환될 예정)
        String generatedImageUrl = "outputs/" + System.currentTimeMillis() + ".png";

        // 4. 생성 이력 저장
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .imageUrl(generatedImageUrl)
                .imageQuality(request.getQuality())
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 5. 사용된 옵션 기록
        request.getVariable_values().forEach(v -> {
            PromptVariable promptVariable = promptVariableRepository.findById(v.getVariable_id())
                    .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_NOT_FOUND));

            imageValueRepository.save(GeneratedImageVariableValue.builder()
                    .generatedImage(savedImage)
                    .promptVariable(promptVariable)
                    .value(v.getValue())
                    .build());
        });

        return GenerationResponse.builder()
                .image_id(savedImage.getId())
                .image_url(imageManager.getPublicUrl(savedImage.getImageUrl()))
                .build();
    }

    public DownloadResponse getDownloadUrl(Long imageId) {
        // 1. DB에서 생성된 이미지 정보 조회
        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        // 2. 응답 데이터 구성 (DB에 저장된 Key를 Public URL로 변환)
        return DownloadResponse.builder()
                .download_url(imageManager.getPublicUrl(image.getImageUrl()))
                .build();
    }
}
