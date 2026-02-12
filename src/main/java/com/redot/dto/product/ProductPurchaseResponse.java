package com.redot.dto.product;

import com.redot.domain.Prompt;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "상품 구매 후 응답 (프롬프트 사용을 위한 정보)")
@Getter
@Builder
public class ProductPurchaseResponse {

    @Schema(description = "프롬프트 ID", example = "123")
    private Long promptId;

    @Schema(description = "프롬프트 제목", example = "프로필 사진 생성 프롬프트")
    private String title;

    @Schema(description = "프롬프트 설명", example = "다양한 스타일의 프로필 사진을 생성할 수 있습니다.")
    private String description;

    @Schema(description = "미리보기 이미지 URL", example = "https://example.com/preview.jpg")
    private String previewImageUrl;

    @Schema(description = "AI 모델 정보 (옵션 포함)")
    private ModelInfo modelInfo;

    @Schema(description = "프롬프트 변수 목록")
    private List<PromptVariableDetail> promptVariables;

    @Schema(description = "AI 모델 상세 정보")
    @Getter
    @Builder
    public static class ModelInfo {
        @Schema(description = "모델 ID", example = "2")
        private Long modelId;

        @Schema(description = "모델 이름", example = "DALL-E 3")
        private String modelName;

        @Schema(description = "지원하는 화면 비율 목록", example = "[\"1:1\", \"16:9\", \"9:16\"]")
        private List<String> aspectRatios;

        @Schema(description = "지원하는 해상도 목록", example = "[\"1024x1024\", \"1792x1024\"]")
        private List<String> resolutions;
    }

    @Schema(description = "프롬프트 변수 상세 정보")
    @Getter
    @Builder
    public static class PromptVariableDetail {
        @Schema(description = "변수 ID", example = "45")
        private Long id;

        @Schema(description = "변수 키 이름", example = "Hair")
        private String keyName;

        @Schema(description = "변수 설명", example = "헤어스타일 종류")
        private String description;

        @Schema(description = "정렬 순서", example = "1")
        private Integer orderIndex;
    }

    // Entity -> DTO 변환 메서드
    public static ProductPurchaseResponse from(Prompt prompt, List<String> aspectRatios, List<String> resolutions, Function<String, String> urlResolver) {
        return ProductPurchaseResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .description(prompt.getDescription())
                .previewImageUrl(urlResolver.apply(prompt.getPreviewImageUrl()))
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