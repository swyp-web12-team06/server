package com.redot.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "프롬프트 변수 생성 요청")
@Getter
@NoArgsConstructor
public class PromptVariableCreateDto {
    @Schema(description = "변수 키 이름", example = "Hair")
    private String keyName;

    @Schema(description = "변수 설명", example = "헤어스타일 종류")
    private String description;

    @Schema(description = "정렬 순서", example = "1")
    private Integer orderIndex;
}