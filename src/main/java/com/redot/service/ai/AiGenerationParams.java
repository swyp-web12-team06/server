package com.redot.service.ai;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiGenerationParams {
    private final String prompt;
    private final String apiIdentifier;
    private final String resolution;
    private final String aspectRatio;
    private final String callbackUrl;
    private final String referenceImageUrl;
    private final String speed;
}
