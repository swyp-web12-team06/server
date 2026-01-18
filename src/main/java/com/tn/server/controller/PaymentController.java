package com.tn.server.controller;

import com.tn.server.dto.payment.ItemResponse;
import com.tn.server.dto.payment.PaymentCompleteRequest;
import com.tn.server.dto.payment.PaymentCompleteResponse;
import com.tn.server.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final CreditService creditService;

    // 테스트용 상품 정보 조회 (HTML이 로딩될 때 호출)
    @GetMapping("/api/item")
    public ResponseEntity<ItemResponse> getItem() {
        // 테스트용 고정 상품 정보 (신발)
        // shoes.png 이미지 파일명과 id("shoes")가 일치해야 함
        return ResponseEntity.ok(
                new ItemResponse("shoes", "나이키 페가수스", 1000, "KRW")
        );
    }

    // 결제 검증 및 완료 처리
    @PostMapping("/api/payment/complete")
    public ResponseEntity<PaymentCompleteResponse> completePayment(@RequestBody PaymentCompleteRequest request) {
        try {
            // 서비스 로직 수행
            String status = creditService.completePayment(request.getPaymentId());

            // 성공 시 status 반환 (HTML이 "PAID"를 기다림)
            return ResponseEntity.ok(new PaymentCompleteResponse(status, request.getPaymentId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}