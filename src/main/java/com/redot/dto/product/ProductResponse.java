package com.redot.dto.product;

import com.redot.domain.LookbookImageVariableOption;
import com.redot.domain.Prompt;
import com.redot.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Schema(description = "상품 상세 정보 응답")
@Getter
@Builder
public class ProductResponse {
    @Schema(description = "프롬프트 ID", example = "123")
    private Long promptId;

    @Schema(description = "상품 제목", example = "프로필 사진 생성 프롬프트")
    private String title;

    @Schema(description = "상품 설명", example = "다양한 스타일의 프로필 사진을 생성할 수 있습니다.")
    private String description;

    @Schema(description = "가격", example = "800")
    private Integer price;

    @Schema(description = "사용자와 상품 간의 관계 상태")
    private UserProductStatus userStatus;

    @Schema(description = "카테고리 ID", example = "1")
    private Long categoryId;

    @Schema(description = "카테고리 이름", example = "프로필")
    private String categoryName;

    @Schema(description = "AI 모델 ID", example = "2")
    private Long modelId;

    @Schema(description = "AI 모델 이름", example = "DALL-E 3")
    private String modelName;

    @Schema(description = "대표 이미지 URL 목록 (최대 3개)", example = "[\"https://example.com/img1.jpg\"]")
    private List<String> representativeImageUrls;

    @Schema(description = "미리보기 이미지 URL", example = "https://example.com/preview.jpg")
    private String previewImageUrl;

    @Schema(description = "태그 목록", example = "[\"프로필\", \"사진\", \"AI생성\"]")
    private List<String> tags;

    @Schema(description = "프롬프트 변수 목록")
    private List<PromptVariableDetail> promptVariables;

    @Schema(description = "룩북 이미지 목록")
    private List<LookbookImageDetail> images;

    @Schema(description = "판매자 정보")
    private SellerInfo seller;

    @Schema(description = "생성 시각", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시각", example = "2024-01-20T14:20:00")
    private LocalDateTime updatedAt;

    @Schema(description = "옵션별 추가 요금 정책")
    private List<OptionPricingDetail> pricingPolicies;

    @Schema(description = "판매자 정보")
    @Getter
    @Builder
    public static class SellerInfo {
        @Schema(description = "판매자 ID", example = "10")
        private Long id;

        @Schema(description = "판매자 닉네임", example = "프롬프트마스터")
        private String nickname;
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

    @Schema(description = "룩북 이미지 상세 정보")
    @Getter
    @Builder
    public static class LookbookImageDetail {
        @Schema(description = "이미지 ID", example = "100")
        private Long id;

        @Schema(description = "이미지 URL", example = "https://example.com/lookbook1.jpg")
        private String imageUrl;

        @Schema(description = "미리보기 이미지 여부", example = "false")
        private Boolean isPreview;

        @Schema(description = "대표 이미지 여부", example = "true")
        private Boolean isRepresentative;

        @Schema(description = "변수명과 값 매핑", example = "{\"Hair\": \"Long\", \"Style\": \"Casual\"}")
        private Map<String, String> optionValues;
    }

    @Schema(description = "옵션별 추가 요금 정보")
    @Getter
    @Builder
    public static class OptionPricingDetail {
        @Schema(description = "옵션 타입", example = "MODEL")
        private String optionType;

        @Schema(description = "옵션 값", example = "Nanobana Pro")
        private String optionValue;

        @Schema(description = "추가 요금", example = "200")
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