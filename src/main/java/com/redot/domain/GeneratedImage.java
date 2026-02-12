package com.redot.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "generated_images")
public class GeneratedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @Column(name = "task_id", unique = true)
    private String taskId;

    @Column(name = "image_url", length = 1000)
    private String imageUrl;

    @Column(name = "image_quality")
    private String imageQuality;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private GeneratedImageStatus status;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateStatus(GeneratedImageStatus status) {
        this.status = status;
    }

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false; // 기본값 비공개

    public void updateVisibility(boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Builder
    public GeneratedImage(Purchase purchase, String taskId, String imageQuality, GeneratedImageStatus status) {
        this.purchase = purchase;
        this.taskId = taskId;
        this.imageQuality = imageQuality;
        this.status = (status != null) ? status : GeneratedImageStatus.PROCESSING;
        this.createdAt = LocalDateTime.now();
    }
}