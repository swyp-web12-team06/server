package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "purchases")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "prompt_id", nullable = false)
    private Long promptId;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    @Builder
    public Purchase(Long userId, Long promptId) {
        this.userId = userId;
        this.promptId = promptId;
        this.purchasedAt = LocalDateTime.now();
    }
}