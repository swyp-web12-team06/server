package com.tn.server.repository;

import com.tn.server.domain.AiModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiModelRepository extends JpaRepository<AiModel, Long> {
    List<AiModel> findAllByIsActiveTrueOrderByOrderIndexAsc();
}