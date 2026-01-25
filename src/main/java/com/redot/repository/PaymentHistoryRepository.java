package com.redot.repository;

import com.redot.domain.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    boolean existsByPaymentUid(String paymentUid);
}