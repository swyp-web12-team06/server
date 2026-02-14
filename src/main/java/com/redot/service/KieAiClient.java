package com.redot.service;

import com.redot.service.ai.AiGenerationParams;
import com.redot.service.ai.AiRequestStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KieAiClient {

    private static final String KIE_BASE_URL = "https://api.kie.ai";

    private final RestTemplate restTemplate;
    private final List<AiRequestStrategy> strategies;

    @Value("${kie.api.key}")
    private String apiKey;

    public String generateAndSaveImage(String prompt, String apiIdentifier, String resolution, String aspectRatio, String callbackUrl, String referenceImageUrl, String speed) {
        try {
            AiRequestStrategy strategy = strategies.stream()
                    .filter(s -> s.supports(apiIdentifier))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("지원하지 않는 AI 모델: " + apiIdentifier));

            String url = KIE_BASE_URL + strategy.getEndpointPath();

            log.info(">>> [KieAiClient] 태스크 생성 요청 - apiId: {}, 엔드포인트: {}, 콜백: {}", apiIdentifier, strategy.getEndpointPath(), callbackUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            AiGenerationParams params = AiGenerationParams.builder()
                    .prompt(prompt)
                    .apiIdentifier(apiIdentifier)
                    .resolution(resolution)
                    .aspectRatio(aspectRatio)
                    .callbackUrl(callbackUrl)
                    .referenceImageUrl(referenceImageUrl)
                    .speed(speed)
                    .build();

            Map<String, Object> body = strategy.buildRequestBody(params);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

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
