package com.redot.dto.product.metadata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "AI 모델 응답 DTO")
public class AiModelResponse {
    @Schema(description = "AI 모델 ID", example = "1")
    private Long id;

    @Schema(description = "AI 모델명", example = "nano-banana-pro")
    private String name;
}