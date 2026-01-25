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

    @Builder
    public GeneratedImage(Purchase purchase, String imageUrl, String imageQuality) {
        this.purchase = purchase;
        this.imageUrl = imageUrl;
        this.imageQuality = imageQuality;
        this.createdAt = LocalDateTime.now();
    }
}