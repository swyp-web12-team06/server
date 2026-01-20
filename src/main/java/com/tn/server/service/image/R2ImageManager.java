package com.tn.server.service.image;

import lombok.RequiredArgsConstructor;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class R2ImageManager implements ImageManager {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String publicBucket;

    @Value("${cloud.aws.s3.secret-bucket}")
    private String secretBucket;

    @Value("${cloud.aws.public-domain}")
    private String publicDomain;

    // 서버 경유 업로드
    @Override
    public String upload(MultipartFile file, String directoryPath, boolean isSecret) {
        String fileName = directoryPath + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        String targetBucket = isSecret ? secretBucket : publicBucket;

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 비밀이면 키만 반환, 공개면 전체 URL 반환
            return isSecret ? fileName : publicDomain + "/" + fileName;

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    // 업로드용 URL 발급 (PUT)
    @Override
    public String getPresignedPutUrl(String fileName, String contentType, boolean isSecret) {
        String targetBucket = isSecret ? secretBucket : publicBucket;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(targetBucket)
                .key(fileName)
                .contentType(contentType)
                .build();

        // 5분간 유효
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url().toString();
    }

    // 다운로드용 URL 발급 (GET) - 비밀 버킷 전용
    @Override
    public String getPresignedGetUrl(String key) {
        // 다운로드 프리사인드는 무조건 Secret Bucket 대상이라고 가정
        // (Public은 그냥 도메인 주소 쓰면 되니까요)

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(secretBucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5)) // 5분 뒤 링크 폭파
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    // 삭제 로직
    @Override
    public void delete(String keyOrUrl, boolean isSecret) {
        String targetBucket = isSecret ? secretBucket : publicBucket;
        String key = extractKey(keyOrUrl); // URL이 들어오면 키만 발라냄

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("이미지 삭제 완료 (버킷: {}, 키: {})", targetBucket, key);

        } catch (Exception e) {
            log.error("이미지 삭제 실패: {}", e.getMessage());
            // 삭제 실패는 흐름을 막지 않도록 로그만 찍고 넘어가는 경우가 많음
        }
    }

    // 내부 헬퍼: URL에서 Key 추출
    private String extractKey(String urlOrKey) {
        // 이미 키 형태라면 그대로 반환
        if (!urlOrKey.startsWith("http")) {
            return urlOrKey;
        }

        // 도메인 제거 로직
        try {
            String key = urlOrKey;
            if (urlOrKey.startsWith(publicDomain)) {
                key = urlOrKey.substring(publicDomain.length());
            }
            if (key.startsWith("/")) {
                key = key.substring(1);
            }
            return URLDecoder.decode(key, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return urlOrKey;
        }
    }
}