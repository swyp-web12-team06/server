package com.tn.server.dto.product;

import com.tn.server.domain.LookbookImageVariableOption;
import com.tn.server.domain.Prompt;
import com.tn.server.domain.PromptVariable;
import com.tn.server.domain.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;

@Getter
@Builder
public class ProductDetailResponse {
    private Long promptId;
    private String title;
    private String description;
    private Integer price;
    private Long categoryId;
    private String categoryName;
    private Long modelId;
    private String modelName;
    private String previewImageUrl;
    private List<String> tags;
    private List<PromptVariableDetail> promptVariables;
    private List<LookbookImageDetail> images;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class PromptVariableDetail {
        private Long id;
        private String keyName;
        private String variableName;
        private String description;
        private Integer orderIndex;
    }

    @Getter
    @Builder
    public static class LookbookImageDetail {
        private Long id;
        private String imageUrl;
        private Boolean isPreview;
        private Boolean isRepresentative;
        private Map<String, String> optionValues;
    }

    public static ProductDetailResponse from(Prompt prompt) {
        return ProductDetailResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .price(prompt.getPrice())
                .categoryId(prompt.getCategory().getId())
                .categoryName(prompt.getCategory().getName())
                .modelId(prompt.getAiModel().getId())
                .modelName(prompt.getAiModel().getName())
                .previewImageUrl(prompt.getPreviewImageUrl())
                .tags(prompt.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .promptVariables(prompt.getPromptVariables().stream()
                        .map(variable -> PromptVariableDetail.builder()
                                .id(variable.getId())
                                .keyName(variable.getKeyName())
                                .variableName(variable.getVariableName())
                                .description(variable.getDescription())
                                .orderIndex(variable.getOrderIndex())
                                .build())
                        .collect(Collectors.toList()))
                .images(prompt.getLookbookImages().stream()
                        .map(image -> LookbookImageDetail.builder()
                                .id(image.getId())
                                .imageUrl(image.getImageUrl())
                                .isPreview(image.getImageUrl().equals(prompt.getPreviewImageUrl()))
                                .isRepresentative(image.getIsRepresentative())
                                .optionValues(image.getVariableOptions().stream()
                                        .collect(Collectors.toMap(
                                                opt -> opt.getPromptVariable().getKeyName(),
                                                LookbookImageVariableOption::getValue
                                        )))
                                .build())
                        .collect(Collectors.toList()))
                .sellerId(prompt.getSeller().getId())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
}