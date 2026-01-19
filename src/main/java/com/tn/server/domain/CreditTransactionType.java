package com.tn.server.domain;

public enum CreditTransactionType {
    CHARGE, // 충전 (+)
    USE,    // 사용 (-)
    REFUND  // 환불 (+ 또는 -)
}