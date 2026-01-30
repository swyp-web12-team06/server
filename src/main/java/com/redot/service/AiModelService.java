package com.redot.service;

import com.redot.domain.AiModel;
import com.redot.domain.ModelOption;
import com.redot.domain.ModelOptionType;
import com.redot.dto.product.metadata.AiModelResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.AiModelRepository;
import com.redot.repository.ModelOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiModelService {
    private final AiModelRepository aiModelRepository;
    private final ModelOptionRepository modelOptionRepository;

    public List<AiModelResponse> getActiveAiModels() {
        return aiModelRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(m -> new AiModelResponse(m.getId(), m.getName()))
                .collect(Collectors.toList());
    }

    public AiModel getModelOrThrow(Long modelId) {
        AiModel model = aiModelRepository.findById(modelId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND));

        if (!model.getIsActive()) {
            throw new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND);
        }

        return model;
    }

    public List<String> getModelAspectRatios(Long modelId) {
        return modelOptionRepository.findByAiModel_IdAndModelOptionTypeAndIsActiveTrueOrderByOrderIndexAsc(modelId, ModelOptionType.ASPECT_RATIO)
                .stream()
                .map(ModelOption::getOptionValue)
                .collect(Collectors.toList());
    }

    public List<String> getModelResolutions(Long modelId) {
        return modelOptionRepository.findByAiModel_IdAndModelOptionTypeAndIsActiveTrueOrderByOrderIndexAsc(modelId, ModelOptionType.RESOLUTION)
                .stream()
                .map(ModelOption::getOptionValue)
                .collect(Collectors.toList());
    }
}
