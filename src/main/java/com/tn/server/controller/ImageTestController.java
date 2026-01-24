package com.tn.server.controller;

import com.tn.server.common.response.ApiResponse;
import com.tn.server.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test/images")
@RequiredArgsConstructor
@Profile({"local", "dev"}) // 운영 환경에서는 비활성화
public class ImageTestController {

    private final ImageManager imageManager;

    // 1. 단순 업로드 테스트 (서버 경유)
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        // 테스트용이므로 경로는 "tests"로 고정
        String result = imageManager.upload(file, "tests", isSecret);

        return ResponseEntity.ok(ApiResponse.success(
                (isSecret ? "[비공개-키 반환] " : "[공개-URL 반환] ") + result
        ));
    }

    // 2. 삭제 테스트 (키만 알면 삭제 가능하므로 주의)
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @RequestParam("keyOrUrl") String keyOrUrl,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        imageManager.delete(keyOrUrl, isSecret);
        return ResponseEntity.ok(ApiResponse.success("삭제 요청 완료 (R2 확인 필요)"));
    }

    // 3. 다운로드용 Presigned URL 발급 테스트 (GET)
    @GetMapping("/presigned-download")
    public ResponseEntity<ApiResponse<String>> getPresignedGetUrl(
            @RequestParam("key") String key
    ) {
        String downloadUrl = imageManager.getPresignedGetUrl(key);
        return ResponseEntity.ok(ApiResponse.success(downloadUrl));
    }

    // 4. URL에서 이미지 다운로드 후 업로드 테스트
    @PostMapping("/upload-from-url")
    public ResponseEntity<ApiResponse<String>> uploadFromUrl(
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        String result = imageManager.uploadFromUrl(imageUrl, "test-url-uploads", isSecret);
        return ResponseEntity.ok(ApiResponse.success(
                (isSecret ? "[비공개-키 반환] " : "[공개-URL 반환] ") + result
        ));
    }
}
