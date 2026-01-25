package com.redot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Builder
    public AiModel(String name, Integer orderIndex, Boolean isActive) {
        this.name = name;
        this.orderIndex = orderIndex;

        // 기본값 설정
        this.isActive = (isActive != null) ? isActive : true;
        this.orderIndex = (orderIndex != null) ? orderIndex : 0;
    }
}