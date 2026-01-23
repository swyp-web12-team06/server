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
import com.tn.server.service.image.ImageManager;
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

import com.tn.server.dto.product.metadata.AiModelDto;
import com.tn.server.dto.product.metadata.CategoryDto;

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
    private final ImageManager imageManager;

    // 카테고리 목록 조회
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(c -> new CategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    // AI 모델 목록 조회
    public List<AiModelDto> getAiModels() {
        return aiModelRepository.findAllByIsActiveTrueOrderByOrderIndexAsc().stream()
                .map(m -> new AiModelDto(m.getId(), m.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long registerProduct(Long userId, ProductCreateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        AiModel aiModel = aiModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AI_MODEL_NOT_FOUND));

        if (request.getPrice() % 100 != 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
        }
        if (request.getPrice() < 500 || request.getPrice() > 1000) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }

        String previewImageUrl;
        if (request.getImages() != null) {
            long representativeCount = request.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsRepresentative()))
                    .count();

            if (representativeCount > 3) {
                throw new BusinessException(ErrorCode.REPRESENTATIVE_IMAGE_LIMIT_EXCEEDED);
            }

            String rawPreviewUrl = request.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPreview()))
                    .map(LookbookImageCreateDto::getImageUrl)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.PREVIEW_IMAGE_REQUIRED));
            
            previewImageUrl = imageManager.extractKey(rawPreviewUrl);
        } else {
             throw new BusinessException(ErrorCode.LOOKBOOK_IMAGE_REQUIRED);
        }

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

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.addTags(tags);
        }

        Set<String> variableNames = extractVariables(request.getMasterPrompt());
        List<PromptVariable> promptVariables = new ArrayList<>();
        
        Map<String, PromptVariableCreateDto> variableMap = request.getPromptVariables() != null
                ? request.getPromptVariables().stream().collect(Collectors.toMap(PromptVariableCreateDto::getKeyName, dto -> dto))
                : Map.of();

        for (String key : variableNames) {
            PromptVariableCreateDto detail = variableMap.get(key);
            String variableName = detail != null ? detail.getVariableName() : key;
            String description = detail != null ? detail.getDescription() : null;
            Integer orderIndex = detail != null ? detail.getOrderIndex() : null;

            PromptVariable variable = new PromptVariable(prompt, key, variableName, description, orderIndex);
            prompt.addPromptVariable(variable);
            promptVariables.add(variable);
        }

        for (LookbookImageCreateDto imgDto : request.getImages()) {
            LookbookImage lookbookImage = new LookbookImage(
                    prompt,
                    imageManager.extractKey(imgDto.getImageUrl()),
                    imgDto.getIsRepresentative()
            );
            prompt.addLookbookImage(lookbookImage);

            if (imgDto.getOptionValues() != null) {
                for (Map.Entry<String, String> entry : imgDto.getOptionValues().entrySet()) {
                    String varKey = entry.getKey();
                    String varValue = entry.getValue();

                    PromptVariable targetVar = promptVariables.stream()
                            .filter(v -> v.getKeyName().equals(varKey))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException(ErrorCode.UNDEFINED_PROMPT_VARIABLE));

                    lookbookImage.addVariableOption(targetVar, varValue);
                }
            }
        }

        return promptRepository.save(prompt).getId();
    }

    private Set<String> extractVariables(String promptText) {
        Set<String> variables = new HashSet<>();
        if (promptText == null) return variables;

        Pattern pattern = Pattern.compile("\\[([a-zA-Z0-9_]+)]");
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

        // 판매자 본인 확인
        if (!prompt.getSeller().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        String newPreviewImageUrl = prompt.getPreviewImageUrl();
        if (request.getPreviewImageId() != null) {
            newPreviewImageUrl = prompt.getLookbookImages().stream()
                    .filter(img -> img.getId().equals(request.getPreviewImageId()))
                    .map(LookbookImage::getImageUrl)
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT));
        }

        if (request.getPrice() != null) {
            if (request.getPrice() % 100 != 0) {
                throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
            }
            if (request.getPrice() < 500 || request.getPrice() > 1000) {
                throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
            }
        }

        prompt.updateInfo(
                category,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                newPreviewImageUrl
        );

        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                    .collect(Collectors.toSet());
            prompt.updateTags(tags);
        }

        if (request.getRepresentativeImageIds() != null) {
            Set<Long> requestIds = new HashSet<>(request.getRepresentativeImageIds());
            Set<Long> existingIds = prompt.getLookbookImages().stream()
                    .map(LookbookImage::getId)
                    .collect(Collectors.toSet());

            if (!existingIds.containsAll(requestIds)) {
                throw new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT);
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

        if (!prompt.getSeller().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        boolean hasPurchased = purchaseRepository.existsByPromptId(promptId);
        if (hasPurchased) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PURCHASED_ITEM);
        }

        promptRepository.softDeleteById(promptId);
    }

    public Page<ProductListResponse> getProducts(Long categoryId, Pageable pageable) {
        Page<Prompt> prompts;
        if (categoryId != null) {
            prompts = promptRepository.findAllByCategory(categoryId, pageable);
        } else {
            prompts = promptRepository.findAllWithSeller(pageable);
        }
        return prompts.map(p -> ProductListResponse.from(p, imageManager::getPublicUrl));
    }

    public Page<ProductListResponse> searchProducts(String keyword, Long categoryId, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProducts(categoryId, pageable);
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            String column = switch (property) {
                case "createdAt" -> "created_at";
                case "updatedAt" -> "updated_at";
                case "id" -> "prompt_id";
                case "price" -> "price";
                default -> "prompt_id";
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

        return prompts.map(p -> ProductListResponse.from(p, imageManager::getPublicUrl));
    }

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

        return ProductDetailResponse.from(prompt, userStatus, imageManager::getPublicUrl);
    }
}