package com.redot.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookbookImageCreateDto {
    private String imageUrl;
    private Boolean isRepresentative; // 대표 이미지 여부 (최대 3개)
    private Boolean isPreview; // 썸네일 여부 (1개 필수)
    private Map<String, String> optionValues; // 변수명: 값 (예: "Hair": "Long")
}
