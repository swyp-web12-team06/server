package com.tn.server.controller;

import com.tn.server.dto.common.ApiResponse;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.service.RateLimiterService;
import com.tn.server.service.image.ImageManager;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageManager imageManager;
    private final RateLimiterService rateLimiterService;

    @Value("${cloud.aws.public-domain}")
    private String publicDomain;

    // 업로드용 Presigned URL 발급 (PUT)
    // GET /api/image/presigned-upload?fileName=a.png&contentType=image/png
    @GetMapping("/presigned-upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedPutUrl(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType
    ) {
        Long userId = Long.parseLong(user.getUsername());
        Bucket bucket = rateLimiterService.resolveBucket(userId);

        if (!bucket.tryConsume(1)) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        // 1. 파일명 생성 (UUID) - 유저별 폴더에 저장 권장
        String savedFileName = "uploads/" + userId + "/" + UUID.randomUUID() + "-" + fileName;

        // 2. URL 발급 (isSecret = false 고정)
        String presignedUrl = imageManager.getPresignedPutUrl(savedFileName, contentType, false);

        // 3. 응답 (URL + 키 + 완성된 퍼블릭 주소)
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl); // PUT 할 주소
        response.put("savedFileName", savedFileName); // 나중에 DB에 저장할 키 (백업용)
        response.put("publicUrl", publicDomain + "/" + savedFileName); // 실제 접근 주소 (상품 등록용)

        return ResponseEntity.ok(ApiResponse.success("URL 발급에 성공했습니다.", response));
    }
}