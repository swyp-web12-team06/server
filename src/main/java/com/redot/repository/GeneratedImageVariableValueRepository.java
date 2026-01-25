package com.redot.repository;

import com.redot.domain.GeneratedImageVariableValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedImageVariableValueRepository extends JpaRepository<GeneratedImageVariableValue, Long> {
    List<GeneratedImageVariableValue> findByGeneratedImageId(Long generatedImageId);
}