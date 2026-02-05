package com.redot.controller;

import com.redot.auth.CustomOAuth2User;
import com.redot.dto.common.ApiResponse;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    // 고화질 이미지 생성 API
    @PostMapping(value = "/product/{promptId}/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenerationResponse> generateImage(
            @PathVariable Long promptId,
            @AuthenticationPrincipal CustomOAuth2User user,
            @RequestBody GenerationRequest request
    ) {
        Long userId = user.getUser().getId();

        log.info(">>> [요청 수신] 유저 ID: {}, 프롬프트 ID: {}, AI 생성 로직을 호출합니다!", userId, promptId);

        GenerationResponse response = generationService.generateHighQualityImage(userId, promptId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/{imageId}/download")
    public ResponseEntity<ApiResponse<DownloadResponse>> downloadImage(
            @PathVariable Long imageId
    ) {
        DownloadResponse response = generationService.getDownloadUrl(imageId);

        return ResponseEntity.ok(ApiResponse.success("다운로드 URL 발급 성공", response));
    }
}