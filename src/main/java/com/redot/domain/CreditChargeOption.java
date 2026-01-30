package com.redot.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CreditChargeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount; // 결제 금액 (KRW)

    public CreditChargeOption(Integer amount) {
        this.amount = amount;
    }
}
