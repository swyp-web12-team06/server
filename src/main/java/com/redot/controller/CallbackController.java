package com.redot.controller;

import com.redot.dto.kieai.KieAiCallbackRequest;
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
    public ResponseEntity<Void> handleKieCallback(
            @RequestBody KieAiCallbackRequest request
    ) {
        log.info(">>> [Kie AI Callback 수신] TaskID: {}, 상태: {}",
                request.getData().getTaskId(), request.getData().getState());

        // 1. 상태가 success일 때만 처리
        if ("success".equals(request.getData().getState())) {
            // 2. 서비스 레이어의 파싱 로직으로 taskId와 resultJson 전달
            generationService.completeImageGeneration(
                    request.getData().getTaskId(),
                    request.getData().getResultJson()
            );
        } else {
            log.error(">>> [AI 생성 실패] TaskID: {}, 사유: {}",
                    request.getData().getTaskId(), request.getData().getFailMsg());
        }

        return ResponseEntity.ok().build();
    }
}