package com.tn.server.controller;

import com.tn.server.common.response.ApiResponse;
import com.tn.server.dto.payment.CreditChargeOptionResponse;
import com.tn.server.dto.payment.CreditHistoryDto;
import com.tn.server.dto.payment.CreditResponse;
import com.tn.server.dto.payment.PaymentCompleteRequest;
import com.tn.server.dto.payment.PaymentCompleteResponse;
import com.tn.server.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    // 잔액 조회 API
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<CreditResponse>> getBalance(@AuthenticationPrincipal UserDetails user) {
        Long userId = Long.parseLong(user.getUsername());

        Integer currentCredit = creditService.getCreditBalance(userId);
        return ResponseEntity.ok(ApiResponse.success(new CreditResponse(currentCredit)));
    }

    // 히스토리 조회 API
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<CreditHistoryDto>>> getHistory(@AuthenticationPrincipal UserDetails user) {
        Long userId = Long.parseLong(user.getUsername());

        List<CreditHistoryDto> history = creditService.getCreditHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    // 결제 옵션 목록 조회
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<List<CreditChargeOptionResponse>>> getPaymentOptions() {
        List<CreditChargeOptionResponse> options = creditService.getChargeOptions();
        return ResponseEntity.ok(ApiResponse.success(options));
    }

    // 결제 검증 및 충전 (충전 완료)
    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<PaymentCompleteResponse>> chargeCredit(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody PaymentCompleteRequest request) {
        
        Long userId = Long.parseLong(user.getUsername());

        // 서비스 로직 수행
        String status = creditService.completePayment(userId, request.getPaymentId());

        return ResponseEntity.ok(ApiResponse.success(new PaymentCompleteResponse(status, request.getPaymentId())));
    }
}
