package com.redot.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "상품 수정 요청 (선택적 필드)")
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @Schema(description = "상품 제목 (5~50자)", example = "수정된 프로필 사진 프롬프트")
    @Size(min = 5, max = 50, message = "제목은 5자 이상 50자 이하여야 합니다.")
    private String title;

    @Schema(description = "상품 설명 (최소 20자)", example = "업데이트된 설명입니다. 더 많은 스타일을 지원합니다.")
    @Size(min = 20, message = "설명은 최소 20자 이상이어야 합니다.")
    private String description;

    @Schema(description = "가격 (500~1000원)", example = "900")
    @Min(value = 500, message = "가격은 최소 500원 이상이어야 합니다.")
    @Max(value = 1000, message = "가격은 최대 1,000원 이하여야 합니다.")
    private Integer price;

    @Schema(description = "카테고리 ID", example = "2")
    private Long categoryId;

    @Schema(description = "미리보기 이미지로 설정할 룩북 이미지 ID", example = "15")
    private Long previewImageId;

    @Schema(description = "태그 목록 (2~5개)", example = "[\"프로필\", \"업데이트\", \"AI\"]")
    @Size(min = 2, max = 5, message = "태그는 최소 2개, 최대 5개까지 등록 가능합니다.")
    private List<String> tags;

    @Schema(description = "대표 이미지로 설정할 룩북 이미지 ID 목록 (최대 3개)", example = "[10, 11, 12]")
    @Size(max = 3, message = "대표 이미지는 최대 3개까지 선택 가능합니다.")
    private List<Long> representativeImageIds;
}
