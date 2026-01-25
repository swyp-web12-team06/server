package com.redot.dto.payment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCompleteRequest {
    private String paymentId;
}
