package com.redot.service.ai;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MidjourneyStrategy implements AiRequestStrategy {

    @Override
    public boolean supports(String apiIdentifier) {
        return apiIdentifier != null
                && apiIdentifier.startsWith("mj_");
    }

    @Override
    public String getEndpointPath() {
        return "/api/v1/mj/generate";
    }

    @Override
    public Map<String, Object> buildRequestBody(AiGenerationParams params) {
        Map<String, Object> body = new HashMap<>();

        body.put("taskType", params.getApiIdentifier());
        body.put("prompt", params.getPrompt());
        body.put("speed", params.getSpeed() != null ? params.getSpeed() : "fast");
        body.put("version", "7");

        if (params.getAspectRatio() != null) {
            body.put("aspectRatio", params.getAspectRatio());
        }

        String ref = params.getReferenceImageUrl();
        if (ref != null && !ref.isBlank()) {
            body.put("fileUrls", List.of(ref));
        }

        if (params.getCallbackUrl() != null) {
            body.put("callBackUrl", params.getCallbackUrl());
        }

        return body;
    }
}
