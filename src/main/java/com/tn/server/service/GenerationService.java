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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GenerationService {

    private final PurchaseRepository purchaseRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final PromptVariableValueRepository valueRepository;
    private final GeneratedImageVariableValueRepository imageValueRepository;
    private final UserRepository userRepository;

    @Transactional // 데이터 변경이 일어나므로 쓰기용 Transactional 추가
    public GenerationResponse generateHighQualityImage(Long userId, Long promptId, GenerationRequest request) {

        // 1. 구매 여부 확인 (명세서 Status 403: NOT_PURCHASED_ITEM)
        Purchase purchase = purchaseRepository.findByUserIdAndPromptId(userId, promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_PURCHASED_ITEM));

        // 2. 유저 크레딧 확인 및 차감 (생성 시 비용이 발생한다고 가정)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 만약 생성 시 100 크레딧이 소모된다면 (명세서 Status 400: INSUFFICIENT_CREDIT 대응)
        // user.decreaseCredit(100);

        // 3. AI 서버 호출 결과 시뮬레이션
        String generatedImageUrl = "https://storage.ai.com/outputs/" + System.currentTimeMillis() + ".png";

        // 4. 생성 이력 저장
        GeneratedImage image = GeneratedImage.builder()
                .purchase(purchase)
                .imageUrl(generatedImageUrl)
                .imageQuality(request.getQuality())
                .build();
        GeneratedImage savedImage = generatedImageRepository.save(image);

        // 5. 사용된 옵션 기록 (이 부분의 엔티티 생성을 Builder로 변경 권장)
        request.getVariable_values().forEach(v -> {
            PromptVariableValue val = valueRepository.findByPromptVariableIdAndValue(v.getVariable_id(), v.getValue())
                    .orElseThrow(() -> new BusinessException(ErrorCode.VARIABLE_OPTION_MISMATCH));

            // 기존 new 대신 Builder 패턴 사용 (일관성)
            imageValueRepository.save(GeneratedImageVariableValue.builder()
                    .generatedImage(savedImage)
                    .promptVariableValue(val)
                    .build());
        });

        return GenerationResponse.builder()
                .image_id(savedImage.getId())
                .image_url(savedImage.getImageUrl())
                .build();
    }
    public DownloadResponse getDownloadUrl(Long imageId) {
        // 1. DB에서 생성된 이미지 정보 조회
        GeneratedImage image = generatedImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        // 2. 응답 데이터 구성 (DB에 저장된 imageUrl 반환)
        return DownloadResponse.builder()
                .download_url(image.getImageUrl())
                .build();
    }
}