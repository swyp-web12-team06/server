package com.tn.server.repository;

import com.tn.server.domain.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * 1. 중복 구매 확인용
     * [구매하기] API에서 이미 산 물건인지 체크할 때 사용합니다.
     */
    boolean existsByUserIdAndPromptId(Long userId, Long promptId);

    /**
     * 2. 구매 내역 상세 조회용
     * [고화질 이미지 생성] API에서 실제로 구매한 사람인지 확인할 때 사용합니다.
     */
    Optional<Purchase> findByUserIdAndPromptId(Long userId, Long promptId);

    List<Purchase> findByUserIdOrderByPurchasedAtDesc(Long userId);

    // 특정 프롬프트가 몇 번 팔렸는지 계산
    int countByPromptId(Long promptId);
}