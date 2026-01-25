package com.redot.util;

import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class ImageFileUtils {

    // 허용 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    // 허용 Content-Type
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    // 최대 파일 크기: 5MB
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

    /**
     * Content-Type 검증
     */
    public static void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            log.warn("허용되지 않은 Content-Type: {}", contentType);
            throw new BusinessException(ErrorCode.IMAGE_INVALID_FORMAT);
        }
    }

    /**
     * 파일 확장자 검증
     */
    public static void validateExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new BusinessException(ErrorCode.IMAGE_INVALID_EXTENSION);
        }

        String extension = fileName
                .substring(fileName.lastIndexOf(".") + 1)
                .toLowerCase(Locale.ROOT);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("허용되지 않은 확장자: {} (파일명: {})", extension, fileName);
            throw new BusinessException(ErrorCode.IMAGE_INVALID_EXTENSION);
        }
    }

    /**
     * 파일명 Sanitization (특수문자 제거)
     * 영문, 숫자, 하이픈, 언더스코어만 허용
     */
    public static String sanitizeFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "unknown";
        }

        // 확장자 분리
        String nameWithoutExt = originalFilename;
        String extension = "";

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            nameWithoutExt = originalFilename.substring(0, lastDotIndex);
            extension = originalFilename.substring(lastDotIndex); // .jpg 형태로 저장
        }

        // 파일명에서 허용되지 않은 문자 제거
        // 영문, 숫자, 하이픈, 언더스코어만 허용
        String sanitized = nameWithoutExt
                .replaceAll("[^a-zA-Z0-9_-]", "_")
                .replaceAll("_{2,}", "_") // 연속된 언더스코어를 하나로
                .replaceAll("^_+|_+$", ""); // 앞뒤 언더스코어 제거

        // 빈 파일명 처리
        if (sanitized.isEmpty()) {
            sanitized = "file";
        }

        // 너무 긴 파일명 자르기 (최대 50자)
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }

        return sanitized + extension.toLowerCase(Locale.ROOT);
    }
}
