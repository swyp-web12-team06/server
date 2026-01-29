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
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AiModelServiceTest {

    @Mock
    private AiModelRepository aiModelRepository;

    @InjectMocks
    private AiModelService aiModelService;

    // 테스트용 엔티티 생성 헬퍼 메서드
    private <T> T createEntity(Class<T> clazz) {
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("엔티티 생성 실패: " + clazz.getName(), e);
        }
    }

    @Test
    @DisplayName("모든 활성 AI 모델을 조회한다.")
    void findActiveAiModel_Success() {
        // given
        AiModel aiModel1 = createEntity(AiModel.class);
        setField(aiModel1, "id", 1L);
        setField(aiModel1, "name", "grok-imagine/text-to-image");
        setField(aiModel1, "orderIndex", 1);
        setField(aiModel1, "isActive", true);

        AiModel aiModel2 = createEntity(AiModel.class);
        setField(aiModel2, "id", 2L);
        setField(aiModel2, "name", "nano-banana-pro");
        setField(aiModel2, "orderIndex", 2);
        setField(aiModel2, "isActive", true);

        List<AiModel> testAiModelList = List.of(aiModel1, aiModel2);

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
