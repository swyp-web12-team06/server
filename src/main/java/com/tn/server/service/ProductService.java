package com.tn.server.service;

import com.tn.server.domain.AiModel;
import com.tn.server.domain.Category;
import com.tn.server.domain.LookbookImage;
import com.tn.server.domain.Prompt;
import com.tn.server.domain.PromptVariable;
import com.tn.server.domain.Tag;
import com.tn.server.domain.user.User;
import com.tn.server.dto.product.LookbookImageCreateDto;
import com.tn.server.dto.product.ProductCreateRequest;
import com.tn.server.dto.product.ProductDetailResponse;
import com.tn.server.dto.product.ProductListResponse;
import com.tn.server.dto.product.ProductUpdateRequest;
import com.tn.server.dto.product.PromptVariableCreateDto;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import com.tn.server.repository.AiModelRepository;
import com.tn.server.repository.CategoryRepository;
import com.tn.server.repository.PromptRepository;
import com.tn.server.repository.PurchaseRepository;
import com.tn.server.repository.TagRepository;
import com.tn.server.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tn.server.dto.product.UserProductStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final PurchaseRepository purchaseRepository;

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

        // 가격 단위 검증 (100원 단위 및 범위)
        if (request.getPrice() % 100 != 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
        }
        if (request.getPrice() < 500 || request.getPrice() > 1000) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }

        // 프리뷰 이미지 URL 추출 (이미지 리스트 중 isPreview가 true인 항목)
        String previewImageUrl = null;
        if (request.getImages() != null) {
            // 대표 이미지 개수 검증 (최대 3개)
            long representativeCount = request.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsRepresentative()))
                    .count();

            if (representativeCount > 3) {
                throw new BusinessException(ErrorCode.REPRESENTATIVE_IMAGE_LIMIT_EXCEEDED); // 대표 이미지는 최대 3개
            }

            previewImageUrl = request.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPreview()))
                    .map(LookbookImageCreateDto::getImageUrl)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.PREVIEW_IMAGE_REQUIRED)); // 프리뷰 이미지 미지정
        } else {
             throw new BusinessException(ErrorCode.LOOKBOOK_IMAGE_REQUIRED); // 이미지가 없음
        }

        // 엔티티 생성
        Prompt prompt = Prompt.builder()
                .seller(user)
                .category(category)
                .aiModel(aiModel)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .masterPrompt(request.getMasterPrompt())
                .previewImageUrl(previewImageUrl)
                .build();

        // 태그 처리
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.addTags(tags);
        }

        // 프롬프트 변수 파싱 및 저장
        Set<String> variableNames = extractVariables(request.getMasterPrompt());
        List<PromptVariable> promptVariables = new ArrayList<>();
        
        // 요청된 변수 설정 맵핑 (Key -> DTO)
        Map<String, PromptVariableCreateDto> variableMap = request.getPromptVariables() != null
                ? request.getPromptVariables().stream().collect(Collectors.toMap(PromptVariableCreateDto::getKeyName, dto -> dto))
                : Map.of();

        for (String key : variableNames) {
            PromptVariableCreateDto detail = variableMap.get(key);
            String variableName = detail != null ? detail.getVariableName() : key; // 없으면 키값을 이름으로 사용
            String description = detail != null ? detail.getDescription() : null;
            Integer orderIndex = detail != null ? detail.getOrderIndex() : null;

            PromptVariable variable = new PromptVariable(prompt, key, variableName, description, orderIndex);
            prompt.addPromptVariable(variable);
            promptVariables.add(variable);
        }

        // 룩북 이미지 처리
        // Validation handled by DTO @Size
        for (LookbookImageCreateDto imgDto : request.getImages()) {
            LookbookImage lookbookImage = new LookbookImage(
                    prompt,
                    imgDto.getImageUrl(),
                    imgDto.getIsRepresentative()
            );
            prompt.addLookbookImage(lookbookImage);

            // 이미지별 변수 옵션 처리
            if (imgDto.getOptionValues() != null) {
                for (Map.Entry<String, String> entry : imgDto.getOptionValues().entrySet()) {
                    String varKey = entry.getKey();
                    String varValue = entry.getValue();

                    // 프롬프트에 정의된 변수인지 확인
                    PromptVariable targetVar = promptVariables.stream()
                            .filter(v -> v.getKeyName().equals(varKey))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(ErrorCode.UNDEFINED_PROMPT_VARIABLE)); // 정의되지 않은 변수 사용

                    lookbookImage.addVariableOption(targetVar, varValue);
                }
            }
        }

        // DB 저장 및 ID 반환
        return promptRepository.save(prompt).getId();
    }

    private Set<String> extractVariables(String promptText) {
        Set<String> variables = new HashSet<>();
        if (promptText == null) return variables;

        Pattern pattern = Pattern.compile("\\[([a-zA-Z0-9_]+)\\]");
        Matcher matcher = pattern.matcher(promptText);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    @Transactional
    public Long updateProduct(Long userId, Long productId, ProductUpdateRequest request) {
        Prompt prompt = promptRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        // Preview Image ID가 제공된 경우 URL 조회
        String newPreviewImageUrl = prompt.getPreviewImageUrl();
        if (request.getPreviewImageId() != null) {
            newPreviewImageUrl = prompt.getLookbookImages().stream()
                    .filter(img -> img.getId().equals(request.getPreviewImageId()))
                    .map(LookbookImage::getImageUrl)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT)); // 해당 상품의 이미지가 아님
        }

        // 가격 단위 검증 (100원 단위 및 범위)
        if (request.getPrice() != null) {
            if (request.getPrice() % 100 != 0) {
                throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
            }
            if (request.getPrice() < 500 || request.getPrice() > 1000) {
                throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
            }
        }

        // 기본 정보 수정 (프롬프트, AI 모델, 이미지는 수정 불가)
        prompt.updateInfo(
                category,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                newPreviewImageUrl
        );

        // 태그 수정
        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.updateTags(tags);
        }

        // 대표 이미지 선택 업데이트
        if (request.getRepresentativeImageIds() != null) {
            Set<Long> requestIds = new HashSet<>(request.getRepresentativeImageIds());
            
            // 본인 소유의 이미지인지 검증을 위해 기존 이미지 ID 수집
            Set<Long> existingIds = prompt.getLookbookImages().stream()
                    .map(LookbookImage::getId)
                    .collect(Collectors.toSet());

            if (!existingIds.containsAll(requestIds)) {
                throw new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT); // 존재하지 않거나 다른 상품의 이미지 ID 포함
            }

            for (LookbookImage image : prompt.getLookbookImages()) {
                image.setRepresentative(requestIds.contains(image.getId()));
            }
        }

        return prompt.getId();
    }

    @Transactional
    public void deleteProduct(Long userId, Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        // 1. 판매자 본인 확인
        if (!prompt.getSeller().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 2. 판매 이력 확인 (한 번이라도 팔렸으면 삭제 불가)
        boolean hasPurchased = purchaseRepository.existsByPromptId(promptId);
        if (hasPurchased) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PURCHASED_ITEM);
        }

        // 3. Soft Delete 수행
        promptRepository.softDeleteById(promptId);
    }

    // 상품 목록 조회
    @Transactional(readOnly = true) // 읽기 전용
    public Page<ProductListResponse> getProducts(Long categoryId, Pageable pageable) {
        Page<Prompt> prompts;
        if (categoryId != null) {
            prompts = promptRepository.findAllByCategory(categoryId, pageable);
        } else {
            prompts = promptRepository.findAllWithSeller(pageable);
        }
        return prompts.map(ProductListResponse::from);
    }

    // 상품 검색 (제목, 판매자명)
    @Transactional(readOnly = true)
    public Page<ProductListResponse> searchProducts(String keyword, Long categoryId, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // 키워드가 없으면 전체 목록 반환
            return getProducts(categoryId, pageable);
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

        Page<Prompt> prompts;
        if (categoryId != null) {
            prompts = promptRepository.searchByKeywordAndCategory(keyword.trim(), categoryId, nativePageable);
        } else {
            prompts = promptRepository.searchByKeywordWithSeller(keyword.trim(), nativePageable);
        }

        return prompts.map(ProductListResponse::from);
    }

    // 상품 상세 조회
    @Transactional(readOnly = true) // 읽기 전용
    public ProductDetailResponse getProductDetail(Long promptId, Long userId) {
        Prompt prompt = promptRepository.findByIdWithDetails(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        UserProductStatus userStatus = UserProductStatus.GUEST;

        if (userId != null) {
            if (prompt.getSeller().getId().equals(userId)) {
                userStatus = UserProductStatus.OWNER;
            } else if (purchaseRepository.existsByUserIdAndPromptId(userId, promptId)) {
                userStatus = UserProductStatus.PURCHASED;
            } else {
                userStatus = UserProductStatus.NOT_PURCHASED;
            }
        }

        return ProductDetailResponse.from(prompt, userStatus);
    }
}
