package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.apache.logging.log4j.message.ParameterizedMessage;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "prompts")
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 주입
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    // Member 객체 대신 ID만 저장 (협업 충돌 방지)
    // 이후 멤버 객체로 전환 검토
    @Column(name = "user_id", nullable = false)
    private Long sellerId;

    // ManyToOne으로 카테고리 ID 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AiModel aiModel;
  
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // 설명은 길 수 있음
    private String description;
  
    @Column(nullable = false)
    private Integer price;

    @Column(name = "master_prompt", columnDefinition = "TEXT")
    private String masterPrompt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PromptStatus status;

    @Column(name = "preview_image_url", columnDefinition = "TEXT")
    private String previewImageUrl;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Prompt(Long sellerId, Category category, AiModel aiModel, String title,
                  String description, Integer price, String masterPrompt, String previewImageUrl) {
        this.sellerId = sellerId;
        this.category = category;
        this.aiModel = aiModel;
        this.title = title;
        this.description = description;
        this.price = price;
        this.masterPrompt = masterPrompt;
        this.previewImageUrl = previewImageUrl;

        // 기본값 설정
        this.status = PromptStatus.APPROVED;
        this.isDeleted = false;
    }
}