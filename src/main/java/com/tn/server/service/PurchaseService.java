package com.tn.server.service;

import com.tn.server.domain.Purchase;
import com.tn.server.domain.User;
import com.tn.server.domain.Prompt;
import com.tn.server.dto.purchase.PurchaseResponse;
import com.tn.server.repository.PurchaseRepository;
import com.tn.server.repository.UserRepository;
import com.tn.server.repository.PromptRepository;
import com.tn.server.common.exception.BusinessException; // import 추가 확인
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final PromptRepository promptRepository;

    @Transactional
    public PurchaseResponse purchasePrompt(Long userId, Long promptId) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "존재하지 않는 사용자입니다."));

        // 2. 프롬프트 정보 조회
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException("PROMPT_NOT_FOUND", "존재하지 않는 프롬프트입니다."));

        // 3. 중복 구매 체크 (명세서 Status 400: ALREADY_PURCHASED)
        if (purchaseRepository.existsByUserIdAndPromptId(userId, promptId)) {
            throw new BusinessException("ALREADY_PURCHASED", "이미 구매한 상품입니다.");
        }

        // 4. 잔액 차감 (User 엔티티 내부에서 INSUFFICIENT_CREDIT 예외를 던지도록 수정 권장)
        user.decreaseCredit(prompt.getPrice());

        // 5. 구매 내역 저장
        Purchase purchase = Purchase.builder()
                .userId(userId)
                .promptId(promptId)
                .build();

        Purchase savedPurchase = purchaseRepository.save(purchase);

        // 6. 응답값 생성
        return PurchaseResponse.builder()
                .purchase_id(savedPurchase.getId())
                .prompt_id(savedPurchase.getPromptId())
                .credit_balance(user.getCreditBalance())
                .purchased_at(savedPurchase.getPurchasedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}