package com.redot.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Map;

@Schema(description = "룩북 이미지 생성 요청")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookbookImageCreateDto {
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "대표 이미지 여부 (최대 3개)", example = "true")
    private Boolean isRepresentative;

    @Schema(description = "썸네일 여부 (1개 필수)", example = "false")
    private Boolean isPreview;

    @Schema(description = "프롬프트 변수명과 값의 매핑", example = "{\"Hair\": \"Long\", \"Style\": \"Casual\"}")
    private Map<String, String> optionValues;
}
