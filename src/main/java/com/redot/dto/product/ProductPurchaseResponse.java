package com.redot.dto.product;

import com.redot.domain.Prompt;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ProductPurchaseResponse {

    private Long promptId;
    private String title;
    private String description;
    private String previewImageUrl;

    // 간소화된 모델 정보 (옵션 포함)
    private ModelInfo modelInfo;

    // 프롬프트 변수들 (모든 정보 포함)
    private List<PromptVariableDetail> promptVariables;

    @Getter
    @Builder
    public static class ModelInfo {
        private Long modelId;
        private String modelName;
        private List<String> aspectRatios;
        private List<String> resolutions;
    }

    @Getter
    @Builder
    public static class PromptVariableDetail {
        private Long id;
        private String keyName;
        private String description;
        private Integer orderIndex;
    }

    // Entity -> DTO 변환 메서드
    public static ProductPurchaseResponse from(Prompt prompt, List<String> aspectRatios, List<String> resolutions) {
        return ProductPurchaseResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .previewImageUrl(prompt.getPreviewImageUrl())
                .modelInfo(ModelInfo.builder()
                        .modelId(prompt.getAiModel().getId())
                        .modelName(prompt.getAiModel().getName())
                        .aspectRatios(aspectRatios)
                        .resolutions(resolutions.isEmpty() ? null : resolutions)
                        .build())
                .promptVariables(prompt.getPromptVariables().stream()
                        .map(variable -> PromptVariableDetail.builder()
                                .id(variable.getId())
                                .keyName(variable.getKeyName())
                                .description(variable.getDescription())
                                .orderIndex(variable.getOrderIndex())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}