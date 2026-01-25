package com.redot.dto.product;

import com.redot.domain.LookbookImageVariableOption;
import com.redot.domain.Prompt;
import com.redot.domain.Tag;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductResponse {
    private Long promptId;
    private String title;
    private String description;
    private Integer price;
    private UserProductStatus userStatus;
    private Long categoryId;
    private String categoryName;
    private Long modelId;
    private String modelName;
    private String previewImageUrl;
    private List<String> tags;
    private List<PromptVariableDetail> promptVariables;
    private List<LookbookImageDetail> images;
    private SellerInfo seller;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class SellerInfo {
        private Long id;
        private String nickname;
    }

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

    public static ProductResponse from(Prompt prompt, UserProductStatus userStatus, Function<String, String> urlConverter) {
        return ProductResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .price(prompt.getPrice())
                .userStatus(userStatus)
                .categoryId(prompt.getCategory().getId())
                .categoryName(prompt.getCategory().getName())
                .modelId(prompt.getAiModel().getId())
                .modelName(prompt.getAiModel().getName())
                .previewImageUrl(urlConverter.apply(prompt.getPreviewImageUrl()))
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
                                .imageUrl(urlConverter.apply(image.getImageUrl()))
                                .isPreview(image.getImageUrl().equals(prompt.getPreviewImageUrl()))
                                .isRepresentative(image.getIsRepresentative())
                                .optionValues(image.getVariableOptions().stream()
                                        .collect(Collectors.toMap(
                                                opt -> opt.getPromptVariable().getKeyName(),
                                                LookbookImageVariableOption::getVariableValue
                                        )))
                                .build())
                        .collect(Collectors.toList()))
                .seller(SellerInfo.builder()
                        .id(prompt.getSeller().getId())
                        .nickname(prompt.getSeller().getNickname())
                        .build())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .build();
    }
}
