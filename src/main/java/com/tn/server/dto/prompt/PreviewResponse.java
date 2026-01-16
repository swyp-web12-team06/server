package com.tn.server.dto.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PreviewResponse {
    private String preview_image_url;
}