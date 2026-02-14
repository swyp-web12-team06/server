package com.redot.service.ai;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultKieStrategy implements AiRequestStrategy {

    @Override
    public boolean supports(String apiIdentifier) {
        return apiIdentifier != null
                && !apiIdentifier.startsWith("mj_");
    }

    @Override
    public String getEndpointPath() {
        return "/api/v1/jobs/createTask";
    }

    @Override
    public Map<String, Object> buildRequestBody(AiGenerationParams params) {
        String apiId = params.getApiIdentifier();

        Map<String, Object> body = new HashMap<>();
        body.put("model", apiId);

        if (params.getCallbackUrl() != null) {
            body.put("callBackUrl", params.getCallbackUrl());
        }

        Map<String, Object> input = new HashMap<>();
        input.put("prompt", params.getPrompt());

        if (params.getResolution() != null && !params.getResolution().isBlank()) {
            input.put("resolution", params.getResolution());
        }

        if (params.getAspectRatio() != null) {
            input.put("aspect_ratio", params.getAspectRatio());
        }

        String ref = params.getReferenceImageUrl();
        if (ref != null && !ref.isBlank()) {
            if ("nano-banana-pro".equals(apiId)) {
                input.put("image_input", List.of(ref));
            }
        }

        body.put("input", input);
        return body;
    }
}
