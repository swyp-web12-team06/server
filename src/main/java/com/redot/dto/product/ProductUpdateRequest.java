package com.redot.dto.product;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @Size(min = 5, max = 50, message = "제목은 5자 이상 50자 이하여야 합니다.")
    private String title;

    @Size(min = 20, message = "설명은 최소 20자 이상이어야 합니다.")
    private String description;

    @Min(value = 500, message = "가격은 최소 500원 이상이어야 합니다.")
    @Max(value = 1000, message = "가격은 최대 1,000원 이하여야 합니다.")
    private Integer price;

    private Long categoryId;

    private Long previewImageId; // 기존 룩북 이미지 중 하나를 선택 (ID)

    @Size(min = 2, max = 5, message = "태그는 최소 2개, 최대 5개까지 등록 가능합니다.")
    private List<String> tags;

    @Size(max = 3, message = "대표 이미지는 최대 3개까지 선택 가능합니다.")
    private List<Long> representativeImageIds;
}
