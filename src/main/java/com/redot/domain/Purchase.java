package com.redot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import com.redot.domain.user.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;

    @Builder
    public Purchase(com.redot.domain.user.User user, Prompt prompt, int price) {
        this.user = user;
        this.prompt = prompt;
        this.price = price; // 💡 가격 저장
        this.purchasedAt = LocalDateTime.now();
    }
}