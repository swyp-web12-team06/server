package com.tn.server.controller;

import com.tn.server.dto.prompt.DownloadResponse;
import com.tn.server.dto.prompt.GenerationRequest;
import com.tn.server.dto.prompt.GenerationResponse;
import com.tn.server.service.GenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompts")
public class GenerationController {

    private final GenerationService generationService;

    // 고화질 이미지 생성 API
    @PostMapping("/{promptId}/generate")
    public ResponseEntity<GenerationResponse> generateImage(
            @PathVariable Long promptId,
            @RequestParam Long userId,
            @RequestBody GenerationRequest request
    ) {
        GenerationResponse response = generationService.generateHighQualityImage(userId, promptId, request);

        // ResponseEntity.ok()는 HTTP 200 OK 상태코드를 반환.
        return ResponseEntity.ok(response);
    }
    @GetMapping("/images/{imageId}/download")
    public ResponseEntity<DownloadResponse> downloadImage(@PathVariable Long imageId) {
        // 서비스 호출하여 결과 반환
        DownloadResponse response = generationService.getDownloadUrl(imageId);
        return ResponseEntity.ok(response);
    }
}