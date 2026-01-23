package com.tn.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCompleteResponse {
    private String status; // "PAID" 등
    private String paymentId;
    private Integer addedCredit; // 충전된 금액 (보너스 포함)
    private Integer totalBalance; // 충전 후 총 잔액
}
