package com.tn.server.controller;

import com.tn.server.common.response.ApiResponse;
import com.tn.server.dto.payment.CreditHistoryDto;
import com.tn.server.dto.payment.CreditResponse;
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
}