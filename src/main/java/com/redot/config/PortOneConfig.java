package com.redot.config;

import io.portone.sdk.server.payment.PaymentClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortOneConfig {

    // V2 API Secret
    @Value("${portone.secret.api}")
    private String apiSecret;

    @Bean
    public PaymentClient paymentClient() {
        // V2 클라이언트 생성
        return new PaymentClient(apiSecret, "https://api.portone.io", null);
    }
}