package com.redot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.service.GenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    // 고화질 이미지 생성 API
    @PostMapping(value = "/product/{promptId}/generate", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<GenerationResponse> generateImage(
            @PathVariable Long promptId,
            @RequestParam Long userId,
            @RequestBody String jsonBody // 일단 문 열어주고
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        GenerationRequest request = objectMapper.readValue(jsonBody, GenerationRequest.class);

        System.out.println(">>> [변환 성공] AI 생성 로직을 호출합니다!");

        GenerationResponse response = generationService.generateHighQualityImage(userId, promptId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/{imageId}/download")
    public ResponseEntity<DownloadResponse> downloadImage(@PathVariable Long imageId) {
        // 서비스 호출하여 결과 반환
        DownloadResponse response = generationService.getDownloadUrl(imageId);
        return ResponseEntity.ok(response);
    }
}