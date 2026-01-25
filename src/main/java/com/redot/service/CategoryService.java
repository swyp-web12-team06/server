package com.redot.service;

import com.redot.domain.Category;
import com.redot.dto.product.metadata.CategoryResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.CategoryRepository;
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
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    // 카테고리 목록 조회 (관리자용)
    public Category getCategoryOrThrow(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // 비활성화된 카테고리 제외
        if (!category.getIsActive()) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        return category;
    }
}
