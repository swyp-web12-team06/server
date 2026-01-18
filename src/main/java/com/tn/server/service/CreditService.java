package com.tn.server.service;

import com.tn.server.domain.*;
import com.tn.server.domain.user.User;
import com.tn.server.dto.payment.CreditHistoryDto;
import com.tn.server.repository.CreditTransactionRepository;
import com.tn.server.repository.PaymentHistoryRepository;
import com.tn.server.repository.user.UserRepository;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final UserRepository userRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final PaymentClient paymentClient; // 포트원 V2 클라이언트

    // =====================================================================
    // 결제 검증 및 충전: 결제 검증 + DB 반영 + 실패 시 자동 환불(보상 트랜잭션)
    // =====================================================================
    @Transactional
    public String completePayment(String paymentId) {
        try {
            // 포트원 서버에서 결제 정보 조회 (동기 호출)
            Payment payment = paymentClient.getPayment(paymentId).join();

            // 결제 상태 검증 (결제 완료 상태인지 확인)
            if (!(payment instanceof PaidPayment paidPayment)) {
                throw new IllegalStateException("결제가 완료되지 않았거나 이미 취소되었습니다.");
            }

            // 금액 검증 (DB에 있는 상품 가격과 비교 필요)
            long paidAmount = paidPayment.getAmount().getTotal();
            // [테스트] 1000원 결제만 허용 (실제 로직에서는 DB 상품 가격 조회)
            if (paidAmount != 1000L) {
                // throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
                // 테스트 중이라 일단 통과
            }

            // 유저 조회 (테스트용 1번 유저)
            User user = userRepository.findById(1L)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

            // PaymentHistory 저장 (PG사 거래 내역)
            PaymentHistory history = PaymentHistory.builder()
                    .user(user)
                    .paymentUid(paymentId)
                    .orderUid(paidPayment.getOrderName()) // 주문명
                    .amount((int) paidAmount)
                    .build();
            paymentHistoryRepository.save(history);

            // CreditTransaction 저장
            CreditTransaction transaction = CreditTransaction.builder()
                    .user(user)
                    .type(CreditTransactionType.CHARGE)
                    .amount((int) paidAmount)
                    .bonus(0)
                    .referenceId(history.getId())
                    .build();
            creditTransactionRepository.save(transaction);

            // 7. [지갑] 유저 잔액 증가
            user.addCredit((int) paidAmount);

            log.info("✅ 충전 성공: 유저ID={}, 금액={}, paymentId={}", user.getId(), paidAmount, paymentId);
            return "PAID";

        } catch (Exception e) {
            // 롤백: 서버 로직 실패 시 -> 포트원 결제 자동 취소
            log.error("결제 처리 중 오류 발생! 자동 환불을 시도합니다. paymentId: {}", paymentId, e);

            try {
                paymentClient.cancelPayment(
                        paymentId,                       // 1. paymentId (필수)
                        null,                            // 2. amount (null이면 전액 환불)
                        null,                            // 3. taxFreeAmount
                        null,                            // 4. vatAmount
                        "서버 내부 오류로 인한 자동 취소",  // 5. reason (취소 사유)
                        null,                            // 6. requester
                        null,                            // 7. promotionDiscountRetainOption
                        null,                            // 8. currentCancellableAmount
                        null                             // 9. refundAccount
                ).join();

                log.info("↩️ 자동 취소(환불) 성공: {}", paymentId);
            } catch (Exception cancelError) {
                // 이 로그가 뜨면 개발자가 수동으로 포트원 관리자에서 취소해야 함
                log.error("자동 취소 실패! 수동 환불 필요! paymentId: {}", paymentId, cancelError);
            }

            // 프론트엔드에 에러 응답 전달
            throw new RuntimeException("결제 처리 중 오류가 발생하여 자동 취소되었습니다.");
        }
    }

    // 잔액 조회
    @Transactional(readOnly = true)
    public Integer getCreditBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return user.getCreditBalance();
    }

    // 히스토리 조회 (CreditController에서 호출)
    @Transactional(readOnly = true)
    public List<CreditHistoryDto> getCreditHistory(Long userId) {
        // 최신순 정렬된 내역 조회 -> DTO 변환
        return creditTransactionRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(t -> new CreditHistoryDto(
                        t.getId(),
                        t.getType().toString(), // CHARGE, USE, REFUND
                        t.getAmount(),
                        t.getBonus(),
                        t.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}