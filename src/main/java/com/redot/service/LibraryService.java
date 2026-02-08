package com.redot.service;

import com.redot.exception.BusinessException;
import com.redot.domain.*;
import com.redot.dto.library.LibraryResponse;
import com.redot.dto.library.LibrarySalesResponse;
import com.redot.exception.ErrorCode;
import com.redot.repository.*;
import com.redot.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
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
    public List<LibraryResponse> getMyPurchases(Long userId) {
        List<Purchase> purchases = purchaseRepository.findByUserIdOrderByPurchasedAtDesc(userId);

        return purchases.stream().map(purchase -> {
            Prompt prompt = promptRepository.findById(purchase.getPrompt().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

            List<GeneratedImage> images = generatedImageRepository.findByPurchaseId(purchase.getId());

            // 변수 정보 추출 (첫 번째 이미지 기준)
            List<LibraryResponse.VariableInfo> variableInfos = List.of();
            if (!images.isEmpty()) {
                variableInfos = imageVariableRepository.findByGeneratedImageId(images.getFirst().getId())
                        .stream().map(v -> LibraryResponse.VariableInfo.builder()
                                .variable_id(v.getPromptVariable().getId())
                                .value(v.getValue())
                                .build())
                        .collect(Collectors.toList());
            }

            return LibraryResponse.builder()
                    .purchase_id(purchase.getId())
                    .prompt_id(prompt.getId())
                    .title(prompt.getTitle())
                    .amount(prompt.getPrice())
                    .variables(variableInfos)
                    .generated_images(images.stream()
                            .map(img -> LibraryResponse.ImageInfo.builder()
                                    .id(img.getId())
                                    .image_url(imageManager.getPublicUrl(img.getImageUrl()))
                                    .build())
                            .collect(Collectors.toList()))
                    .purchased_at(purchase.getPurchasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")))
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * [판매 목록 조회]
     * 판매자가 등록한 프롬프트의 판매 현황(누적 판매수, 수익)을 조회합니다.
     */
    public List<LibrarySalesResponse> getMySalesList(Long userId) {
        List<Prompt> myPrompts = promptRepository.findBySeller_IdOrderByCreatedAtDesc(userId);

        return myPrompts.stream().map(prompt -> {
            // 해당 프롬프트의 총 판매 개수 계산
            int salesCount = purchaseRepository.countByPromptId(prompt.getId());

            // createdAt이 null일 경우를 대비한 안전한 처리
            String formattedDate = (prompt.getCreatedAt() != null)
                    ? prompt.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
                    : "";

            return LibrarySalesResponse.builder()
                    .prompt_id(prompt.getId())
                    .title(prompt.getTitle())
                    .price(prompt.getPrice())
                    .status(prompt.getStatus())
                    .preview_image_url(imageManager.getPresignedGetUrl(prompt.getPreviewImageUrl()))
                    .sales_count(salesCount)
                    .total_revenue(prompt.getPrice() * salesCount)
                    .created_at(formattedDate)
                    .build();
        }).collect(Collectors.toList());
    }
}
