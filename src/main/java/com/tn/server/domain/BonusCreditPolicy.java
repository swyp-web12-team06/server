package com.tn.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class BonusCreditPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer minAmount; // 해당 비율이 적용되는 최소 결제 금액 (예: 5000)

    @Column(nullable = false)
    private BigDecimal bonusRate; // 보너스 비율 (예: 0.05)

    @Column(nullable = false)
    private String description; // 정책 설명 (예: "5천원 이상 5%")

    public BonusCreditPolicy(Integer minAmount, BigDecimal bonusRate, String description) {
        this.minAmount = minAmount;
        this.bonusRate = bonusRate;
        this.description = description;
    }
}
