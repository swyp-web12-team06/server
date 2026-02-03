package com.redot.controller;

import com.redot.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/callback")
@RequiredArgsConstructor
public class CallbackController {

    private final GenerationService generationService;

    @PostMapping("/kie-ai")
    public ResponseEntity<Void> handleKieCallback(
            @RequestBody Map<String, Object> callbackData
    ) {
        log.info(">>> [Callback 수신] AI 생성 완료 신호 도착: {}", callbackData);

        String taskId = (String) callbackData.get("task_id");
        String imageUrl = (String) callbackData.get("image_url");

        if (taskId != null && imageUrl != null) {
            generationService.completeImageGeneration(taskId, imageUrl);
        }

        return ResponseEntity.ok().build();
    }
}