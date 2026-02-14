package com.redot.controller;

import com.redot.dto.common.ApiResponse;
import com.redot.dto.prompt.DownloadResponse;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.dto.prompt.GenerationResponse;
import com.redot.dto.prompt.ImageStatusResponse;
import com.redot.service.GenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GenerationController {

    private final GenerationService generationService;

    @PostMapping(value = "/product/{promptId}/generate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenerationResponse> generateImage(
            @PathVariable Long promptId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GenerationRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        log.info(">>> [요청 수신] 유저 ID: {}, 프롬프트 ID: {}, AI 생성 로직을 호출합니다!", userId, promptId);

        GenerationResponse response = generationService.generateHighQualityImage(userId, promptId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/image/{imageId}/status")
    public ResponseEntity<ApiResponse<ImageStatusResponse>> getImageStatus(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ImageStatusResponse response = generationService.getImageStatus(imageId, userId);

        return ResponseEntity.ok(ApiResponse.success("이미지 상태 조회 성공", response));
    }

    @GetMapping("/image/{imageId}/download")
    public ResponseEntity<ApiResponse<DownloadResponse>> downloadImage(
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        DownloadResponse response = generationService.getDownloadUrl(imageId, userId);

        return ResponseEntity.ok(ApiResponse.success("다운로드 URL 발급 성공", response));
    }
}