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

    private List<String> representativeImageUrl;
    private String previewImageUrls;
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

        // 1. 대표 이미지(RepresentativeUrl): isPreview가 true인 딱 1장 (상세 상단 메인 이미지)
        String previewUrls = prompt.getLookbookImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPreview()))
                .map(img -> urlConverter.apply(img.getImageUrl()))
                .findFirst()
                .orElse(urlConverter.apply(prompt.getPreviewImageUrl()));

        // 2. 미리보기 리스트(PreviewUrls): isRepresentative가 true인 이미지들 (대표 이미지 포함 총 3장)
        List<String> representativeUrl = prompt.getLookbookImages().stream()
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
                .representativeImageUrl(representativeUrl)
                .previewImageUrls(previewUrls)
                .previewImageUrl(urlConverter.apply(prompt.getPreviewImageUrl())) // 기존 호환용

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
                .build();
    }
}