package com.redot.service;

import com.redot.exception.BusinessException;
import com.redot.domain.*;
import com.redot.dto.library.LibraryResponse;
import com.redot.dto.library.LibrarySalesResponse;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.domain.GeneratedImage;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryService {

    private final PurchaseRepository purchaseRepository;
    private final PromptRepository promptRepository;
    private final GeneratedImageRepository generatedImageRepository;
    private final GeneratedImageVariableValueRepository imageVariableRepository;
    private final ImageManager imageManager;

    /**
     * [구매 목록 조회]
     * 사용자가 구매한 프롬프트와 그로 인해 생성된 이미지/변수 정보를 조회합니다.
     */
    public Page<LibraryResponse> getMyPurchases(Long userId, Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findValidPurchasesByUserId(userId, pageable);

        return purchases.map(purchase -> {
            Prompt prompt = promptRepository.findById(purchase.getPrompt().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

            List<GeneratedImage> images = generatedImageRepository.findByPurchaseId(purchase.getId());

            // 2. 이미지가 없을 경우를 대비한 안전한 처리
            if (images.isEmpty()) {
                throw new BusinessException(ErrorCode.IMAGE_NOT_FOUND);
            }

            // 3. 변수 정보 추출 (첫 번째 이미지 기준)
            List<LibraryResponse.VariableInfo> variableInfos = imageVariableRepository.findByGeneratedImageId(images.get(0).getId())
                    .stream().map(v -> LibraryResponse.VariableInfo.builder()
                            .variable_id(v.getPromptVariable().getId())
                            .value(v.getValue())
                            .build())
                    .collect(Collectors.toList());

            return LibraryResponse.builder()
                    .purchase_id(purchase.getId())
                    .prompt_id(prompt.getId())
                    .title(prompt.getTitle())
                    .amount(prompt.getPrice())
                    .status(images.get(0).getStatus().name()) // 첫 번째 이미지 상태 기준
                    .variables(variableInfos)

                    // 4. 중복 호출되던 generated_images를 하나로 통합하고 img.isPublic() 호출
                    .generated_images(images.stream()
                            .map(img -> LibraryResponse.ImageInfo.builder()
                                    .id(img.getId())
                                    .image_url(img.getStatus() == GeneratedImageStatus.COMPLETED
                                            ? imageManager.getPresignedGetUrl(img.getImageUrl())
                                            : null)
                                    .status(img.getStatus().name())
                                    .is_public(img.isPublic())
                                    .build())
                            .collect(Collectors.toList()))
                    .purchased_at(purchase.getPurchasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")))
                    .build();
        });
    }

    /**
     * [판매 목록 조회]
     * 판매자가 등록한 프롬프트의 판매 현황(누적 판매수, 수익)을 조회합니다.
     */
    public Page<LibrarySalesResponse> getMySalesList(Long userId, Pageable pageable) {
        Page<Prompt> myPrompts = promptRepository.findBySeller_IdOrderByCreatedAtDesc(userId, pageable);

        return myPrompts.map(prompt -> {
            int salesCount = purchaseRepository.countByPromptId(prompt.getId());

            String formattedDate = (prompt.getCreatedAt() != null)
                    ? prompt.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
                    : "";

            return LibrarySalesResponse.builder()
                    .prompt_id(prompt.getId())
                    .title(prompt.getTitle())
                    .price(prompt.getPrice())
                    .status(prompt.getStatus())
                    .preview_image_url(imageManager.getPublicUrl(prompt.getPreviewImageUrl()))
                    .sales_count(salesCount)
                    .total_revenue(prompt.getPrice() * salesCount)
                    .created_at(formattedDate)
                    .build();
        });
    }
}