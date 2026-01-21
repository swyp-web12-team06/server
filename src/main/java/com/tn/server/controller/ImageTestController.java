package com.tn.server.controller;

import com.tn.server.service.image.ImageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test/images")
@RequiredArgsConstructor
public class ImageTestController {

    private final ImageManager imageManager;

    // 1. 단순 업로드 테스트 (서버 경유)
    // POST /api/test/images/upload?isSecret=true (또는 false)
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        // isSecret에 따라 공개/비공개 버킷으로 자동 분기됨
        String result = imageManager.upload(file, "tests", isSecret);

        return ResponseEntity.ok(
                (isSecret ? "[비공개-키 반환] " : "[공개-URL 반환] ") + result
        );
    }

    // 2. 삭제 테스트
    // DELETE /api/test/images?keyOrUrl=...&isSecret=true
    @DeleteMapping
    public ResponseEntity<String> deleteImage(
            @RequestParam("keyOrUrl") String keyOrUrl,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        imageManager.delete(keyOrUrl, isSecret);
        return ResponseEntity.ok("삭제 요청 완료 (R2 확인 필요)");
    }

    // 3. 업로드용 Presigned URL 발급 (PUT)
    // GET /api/test/images/presigned-upload?fileName=a.png&contentType=image/png&isSecret=true
    @GetMapping("/presigned-upload")
    public ResponseEntity<Map<String, String>> getPresignedPutUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        // 1. 파일명 생성 (UUID) - DB에 저장할 이름
        String savedFileName = "tests/" + UUID.randomUUID() + "-" + fileName;

        // 2. URL 발급
        String presignedUrl = imageManager.getPresignedPutUrl(savedFileName, contentType, isSecret);

        // 3. 응답 (URL + 키)
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl); // PUT 할 주소
        response.put("savedFileName", savedFileName); // 나중에 DB에 저장할 키
        response.put("bucketType", isSecret ? "SECRET" : "PUBLIC");

        return ResponseEntity.ok(response);
    }

    // 4. 다운로드용 Presigned URL 발급 (GET) - 비공개 버킷 전용
    // GET /api/test/images/presigned-download?key=tests/....png
    @GetMapping("/presigned-download")
    public ResponseEntity<String> getPresignedGetUrl(
            @RequestParam("key") String key
    ) {
        // 비공개 버킷에 있는 파일을 5분간 볼 수 있는 링크 생성
        String downloadUrl = imageManager.getPresignedGetUrl(key);
        return ResponseEntity.ok(downloadUrl);
    }

    // 5. URL에서 이미지 다운로드 후 업로드 (kie.ai 등 외부 AI 이미지)
    // POST /api/test/images/upload-from-url?imageUrl=...&isSecret=false
    @PostMapping("/upload-from-url")
    public ResponseEntity<String> uploadFromUrl(
            @RequestParam("imageUrl") String imageUrl,
            @RequestParam(value = "isSecret", defaultValue = "false") boolean isSecret
    ) {
        String result = imageManager.uploadFromUrl(imageUrl, "ai-images", isSecret);
        return ResponseEntity.ok(
                (isSecret ? "[비공개-키 반환] " : "[공개-URL 반환] ") + result
        );
    }
}