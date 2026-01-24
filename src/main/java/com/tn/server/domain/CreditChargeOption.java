package com.tn.server.domain;

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

    // 화면 표시 순서 등을 위해 필요한 경우 추가 가능
    // private Integer displayOrder;

    public CreditChargeOption(Integer amount) {
        this.amount = amount;
    }
}
