package com.redot.dto.product;

import com.redot.domain.LookbookImage;
import com.redot.domain.Prompt;
import com.redot.domain.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductListResponse {
    private Long promptId;
    private String title;
    private Integer price;
    private String categoryName;
    private String modelName;
    private String previewImageUrl;
    private List<String> representativeImages;
    private SellerInfo seller;
    private List<String> tags;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    public static class SellerInfo {
        private Long id;
        private String nickname;
    }

    public static ProductListResponse from(Prompt prompt, Function<String, String> urlConverter) {
        return ProductListResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .price(prompt.getPrice())
                .categoryName(prompt.getCategory().getName())
                .modelName(prompt.getAiModel().getName())
                .previewImageUrl(urlConverter.apply(prompt.getPreviewImageUrl()))
                .representativeImages(prompt.getLookbookImages().stream()
                        .filter(LookbookImage::getIsRepresentative)
                        .map(img -> urlConverter.apply(img.getImageUrl()))
                        .limit(3)
                        .collect(Collectors.toList()))
                .seller(SellerInfo.builder()
                        .id(prompt.getSeller().getId())
                        .nickname(prompt.getSeller().getNickname())
                        .build())
                .tags(prompt.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .createdAt(prompt.getCreatedAt())
                .build();
    }
}
