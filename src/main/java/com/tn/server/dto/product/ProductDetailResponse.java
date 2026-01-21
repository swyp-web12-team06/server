package com.tn.server.dto.product;

import com.tn.server.domain.Prompt;
import com.tn.server.domain.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductDetailResponse {
    private Long promptId;
    private String title;
    private String description;
    private Integer price;
    private String categoryName;
    private String modelName;
    private String previewImageUrl;
    private List<String> tags;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDetailResponse from(Prompt prompt) {
        return ProductDetailResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .price(prompt.getPrice())
                .categoryName(prompt.getCategory().getName())
                .modelName(prompt.getAiModel().getName())
                .previewImageUrl(prompt.getPreviewImageUrl())
                .tags(prompt.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .sellerId(prompt.getSeller().getId())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
}