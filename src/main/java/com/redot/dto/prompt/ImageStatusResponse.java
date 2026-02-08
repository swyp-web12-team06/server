package com.redot.dto.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageStatusResponse {
    private String status;
    private Long imageId;
    private String downloadUrl;
}
