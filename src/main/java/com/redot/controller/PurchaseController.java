package com.redot.controller;

import com.redot.auth.CustomOAuth2User;
import com.redot.dto.purchase.PurchaseResponse;
import com.redot.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 💡 인증 어노테이션
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class PurchaseController {

    private final PurchaseService purchaseService;

    /**
     * [상품 구매 API]
     * 이제 외부에서 넘겨주는 userId를 믿지 않고, 인증된 세션 정보를 사용합니다.
     */
    @PostMapping("/{promptId}/purchase")
    public ResponseEntity<PurchaseResponse> purchasePrompt(
            @PathVariable Long promptId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    ) {
        // 💡 인증된 유저 엔티티에서 ID 직접 추출
        Long userId = oAuth2User.getUser().getId();

        log.info(">>> [구매 요청] 유저: {}, 프롬프트: {} 구매 로직 실행", userId, promptId);

        PurchaseResponse response = purchaseService.purchasePrompt(userId, promptId);

        return ResponseEntity.ok(response);
    }
}