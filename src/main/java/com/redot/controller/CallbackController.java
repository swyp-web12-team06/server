package com.redot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.redot.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class CallbackController {

    private final GenerationService generationService;

    @PostMapping("/kie-ai")
    public ResponseEntity<Void> handleKieCallback(@RequestBody JsonNode root) {
        JsonNode data = root.path("data");
        String taskId = data.path("taskId").asText(null);

        if (taskId == null) {
            log.error(">>> [Callback] taskId가 없습니다.");
            return ResponseEntity.badRequest().build();
        }

        // MJ 콜백: data.state 필드가 없고, data.resultUrls가 직접 존재
        if (data.has("state")) {
            handleDefaultCallback(data, taskId);
        } else {
            handleMidjourneyCallback(root, data, taskId);
        }

        return ResponseEntity.ok().build();
    }

    private void handleDefaultCallback(JsonNode data, String taskId) {
        String state = data.path("state").asText();
        log.info(">>> [Callback 수신] TaskID: {}, 상태: {}", taskId, state);

        if ("success".equals(state)) {
            generationService.completeImageGeneration(taskId, data.path("resultJson").asText());
        } else {
            generationService.failImageGeneration(taskId, data.path("failMsg").asText(null));
        }
    }

    private void handleMidjourneyCallback(JsonNode root, JsonNode data, String taskId) {
        int code = root.path("code").asInt(-1);
        log.info(">>> [MJ Callback 수신] TaskID: {}, code: {}", taskId, code);

        if (code == 200) {
            JsonNode resultUrls = data.path("resultUrls");
            if (resultUrls.isArray() && !resultUrls.isEmpty()) {
                String firstUrl = resultUrls.get(0).asText();
                generationService.completeMjImageGeneration(taskId, firstUrl);
            } else {
                log.error(">>> [MJ Callback] resultUrls가 비어있습니다. TaskID: {}", taskId);
                generationService.failImageGeneration(taskId, "resultUrls 비어있음");
            }
        } else {
            String msg = root.path("msg").asText("MJ 생성 실패");
            generationService.failImageGeneration(taskId, msg);
        }
    }
}
