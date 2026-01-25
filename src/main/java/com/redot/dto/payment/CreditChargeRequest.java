package com.redot.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreditChargeRequest {
    private String paymentUid; // 포트원 결제 고유 ID (imp_uid)
    private String orderUid;   // 주문 ID (merchant_uid)
    private Integer amount;    // 충전 금액
}