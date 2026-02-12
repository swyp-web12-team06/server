package com.redot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// AI 모델 Entity
@Entity
@Getter
@Table(name = "ai_models")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long id;

    private String name;

    // API 호출 시 사용하는 식별자 (ex: "nano-banana-pro", "mj_img2img")
    @Column(name = "api_identifier")
    private String apiIdentifier;

    // 참조 이미지 사용 여부 (false: txt2img, true: img2img)
    @Column(name = "use_reference_image", nullable = false)
    private Boolean useReferenceImage;

    // MJ 전용: 생성 속도 (fast, relaxed, turbo)
    @Column(name = "speed")
    private String speed;

    // 모델 노출 순서 (인기 모델 상단 노출용)
    @Column(name = "order_index")
    private Integer orderIndex;

    // 모델 활성화 여부 (구형 모델 숨김용)
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void updateApiIdentifier(String apiIdentifier) {
        this.apiIdentifier = apiIdentifier;
    }

    @Builder
    public AiModel(String name, String apiIdentifier, Boolean useReferenceImage, String speed, Integer orderIndex, Boolean isActive) {
        this.name = name;
        this.apiIdentifier = apiIdentifier;
        this.useReferenceImage = (useReferenceImage != null) ? useReferenceImage : true;
        this.speed = speed;
        this.orderIndex = orderIndex;

        // 기본값 설정
        this.isActive = (isActive != null) ? isActive : true;
        this.orderIndex = (orderIndex != null) ? orderIndex : 0;
    }
}