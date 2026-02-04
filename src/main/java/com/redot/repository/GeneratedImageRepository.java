package com.redot.repository;

import com.redot.domain.GeneratedImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GeneratedImageRepository extends JpaRepository<GeneratedImage, Long> {
    List<GeneratedImage> findByPurchaseId(Long purchaseId);

    Optional<GeneratedImage> findByTaskId(String taskId);
}