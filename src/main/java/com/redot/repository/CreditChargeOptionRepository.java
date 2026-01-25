package com.redot.repository;

import com.redot.domain.CreditChargeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditChargeOptionRepository extends JpaRepository<CreditChargeOption, Long> {
    // 금액 오름차순 정렬
    List<CreditChargeOption> findAllByOrderByAmountAsc();
}
