package com.tn.server.repository;

import com.tn.server.domain.LookbookImageVariableOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LookbookImageVariableOptionRepository extends JpaRepository<LookbookImageVariableOption, Long> {
    List<LookbookImageVariableOption> findByPromptVariableValueIdIn(List<Long> valueIds);
}