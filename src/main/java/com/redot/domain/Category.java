package com.redot.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id") // DB: category_id
    private Long id;              // Java: id

    @Column(nullable = false, length = 50)
    private String name;

    // 카테고리 노출 순서
    @Column(name = "order_index")
    private Integer orderIndex;

    // 카테고리 비활성화
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Category(String name, Integer orderIndex, Boolean isActive) {
        this.name = name;
        this.orderIndex = orderIndex;

        // 기본값 설정
        this.isActive = (isActive != null) ? isActive : true;
        this.orderIndex = (orderIndex != null) ? orderIndex : 0;
    }
}