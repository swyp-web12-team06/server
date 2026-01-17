package com.tn.server.repository;

import com.tn.server.domain.PromptVariableValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromptVariableValueRepository extends JpaRepository<PromptVariableValue, Long> {
    Optional<PromptVariableValue> findByPromptVariableIdAndValue(Long variableId, String value);
}