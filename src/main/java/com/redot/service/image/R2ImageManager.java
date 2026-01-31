package com.redot.service.image;

import com.redot.util.ImageFileUtils;
import lombok.RequiredArgsConstructor;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
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
import java.io.InputStream;
import java.net.URI;
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
        // 파일명 sanitization 적용
        String sanitizedFilename = ImageFileUtils.sanitizeFilename(file.getOriginalFilename());
        String fileName = directoryPath + "/" + UUID.randomUUID() + "-" + sanitizedFilename;
        String targetBucket = isSecret ? secretBucket : publicBucket;

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putOb, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 항상 Key(fileName)만 반환하도록 수정
            return fileName;

        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    // 업로드용 URL 발급 (PUT)
    @Override
    public String getPresignedPutUrl(String fileName, String contentType, boolean isSecret) {
        // 1. 파일명 확장자 검증
        ImageFileUtils.validateExtension(fileName);

        // 2. Content-Type 검증
        ImageFileUtils.validateContentType(contentType);

        String targetBucket = isSecret ? secretBucket : publicBucket;

        // 3. Presigned URL 생성 (Content-Length 제한 추가)
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(targetBucket)
                .key(fileName)
                .contentType(contentType)
                // .contentLength(ImageFileUtils.MAX_FILE_SIZE) // 5MB 제한 (정확한 용량만 허용함으로 제외)
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
            log.error("이미지 삭제 실패 (버킷: {}, 키: {}): {}", targetBucket, key, e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_DELETE_FAILED);
        }
    }

    // URL에서 이미지 다운로드 후 R2에 업로드
    @Override
    public String uploadFromUrl(String imageUrl, String directoryPath, boolean isSecret) {
        String targetBucket = isSecret ? secretBucket : publicBucket;

        try {
            // 1. URL에서 이미지 다운로드
            URI uri = URI.create(imageUrl);
            byte[] imageBytes;

            try (InputStream inputStream = uri.toURL().openStream()) {
                imageBytes = inputStream.readAllBytes();
            }

            // 2. 빈 파일 체크
            if (imageBytes.length == 0) {
                throw new BusinessException(ErrorCode.IMAGE_FILE_EMPTY);
            }

            // 3. 파일 크기 검증 (10MB)
            if (imageBytes.length > ImageFileUtils.MAX_FILE_SIZE) {
                log.warn("이미지 크기 초과: {} bytes (최대: {} bytes)", imageBytes.length, ImageFileUtils.MAX_FILE_SIZE);
                throw new BusinessException(ErrorCode.IMAGE_FILE_TOO_LARGE);
            }

            // 4. 파일명 생성 (UUID + 확장자)
            String extension = detectImageExtension(imageBytes);
            String fileName = directoryPath + "/" + UUID.randomUUID() + "." + extension;

            // 5. Content-Type 결정
            String contentType = getContentTypeByExtension(extension);

            // 6. R2에 업로드
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(targetBucket)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength((long) imageBytes.length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(imageBytes));

            log.info("URL 이미지 업로드 완료 (버킷: {}, 키: {}, 크기: {} bytes)", targetBucket, fileName, imageBytes.length);

            // 항상 Key(fileName)만 반환하도록 수정
            return fileName;

        } catch (BusinessException e) {
            throw e; // 이미 처리된 예외는 그대로 전파
        } catch (IOException e) {
            log.error("이미지 URL 다운로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("URL 이미지 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    @Override
    public String getPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        // 이미 URL 형태라면 그대로 반환
        if (key.startsWith("http")) {
            return key;
        }
        // 도메인과 결합 (도메인 뒤에 /가 없을 경우 대비)
        String domain = publicDomain.endsWith("/") ? publicDomain.substring(0, publicDomain.length() - 1) : publicDomain;
        String path = key.startsWith("/") ? key : "/" + key;
        return domain + path;
    }

    // 내부 헬퍼: 이미지 바이트에서 확장자 감지 (매직 넘버 기반)
    private String detectImageExtension(byte[] imageBytes) {
        if (imageBytes.length < 12) {
            return "png"; // 기본값
        }

        // PNG: 89 50 4E 47
        if (imageBytes[0] == (byte) 0x89 &&
                imageBytes[1] == 0x50 &&
                imageBytes[2] == 0x4E &&
                imageBytes[3] == 0x47) {
            return "png";
        }

        // JPEG: FF D8 FF
        if (imageBytes[0] == (byte) 0xFF &&
                imageBytes[1] == (byte) 0xD8 &&
                imageBytes[2] == (byte) 0xFF) {
            return "jpg";
        }

        // WebP: 52 49 46 46 ... 57 45 42 50
        if (imageBytes[0] == 0x52 &&
                imageBytes[1] == 0x49 &&
                imageBytes[2] == 0x46 &&
                imageBytes[3] == 0x46 &&
                imageBytes[8] == 0x57 &&
                imageBytes[9] == 0x45 &&
                imageBytes[10] == 0x42 &&
                imageBytes[11] == 0x50) {
            return "webp";
        }

        // 기본값
        return "png";
    }

    // 내부 헬퍼: 확장자로 Content-Type 반환
    private String getContentTypeByExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            default -> "image/png";
        };
    }

    // URL에서 Key 추출
    @Override
    public String extractKey(String urlOrKey) {
        if (urlOrKey == null) return null;
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

    public String uploadBytes(byte[] bytes, String fileName, String contentType) {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(publicBucket)
                    .key(fileName)
                    .contentType(contentType)
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

            return fileName;
        } catch (Exception e) {
            log.error("byte 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }
}