package com.tn.server.service;

import com.tn.server.dto.payment.PaymentCompleteResponse;
import com.tn.server.domain.*;
import com.tn.server.domain.user.User;
import com.tn.server.dto.payment.CreditChargeOptionResponse;
import com.tn.server.dto.payment.CreditHistoryDto;
import com.tn.server.repository.BonusCreditPolicyRepository;
import com.tn.server.repository.CreditChargeOptionRepository;
import com.tn.server.repository.CreditTransactionRepository;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.PaymentHistoryRepository;
import com.tn.server.repository.user.UserRepository;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditService {

    private final UserRepository userRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final CreditChargeOptionRepository creditChargeOptionRepository;
    private final BonusCreditPolicyRepository bonusCreditPolicyRepository; // 보너스 정책 조회용
    private final PaymentClient paymentClient; // 포트원 V2 클라이언트

    // 결제 옵션 목록 조회 (프론트엔드 노출용)
    @Transactional(readOnly = true)
    public List<CreditChargeOptionResponse> getChargeOptions() {
        return creditChargeOptionRepository.findAllByOrderByAmountAsc().stream()
                .map(option -> {
                    int amount = option.getAmount();
                    int basicCredit = amount / 100;
                    
                    // 정책 조회 및 계산
                    BonusCreditPolicy policy = findPolicy(amount);
                    int bonusCredit = calculateBonus(amount, policy);
                    String bonusRateText = formatBonusText(policy);

                    int totalCredit = basicCredit + bonusCredit;

                    return new CreditChargeOptionResponse(
                            option.getId(),
                            amount,
                            basicCredit,
                            bonusCredit,
                            totalCredit,
                            bonusRateText
                    );
                })
                .collect(Collectors.toList());
    }

    // 결제 검증 및 충전
    @Transactional
    public PaymentCompleteResponse completePayment(Long userId, String paymentId) {
        try {
            // 중복 처리 여부 확인
            if (paymentHistoryRepository.existsByPaymentUid(paymentId)) {
                throw new BusinessException(ErrorCode.ALREADY_PROCESSED_PAYMENT);
            }

            // 포트원 서버에서 결제 정보 조회 (동기 호출)
            Payment payment = paymentClient.getPayment(paymentId).join();

            // 결제 상태 검증
            if (!(payment instanceof PaidPayment paidPayment)) {
                throw new BusinessException(ErrorCode.PAYMENT_NOT_COMPLETED);
            }

            // 금액 검증 및 정책 확인
            long paidAmountLong = paidPayment.getAmount().getTotal();
            int paidAmount = (int) paidAmountLong;

            validatePaymentAmount(paidAmount);

            // 크레딧 계산 (DB 정책 적용)
            int basicCredit = paidAmount / 100;
            BonusCreditPolicy policy = findPolicy(paidAmount);
            int bonusCredit = calculateBonus(paidAmount, policy);
            int totalCredit = basicCredit + bonusCredit;

            // 유저 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            // PaymentHistory 저장
            PaymentHistory history = PaymentHistory.builder()
                    .user(user)
                    .paymentUid(paymentId)
                    .orderUid(paidPayment.getOrderName())
                    .amount(paidAmount)
                    .build();
            paymentHistoryRepository.save(history);

            // CreditTransaction 저장
            CreditTransaction transaction = CreditTransaction.builder()
                    .user(user)
                    .type(CreditTransactionType.CHARGE)
                    .amount(totalCredit)
                    .bonus(bonusCredit)
                    .referenceId(history.getId())
                    .build();
            creditTransactionRepository.save(transaction);

            // 유저 잔액 증가
            user.addCredit(totalCredit);

            log.info("충전 성공: 유저ID={}, 결제금액={}원, 지급크레딧={} (보너스 {}), paymentId={}",
                    user.getId(), paidAmount, totalCredit, bonusCredit, paymentId);
            
            return new PaymentCompleteResponse("PAID", paymentId, totalCredit, user.getCreditBalance());

        } catch (BusinessException be) {
            log.error("결제 정책 위반으로 인한 자동 환불 진행: {}", be.getMessage());
            cancelPayment(paymentId, be.getMessage());
            throw be;
        } catch (Exception e) {
            log.error("결제 처리 중 알 수 없는 오류 발생! 자동 환불 시도. paymentId: {}", paymentId, e);
            cancelPayment(paymentId, "서버 내부 오류로 인한 자동 취소");
            throw new BusinessException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }
    }

    // 결제 취소 (환불)
    private void cancelPayment(String paymentId, String reason) {
        try {
            paymentClient.cancelPayment(
                    paymentId, null, null, null,
                    reason, null, null, null, null
            ).join();
            log.info("자동 취소(환불) 성공: {}", paymentId);
        } catch (Exception cancelError) {
            log.error("자동 취소 실패! 수동 환불 필요! paymentId: {}", paymentId, cancelError);
        }
    }

    // 금액 유효성 검증
    private void validatePaymentAmount(int amount) {
        if (amount < 3000 || amount > 50000) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        if (amount % 100 != 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
        }
    }

    // 정책 조회 (Helper)
    private BonusCreditPolicy findPolicy(int amount) {
        return bonusCreditPolicyRepository.findTopByMinAmountLessThanEqualOrderByMinAmountDesc(amount)
                .orElse(null); // 정책이 없으면 null 반환 (0% 처리)
    }

    // 보너스 크레딧 계산 (Helper)
    private int calculateBonus(int amount, BonusCreditPolicy policy) {
        if (policy == null) {
            return 0;
        }

        int basicCredit = amount / 100;
        BigDecimal bonus = new BigDecimal(basicCredit).multiply(policy.getBonusRate());
        return bonus.intValue(); // 소수점 버림
    }

    private String formatBonusText(BonusCreditPolicy policy) {
        if (policy == null || policy.getBonusRate().compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }

        int percent = policy.getBonusRate().multiply(new BigDecimal(100)).intValue();
        return percent + "%";
    }

    // 잔액 조회
    @Transactional(readOnly = true)
    public Integer getCreditBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return user.getCreditBalance();
    }

    // 히스토리 조회
    @Transactional(readOnly = true)
    public List<CreditHistoryDto> getCreditHistory(Long userId) {
        return creditTransactionRepository.findAllByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(t -> new CreditHistoryDto(
                        t.getId(),
                        t.getType().toString(),
                        t.getAmount(),
                        t.getBonus(),
                        t.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}