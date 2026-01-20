package com.tn.server.repository;

import com.tn.server.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findBySeller_IdOrderByCreatedAtDesc(Long sellerId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Prompt p SET p.isDeleted = true WHERE p.seller.id = :userId")
    void softDeleteAllByUserId(@Param("userId") Long userId);

}