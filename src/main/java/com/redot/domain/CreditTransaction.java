package com.redot.domain;

import com.redot.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "credit_transactions")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CreditTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private CreditTransactionType type;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer bonus = 0; // 보너스 (기본값 0)

    @Column(name = "reference_id")
    private Long referenceId; // 관련 결제ID 또는 주문ID

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public CreditTransaction(User user, CreditTransactionType type, Integer amount, Integer bonus, Long referenceId) {
        this.user = user;
        this.type = type;
        this.amount = amount;
        this.bonus = bonus;
        this.referenceId = referenceId;
    }
}