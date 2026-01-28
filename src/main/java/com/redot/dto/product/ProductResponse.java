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

    private List<String> representativeImageUrls;
    private String previewImageUrl;
    private List<String> tags;
    private List<PromptVariableDetail> promptVariables;
    private List<LookbookImageDetail> images;
    private SellerInfo seller;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OptionPricingDetail> pricingPolicies;

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

    @Getter
    @Builder
    public static class OptionPricingDetail {
        private String optionType;
        private String optionValue;
        private Integer extraPrice;
    }

    public static ProductResponse from(Prompt prompt, UserProductStatus userStatus, Function<String, String> urlConverter) {

        // 1. 단수 추출: isPreview 기준
        String previewImageUrl = prompt.getLookbookImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPreview()))
                .map(img -> urlConverter.apply(img.getImageUrl()))
                .findFirst()
                .orElse(urlConverter.apply(prompt.getPreviewImageUrl()));

        // 2. 복수 추출: isRepresentative 기준 (최대 3장)
        List<String> representativeImageUrls = prompt.getLookbookImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsRepresentative()))
                .map(img -> urlConverter.apply(img.getImageUrl()))
                .limit(3)
                .collect(Collectors.toList());

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

                // --- 새로 추가된 이미지 필드 매핑 ---
                .previewImageUrl(previewImageUrl)
                .representativeImageUrls(representativeImageUrls)

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
                                .isPreview(image.getIsPreview())
                                .isRepresentative(image.getIsRepresentative())
                                .optionValues(image.getVariableOptions().stream()
                                        .collect(Collectors.toMap(
                                                opt -> opt.getPromptVariable().getKeyName(),
                                                LookbookImageVariableOption::getVariableValue,
                                                (v1, v2) -> v1
                                        )))
                                .build())
                        .collect(Collectors.toList()))
                .seller(SellerInfo.builder()
                        .id(prompt.getSeller().getId())
                        .nickname(prompt.getSeller().getNickname())
                        .build())
                .createdAt(prompt.getCreatedAt())
                .updatedAt(prompt.getUpdatedAt())
                .pricingPolicies(List.of(
                        OptionPricingDetail.builder().optionType("MODEL").optionValue("Nanobana Pro").extraPrice(200).build(),
                        OptionPricingDetail.builder().optionType("RESOLUTION").optionValue("2048").extraPrice(300).build()
                ))
                .build();
    }
}