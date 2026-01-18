package com.tn.server.domain;

import com.tn.server.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 기록
public class PaymentHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;      // 충전한 사람

    private String orderUid;    // 우리 주문 번호 (merchant_uid)
    private String paymentUid;  // 포트원 결제 번호 (imp_uid)
    private Integer amount;        // 충전 금액

    @CreatedDate
    private LocalDateTime createdAt; // 결제 일시

    @Builder
    public PaymentHistory(User user, String orderUid, String paymentUid, Integer amount) {
        this.user = user;
        this.orderUid = orderUid;
        this.paymentUid = paymentUid;
        this.amount = amount;
    }
}