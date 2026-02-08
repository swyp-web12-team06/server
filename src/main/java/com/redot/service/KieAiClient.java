package com.redot.service;

import com.redot.service.image.ImageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KieAiClient {
    private final RestTemplate restTemplate;
    private final ImageManager imageManager;
    private final ObjectMapper objectMapper;

    @Value("${kie.api.key}")
    private String apiKey;

    public String generateAndSaveImage(String prompt, String modelName, String resolution, String aspectRatio, String callbackUrl, String referenceImageUrl) {
        try {
            log.info(">>> [KieAiClient] 태스크 생성 요청 - 모델: {}, 비율: {}, 콜백: {}", modelName, aspectRatio, callbackUrl);

            String createUrl = "https://api.kie.ai/api/v1/jobs/createTask";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. 요청 바디 구성
            Map<String, Object> body = new HashMap<>();
            body.put("model", (modelName != null && !modelName.isEmpty()) ? modelName : "nano-banana-pro");

            if (callbackUrl != null) {
                body.put("callBackUrl", callbackUrl);
            }

            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);

            if (resolution != null && !resolution.isBlank()) {
                input.put("resolution", resolution);
            }

            if (aspectRatio != null) input.put("aspect_ratio", aspectRatio);

            // 참조 이미지 (모델별 필드명 분기)
            if (referenceImageUrl != null && !referenceImageUrl.isBlank()) {
                if ("nano-banana-pro".equals(modelName)) {
                    input.put("image_input", referenceImageUrl);
                } else if ("grok-imagine/image-to-image".equals(modelName)) {
                    input.put("image_urls", List.of(referenceImageUrl));
                }
            }

            body.put("input", input);

            // 2. 태스크 생성 요청
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(createUrl, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || responseBody.get("data") == null) {
                throw new RuntimeException("KIE 서버 응답 에러 (data 없음)");
            }

            Map<String, Object> dataMap = (Map<String, Object>) responseBody.get("data");
            String taskId = (String) dataMap.get("taskId");

            if (taskId == null) {
                throw new RuntimeException("taskId 발급 실패");
            }

            log.info(">>> [KieAiClient] 태스크 생성 성공. taskId: {}", taskId);

            return taskId;

        } catch (Exception e) {
            log.error(">>> [KieAiClient] 요청 실패: {}", e.getMessage());
            throw new RuntimeException("AI API 요청 중 오류 발생: " + e.getMessage());
        }
    }
}