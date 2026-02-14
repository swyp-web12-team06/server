package com.redot.service;

import com.redot.exception.BusinessException;
import com.redot.domain.*;
import com.redot.dto.library.LibraryResponse;
import com.redot.dto.library.LibrarySalesResponse;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
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

            GeneratedImage image = generatedImageRepository.findByPurchaseId(purchase.getId()).getFirst();

            // 변수 정보 추출
            List<LibraryResponse.VariableInfo> variableInfos = imageVariableRepository.findByGeneratedImageId(image.getId())
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
                    .status(image.getStatus().name())
                    .variables(variableInfos)
                    .generated_images(List.of(LibraryResponse.ImageInfo.builder()
                            .id(image.getId())
                            .image_url(image.getStatus() == GeneratedImageStatus.COMPLETED
                                    ? imageManager.getPresignedGetUrl(image.getImageUrl())
                                    : null)
                            .status(image.getStatus().name())
                            .build()))
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
