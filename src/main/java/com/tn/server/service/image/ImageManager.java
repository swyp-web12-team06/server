package com.tn.server.service.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageManager {

    /**
     * 1. 서버 경유 업로드 (비상용)
     * @return 공개면 Full URL, 비밀이면 Key 반환
     */
    String upload(MultipartFile file, String directoryPath, boolean isSecret);

    /**
     * 2. 업로드용 Presigned URL 발급 (PUT)
     * @param fileName 저장될 파일명 (UUID 포함 권장)
     * @param contentType 파일 타입 (image/png 등)
     * @param isSecret true=비밀버킷, false=공개버킷
     */
    String getPresignedPutUrl(String fileName, String contentType, boolean isSecret);

    /**
     * 3. 다운로드용 Presigned URL 발급 (GET)
     * 오직 '비밀 버킷'에 있는 파일을 조회할 때 사용
     */
    String getPresignedGetUrl(String key);

    /**
     * 4. 파일 삭제
     */
    void delete(String keyOrUrl, boolean isSecret);

    /**
     * 5. URL에서 이미지 다운로드 후 R2에 업로드
     * @param imageUrl 다운로드할 이미지 URL (예: kie.ai 이미지 URL)
     * @param directoryPath 저장할 디렉토리 경로
     * @param isSecret true=비밀버킷, false=공개버킷
     * @return 공개면 Full URL, 비밀이면 Key 반환
     */
    String uploadFromUrl(String imageUrl, String directoryPath, boolean isSecret);
}