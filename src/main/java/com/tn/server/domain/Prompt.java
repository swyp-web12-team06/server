package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "prompts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Prompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prompt_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String status;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @CreatedDate // 이 부분이 있어야 getCreatedAt()을 쓸 수 있습니다.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;// APPROVED, PENDING 등
}