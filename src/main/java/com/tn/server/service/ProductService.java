package com.tn.server.service;

import com.tn.server.domain.AiModel;
import com.tn.server.domain.Category;
import com.tn.server.domain.Prompt;
import com.tn.server.domain.user.User;
import com.tn.server.dto.product.ProductCreateRequest;
import com.tn.server.dto.product.ProductDetailResponse;
import com.tn.server.dto.product.ProductListResponse;
import com.tn.server.repository.AiModelRepository;
import com.tn.server.repository.CategoryRepository;
import com.tn.server.repository.PromptRepository;
import com.tn.server.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final PromptRepository promptRepository;
    private final CategoryRepository categoryRepository;
    private final AiModelRepository aiModelRepository;
    private final UserRepository userRepository;

    @Transactional // 쓰기 작업이므로 readOnly 미사용
    public Long registerProduct(Long userId, ProductCreateRequest request) {

        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리가 존재하지 않습니다."));

        // AI 모델 존재 여부 확인
        AiModel aiModel = aiModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("해당 AI 모델이 존재하지 않습니다."));

        // 엔티티 생성
        Prompt prompt = Prompt.builder()
                .seller(user)
                .category(category)
                .aiModel(aiModel)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .masterPrompt(request.getMasterPrompt())
                .previewImageUrl(request.getPreviewImageUrl())
                .build();

        // DB 저장 및 ID 반환
        return promptRepository.save(prompt).getId();
    }

    // 상품 목록 조회
    @Transactional(readOnly = true) // 읽기 전용
    public Page<ProductListResponse> getProducts(Pageable pageable) {
        // DB에서 페이지 단위로 가져온 뒤 DTO로 변환(map)
        return promptRepository.findAll(pageable)
                .map(ProductListResponse::from); // DTO static 변환 사용
    }

    // 상품 상세 조회
    @Transactional(readOnly = true) // 읽기 전용
    public ProductDetailResponse getProductDetail(Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        return ProductDetailResponse.from(prompt);
    }
}
