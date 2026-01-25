package com.redot.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditChargeOptionResponse {
    private Long id;            // 옵션 ID
    private Integer amount;     // 결제 금액 (KRW)
    private Integer basicCredit; // 기본 충전 크레딧
    private Integer bonusCredit; // 보너스 크레딧
    private Integer totalCredit; // 총 적립 크레딧
    private String bonusRateText; // 보너스율 텍스트 (예: "5%")
}
