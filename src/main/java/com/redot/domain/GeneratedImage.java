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

    @Column(name = "image_url", nullable = false, length = 1000)
    private String imageUrl;

    @Column(name = "image_quality")
    private String imageQuality;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "task_id", unique = true) // 💡 TaskID 추가
    private String taskId;

    @Enumerated(EnumType.STRING)
    private GeneratedImageStatus status;

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateStatus(GeneratedImageStatus status) {
        this.status = status;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Builder
    public GeneratedImage(Purchase purchase, String imageUrl, String imageQuality) {
        this.purchase = purchase;
        this.imageUrl = imageUrl;
        this.imageQuality = imageQuality;
        this.taskId = taskId;
        this.status = GeneratedImageStatus.PROCESSING;
        this.createdAt = LocalDateTime.now();
    }
}