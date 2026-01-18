package com.tn.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCompleteResponse {
    private String status; // "PAID" 등
    private String paymentId;
}
