package com.redot.service.ai;

import java.util.Map;

public interface AiRequestStrategy {

    boolean supports(String apiIdentifier);

    String getEndpointPath();

    Map<String, Object> buildRequestBody(AiGenerationParams params);
}
