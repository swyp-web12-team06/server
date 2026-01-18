package com.tn.server.repository;

import com.tn.server.domain.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findBySeller_IdOrderByCreatedAtDesc(Long sellerId);
}