package com.tn.server.service;

import com.tn.server.domain.AiModel;
import com.tn.server.dto.product.metadata.AiModelDto;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.AiModelRepository;
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

    public List<AiModelDto> getActiveAiModels() {
        return aiModelRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(m -> new AiModelDto(m.getId(), m.getName()))
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
}
