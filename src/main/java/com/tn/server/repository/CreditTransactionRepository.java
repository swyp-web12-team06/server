package com.tn.server.repository;

import com.tn.server.domain.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, Long> {
    // 특정 유저의 내역을 최신순으로 가져오기
    List<CreditTransaction> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}