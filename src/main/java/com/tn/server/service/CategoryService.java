package com.tn.server.service;

import com.tn.server.domain.Category;
import com.tn.server.dto.product.metadata.CategoryDto;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 목록 조회 (유저용)
    public List<CategoryDto> getActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    // 카테고리 목록 조회 (관리자용)
    public Category getCategoryOrThrow(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // ★ [추가] 비활성화된 카테고리는 "없는 것"으로 간주!
        if (!category.getIsActive()) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return category;
    }
}
