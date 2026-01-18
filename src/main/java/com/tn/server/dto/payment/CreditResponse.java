package com.tn.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreditResponse {
    private Integer currentCredit; // 충전 후 잔액
}