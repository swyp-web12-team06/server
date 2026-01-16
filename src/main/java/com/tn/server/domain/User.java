package com.tn.server.domain;

import com.tn.server.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "credit_balance", nullable = false)
    private Integer creditBalance; // 유저 보유 크레딧

    // 잔액 차감 로직 (비즈니스 메서드)
    public void decreaseCredit(int amount) {
        if (this.creditBalance < amount) {
            // 명세서에 있는 400 에러 코드 적용
            throw new BusinessException("INSUFFICIENT_CREDIT", "잔액이 부족합니다.");
        }
        this.creditBalance -= amount;
    }
}