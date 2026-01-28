package com.redot.service;

import com.redot.domain.Category;
import com.redot.dto.product.metadata.CategoryResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.CategoryRepository;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("모든 활성 카테고리 목록을 조회한다.")
    void findActiveCategory_Success() {
        // given
        List<Category> testCategories = List.of(
                Category.builder()
                        .id(1L)
                        .name("풍경")
                        .orderIndex(1)
                        .isActive(true)
                        .build(),
                Category.builder()
                        .id(2L)
                        .name("인물")
                        .orderIndex(2)
                        .isActive(true)
                        .build()
        );

        given(categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc())
                .willReturn(testCategories);

        // when
        List<CategoryResponse> result = categoryService.getActiveCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getName()).isEqualTo("풍경");


        verify(categoryRepository).findAllByIsActiveTrueOrderByOrderIndexAsc();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 조회 시 예외가 발생한다")
    void findById_Fail() {
        // Given
        Long invalidId = 999L;
        given(categoryRepository.findById(invalidId)).willReturn(Optional.empty());

        // When & Then (실행 및 검증 동시 수행)
        // -> 예외가 터져야 성공입니다.
        assertThatThrownBy(() -> categoryService.getCategoryOrThrow(invalidId))
                .isInstanceOf(BusinessException.class) // BusinessException이 터져야 함
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CATEGORY_NOT_FOUND); // 에러 코드가 맞는지 확인
    }
}
