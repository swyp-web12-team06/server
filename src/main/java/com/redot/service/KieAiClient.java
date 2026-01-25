package com.redot.service;

import com.redot.service.image.ImageManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
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

    public String generateAndSaveImage(String prompt, String modelName, String resolution, String aspectRatio) {
        try {
            log.info(">>> [KieAiClient] 생성 시작 - 모델: {}, 비율: {}", modelName, aspectRatio);

            String createUrl = "https://api.kie.ai/api/v1/jobs/createTask";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. 요청 바디 구성
            Map<String, Object> body = new HashMap<>();
            // 모델명이 없으면 기본값 설정
            body.put("model", (modelName != null && !modelName.isEmpty()) ? modelName : "nano-banana-pro");

            Map<String, Object> input = new HashMap<>();
            input.put("prompt", prompt);
            if (resolution != null) input.put("resolution", resolution);
            if (aspectRatio != null) input.put("aspect_ratio", aspectRatio);
            body.put("input", input);

            // 2. 태스크 생성 요청
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(createUrl, entity, Map.class);

            log.info(">>> [KieAiClient] KIE 서버 응답: {}", response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || responseBody.get("data") == null) {
                throw new RuntimeException("KIE 서버 응답 에러 (data 없음): " + responseBody);
            }

            Map<String, Object> dataMap = (Map<String, Object>) responseBody.get("data");
            String taskId = (String) dataMap.get("taskId");

            if (taskId == null) {
                throw new RuntimeException("taskId를 받지 못했습니다: " + responseBody);
            }

            // 3. 이미지 생성 완료 대기 (폴링)
            String kieGeneratedUrl = null;
            for (int i = 0; i < 20; i++) { // 최대 100초 대기 (5초 * 20회)
                Thread.sleep(5000);
                String queryUrl = "https://api.kie.ai/api/v1/jobs/recordInfo?taskId=" + taskId;

                ResponseEntity<Map> res = restTemplate.exchange(queryUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
                Map resBody = res.getBody();

                if (resBody != null && resBody.get("data") != null) {
                    Map data = (Map) resBody.get("data");
                    String state = (String) data.get("state");

                    log.info(">>> [KieAiClient] 생성 상태 체크 ({}회차): {}", i + 1, state);

                    if ("success".equals(state)) {
                        JsonNode root = objectMapper.readTree((String) data.get("resultJson"));
                        kieGeneratedUrl = root.get("resultUrls").get(0).asText();
                        log.info(">>> [KieAiClient] 이미지 생성 성공! URL: {}", kieGeneratedUrl);
                        break;
                    } else if ("failed".equals(state)) {
                        throw new RuntimeException("AI 생성 실패 (KIE 서버 에러)");
                    }
                }
            }

            if (kieGeneratedUrl == null) {
                throw new RuntimeException("AI 생성 시간 초과 (Timeout)");
            }

            // 4. R2 스토리지 업로드
            log.info(">>> [KieAiClient] R2 스토리지 업로드 시작...");
            return imageManager.uploadFromUrl(kieGeneratedUrl, "generated-images", true);

        } catch (Exception e) {
            log.error(">>> [KieAiClient] 최종 에러 발생: {}", e.getMessage());
            throw new RuntimeException("이미지 생성 프로세스 실패: " + e.getMessage(), e);
        }
    }

    public String generateAndSaveImage(String prompt, String modelName) {
        return generateAndSaveImage(prompt, modelName, null, "1:1");
    }
}