package com.redot.service;

import com.redot.domain.AiModel;
import com.redot.dto.product.metadata.AiModelResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.AiModelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AiModelServiceTest {

    @Mock
    private AiModelRepository aiModelRepository;

    @InjectMocks
    private AiModelService aiModelService;

    @Test
    @DisplayName("모든 활성 AI 모델을 조회한다.")
    void findActiveAiModel_Success() {
        // given
        List<AiModel> testAiModelList = List.of(
                AiModel.builder()
                        .id(1L)
                        .name("grok-imagine/text-to-image")
                        .orderIndex(1)
                        .isActive(true)
                        .build(),
                AiModel.builder()
                        .id(2L)
                        .name("nano-banana-pro")
                        .orderIndex(2)
                        .isActive(true)
                        .build()
        );

        given(aiModelRepository.findAllByIsActiveTrueOrderByOrderIndexAsc())
                .willReturn(testAiModelList);

        // when
        List<AiModelResponse> result = aiModelService.getActiveAiModels();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("grok-imagine/text-to-image");
    }

    @Test
    @DisplayName("존재하지 않는 AI 모델을 조회할 시 예외가 발생한다.")
    void getModelOrThrow_Fail() {
        // given
        Long invalidId = 100L;
        given(aiModelRepository.findById(invalidId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> aiModelService.getModelOrThrow(invalidId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AI_MODEL_NOT_FOUND);
    }
}
