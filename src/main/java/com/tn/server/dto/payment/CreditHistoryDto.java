package com.tn.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreditHistoryDto {
    private Long id;
    private String type;     // CHARGE(충전), USE(사용), REFUND(환불)
    private Integer amount;  // 변동 금액
    private Integer bonus;   // 보너스
    private LocalDateTime date; // 발생 일시
}