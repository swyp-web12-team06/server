package com.redot.repository;

import com.redot.domain.BonusCreditPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BonusCreditPolicyRepository extends JpaRepository<BonusCreditPolicy, Long> {

    // 입력된 금액보다 작거나 같은(minAmount <= amount) 정책 중
    // 가장 minAmount가 큰(높은) 정책 1개를 가져옴
    Optional<BonusCreditPolicy> findTopByMinAmountLessThanEqualOrderByMinAmountDesc(Integer amount);
}
