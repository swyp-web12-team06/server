package com.redot.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "상품 생성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {

    @Schema(description = "상품 제목 (5~50자)", example = "프로필 사진 생성 프롬프트")
    @NotBlank(message = "제목을 입력해주세요.")
    @Size(min = 5, max = 50, message = "제목은 5자 이상 50자 이하여야 합니다.")
    private String title;

    @Schema(description = "상품 설명 (최소 20자)", example = "다양한 스타일의 프로필 사진을 생성할 수 있는 고품질 프롬프트입니다.")
    @NotBlank(message = "설명을 입력해주세요.")
    @Size(min = 20, message = "설명은 최소 20자 이상이어야 합니다.")
    private String description;

    @Schema(description = "가격 (500~1000원)", example = "800")
    @NotNull(message = "가격을 입력해주세요.")
    @Min(value = 500, message = "가격은 최소 500원 이상이어야 합니다.")
    @Max(value = 1000, message = "가격은 최대 1,000원 이하여야 합니다.")
    private Integer price;

    @Schema(description = "카테고리 ID", example = "1")
    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @Schema(description = "AI 모델 ID", example = "2")
    @NotNull(message = "AI 모델을 선택해주세요.")
    private Long modelId;

    @Schema(description = "마스터 프롬프트 내용", example = "A professional profile photo with [Hair] hair and [Style] style")
    @NotBlank(message = "프롬프트 내용을 입력해주세요.")
    private String masterPrompt;

    @Schema(description = "태그 목록 (2~5개)", example = "[\"프로필\", \"사진\", \"AI생성\"]")
    @Size(min = 2, max = 5, message = "태그는 최소 2개, 최대 5개까지 등록 가능합니다.")
    private List<String> tags;

    @Schema(description = "프롬프트 변수 목록 (1~5개)")
    @Size(min = 1, max = 5, message = "프롬프트 변수는 최소 1개, 최대 5개까지 등록 가능합니다.")
    private List<PromptVariableCreateDto> promptVariables;

    @Schema(description = "룩북 이미지 목록 (1~10개)")
    @Size(min = 1, max = 10, message = "룩북 이미지는 최소 1개, 최대 10개까지 등록 가능합니다.")
    private List<LookbookImageCreateDto> images;
}