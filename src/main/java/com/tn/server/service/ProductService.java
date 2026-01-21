package com.tn.server.service;

import com.tn.server.domain.AiModel;
import com.tn.server.domain.Category;
import com.tn.server.domain.Prompt;
import com.tn.server.domain.Tag;
import com.tn.server.domain.user.User;
import com.tn.server.dto.product.ProductCreateRequest;
import com.tn.server.dto.product.ProductDetailResponse;
import com.tn.server.dto.product.ProductListResponse;
import com.tn.server.dto.product.ProductUpdateRequest;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.AiModelRepository;
import com.tn.server.repository.CategoryRepository;
import com.tn.server.repository.PromptRepository;
import com.tn.server.repository.TagRepository;
import com.tn.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final PromptRepository promptRepository;
    private final CategoryRepository categoryRepository;
    private final AiModelRepository aiModelRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional // 쓰기 작업이므로 readOnly 미사용
    public Long registerProduct(Long userId, ProductCreateRequest request) {

        // 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        // AI 모델 존재 여부 확인
        AiModel aiModel = aiModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND));

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

        // 태그 처리
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.addTags(tags);
        }

        // DB 저장 및 ID 반환
        return promptRepository.save(prompt).getId();
    }

    @Transactional
    public Long updateProduct(Long userId, Long productId, ProductUpdateRequest request) {
        Prompt prompt = promptRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        // 판매자 본인 확인
        if (!prompt.getSeller().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        AiModel aiModel = aiModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND));

        // 기본 정보 수정
        prompt.update(
                category,
                aiModel,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getMasterPrompt(),
                request.getPreviewImageUrl() != null ? request.getPreviewImageUrl() : prompt.getPreviewImageUrl()
        );

        // 태그 수정
        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.updateTags(tags);
        }

        return prompt.getId();
    }

    // 상품 목록 조회
    @Transactional(readOnly = true) // 읽기 전용
    public Page<ProductListResponse> getProducts(Pageable pageable) {
        // DB에서 페이지 단위로 가져온 뒤 DTO로 변환(map)
        // Fetch Join으로 N+1 문제 해결
        return promptRepository.findAllWithSeller(pageable)
                .map(ProductListResponse::from); // DTO static 변환 사용
    }

    // 상품 검색 (제목, 판매자명)
    @Transactional(readOnly = true)
    public Page<ProductListResponse> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 키워드가 없으면 전체 목록 반환
            return getProducts(pageable);
        }

        // Native Query 정렬 필드 매핑 (Entity Field -> DB Column)
        // 예: createdAt -> created_at, id -> prompt_id
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            String column = switch (property) {
                case "createdAt" -> "created_at";
                case "updatedAt" -> "updated_at";
                case "id" -> "prompt_id";
                case "price" -> "price";
                default -> "prompt_id"; // 기본값
            };
            orders.add(new Sort.Order(order.getDirection(), column));
        }

        Pageable nativePageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(orders)
        );

        return promptRepository.searchByKeywordWithSeller(keyword.trim(), nativePageable)
                .map(ProductListResponse::from);
    }

    // 상품 상세 조회
    @Transactional(readOnly = true) // 읽기 전용
    public ProductDetailResponse getProductDetail(Long promptId) {
        Prompt prompt = promptRepository.findByIdWithDetails(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        return ProductDetailResponse.from(prompt);
    }
}
