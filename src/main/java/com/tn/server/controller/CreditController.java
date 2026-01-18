package com.tn.server.controller;

import com.tn.server.dto.payment.CreditHistoryDto;
import com.tn.server.dto.payment.CreditResponse;
import com.tn.server.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit") // 팀장님 설정 유지
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    // 잔액 조회 API
    @GetMapping("/balance")
    public ResponseEntity<CreditResponse> getBalance() {
        Long userId = 1L; // 테스트 - 1번 유저

        // Long -> CreditResponse 변환
        Integer currentCredit = creditService.getCreditBalance(userId);
        return ResponseEntity.ok(new CreditResponse(currentCredit));
    }

    // 히스토리 조회 API
    @GetMapping("/history")
    public ResponseEntity<List<CreditHistoryDto>> getHistory() {
        Long userId = 1L; // 테스트 - 1번 유저

        List<CreditHistoryDto> history = creditService.getCreditHistory(userId);
        return ResponseEntity.ok(history);
    }
}