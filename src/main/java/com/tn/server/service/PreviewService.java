package com.tn.server.service;

import com.tn.server.common.exception.BusinessException;
import com.tn.server.domain.LookbookImageVariableOption;
import com.tn.server.domain.PromptVariableValue;
import com.tn.server.dto.prompt.PreviewRequest;
import com.tn.server.dto.prompt.PreviewResponse;
import com.tn.server.repository.LookbookImageVariableOptionRepository;
import com.tn.server.repository.PromptVariableValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreviewService {

    private final PromptVariableValueRepository valueRepository;
    private final LookbookImageVariableOptionRepository optionRepository;

    public PreviewResponse getPreviewImage(Long promptId, PreviewRequest request) {
        // 1. 요청된 가변값들의 ID를 조회
        List<Long> valueIds = request.getVariable_values().stream()
                .map(v -> valueRepository.findByPromptVariableIdAndValue(v.getVariable_id(), v.getValue())
                        .orElseThrow(() -> new BusinessException("VARIABLE_OPTION_MISMATCH", "잘못된 변수 옵션입니다."))
                        .getId())
                .collect(Collectors.toList());

        // 2. 해당 가변값들을 포함하는 모든 옵션 조합 조회
        List<LookbookImageVariableOption> options = optionRepository.findByPromptVariableValueIdIn(valueIds);

        // 3. 모든 선택된 valueIds를 동시에 가지고 있는 combination_id 찾기 (교집합 로직)
        String targetCombinationId = options.stream()
                .collect(Collectors.groupingBy(LookbookImageVariableOption::getCombinationId))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() == valueIds.size())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new BusinessException("INVALID_VARIABLE_COMBINATION", "해당 조합의 이미지가 없습니다."));

        // 4. 찾은 조합의 이미지 URL 반환
        String imageUrl = options.stream()
                .filter(o -> o.getCombinationId().equals(targetCombinationId))
                .findFirst()
                .get().getLookbookImage().getImageUrl();

        return PreviewResponse.builder()
                .preview_image_url(imageUrl)
                .build();
    }
}