package com.tn.server.dto.purchase;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PurchaseResponse {
    private Long purchase_id;      // 생성된 구매 고유 번호
    private Long prompt_id;        // 구매 완료된 프롬프트 ID
    private Integer credit_balance; // 결제 후 유저의 남은 잔액
    private String purchased_at;   // 구매 완료 일시
}