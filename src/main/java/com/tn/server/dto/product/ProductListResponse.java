package com.tn.server.dto.product;

import com.tn.server.domain.Prompt;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductListResponse {
    private Long promptId;
    private String title;
    private Integer price;
    private String previewImageUrl;
    private Long sellerId; // 닉네임 대신 ID (임시)
    private LocalDateTime createdAt;

    // Entity -> DTO 변환 static from 메소드
    public static ProductListResponse from(Prompt prompt) {
        return ProductListResponse.builder()
                .promptId(prompt.getId())
                .title(prompt.getTitle())
                .price(prompt.getPrice())
                .previewImageUrl(prompt.getPreviewImageUrl())
                .sellerId(prompt.getSeller().getId())
                .createdAt(prompt.getCreatedAt())
                .build();
    }
}