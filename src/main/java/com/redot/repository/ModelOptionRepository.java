package com.redot.repository;

import com.redot.domain.ModelOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ModelOptionRepository extends JpaRepository<ModelOption, Long> {

    // 특정 모델의 활성화된 옵션 조회
    List<ModelOption> findByAiModelIdAndIsActiveTrueOrderByOrderIndexAsc(Long modelId);

    // 특정 모델의 특정 타입 옵션 조회 (aspect_ratio or resolution)
    List<ModelOption> findByAiModelIdAndOptionTypeAndIsActiveTrueOrderByOrderIndexAsc(Long modelId, String optionType);

    // 여러 모델의 옵션 한번에 조회
    @Query("SELECT mo FROM ModelOption mo WHERE mo.aiModel.id IN :modelIds AND mo.isActive = true ORDER BY mo.aiModel.id, mo.orderIndex")
    List<ModelOption> findByModelIdsAndIsActiveTrue(@Param("modelIds") List<Long> modelIds);
}
