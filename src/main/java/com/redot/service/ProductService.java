package com.redot.service;

import com.redot.domain.*;
import com.redot.domain.user.User;
import com.redot.dto.product.LookbookImageCreateDto;
import com.redot.dto.product.ProductCreateRequest;
import com.redot.dto.product.ProductResponse;
import com.redot.dto.product.ProductUpdateRequest;
import com.redot.dto.product.PromptVariableCreateDto;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import com.redot.repository.PromptRepository;
import com.redot.repository.PurchaseRepository;
import com.redot.service.image.ImageManager;
import com.redot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.redot.dto.product.UserProductStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserService userService;
    private final PromptRepository promptRepository;
    private final PurchaseRepository purchaseRepository;
    private final ImageManager imageManager;
    private final CategoryService categoryService;
    private final AiModelService aiModelService;
    private final TagService tagService;
    private static final int CASH_TO_CREDIT_RATE = 100;
    private static final int MIN_PRICE = 500;
    private static final int MAX_PRICE = 1000;
    private static final int PRICE_UNIT = CASH_TO_CREDIT_RATE;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Transactional
    public Long registerProduct(Long userId, ProductCreateRequest request) {
        // 단순 조회 및 검증 로직 위임
        User user = userService.findActiveUser(userId);
        Category category = categoryService.getCategoryOrThrow(request.getCategoryId());
        AiModel aiModel = aiModelService.getModelOrThrow(request.getModelId());

        // 가격 정책 검증 (도메인 로직)
        validatePricePolicy(request.getPrice());

        // 이미지 검증 및 프리뷰 추출
        String previewImageUrl = validateAndGetPreviewKey(request.getImages());

        // 저장 전 프롬프트 엔티티 생성
        Prompt prompt = Prompt.builder()
                .seller(user)
                .category(category)
                .aiModel(aiModel)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice() / CASH_TO_CREDIT_RATE) // 단위 변환
                .masterPrompt(request.getMasterPrompt())
                .previewImageUrl(previewImageUrl)
                .status(PromptStatus.APPROVED)
                .isDeleted(false)
                .build();

        // 태그 처리
        Set<String> uniqueTagNames = validateAndSanitizeTags(request.getTags());

        // 태그 서비스: 이름 Set으로 태그 엔티티 Set 찾기f
        Set<Tag> tags = tagService.findOrCreateTags(uniqueTagNames);
        prompt.addTags(tags);

        // 변수 파싱 및 등록 (promptVariables 리스트 반환)
        List<PromptVariable> variables = processPromptVariables(prompt, request.getMasterPrompt(), request.getPromptVariables());

        // 룩북 이미지 및 변수 옵션 매핑
        processLookbookImages(prompt, variables, request.getImages());

        // 최종 저장 (Cascade 설정으로 prompt만 저장해도 자식들 일괄 저장)
        return promptRepository.save(prompt).getId();
    }

    // 변수 파싱 및 엔티티 생성 로직
    private List<PromptVariable> processPromptVariables(Prompt prompt, String masterPrompt, List<PromptVariableCreateDto> variableDtos) {
        // 본문에서 [key] 추출
        Set<String> extractedKeys = extractVariables(masterPrompt);

        // DTO를 Map으로 변환 (빠른 조회를 위해)
        Map<String, PromptVariableCreateDto> dtoMap = variableDtos != null
                ? variableDtos.stream().collect(Collectors.toMap(PromptVariableCreateDto::getKeyName, Function.identity()))
                : Map.of();

        List<PromptVariable> promptVariables = new ArrayList<>();

        // 추출된 키를 기반으로 엔티티 생성
        for (String key : extractedKeys) {
            String cleanKey = key.trim(); // ★ 공백 제거 필수
            PromptVariableCreateDto detail = dtoMap.get(cleanKey);

            PromptVariable variable = PromptVariable.builder()
                    .prompt(prompt)
                    .keyName(cleanKey)
                    .variableName(detail != null ? detail.getVariableName() : cleanKey)
                    .description(detail != null ? detail.getDescription() : null)
                    .orderIndex(detail != null ? detail.getOrderIndex() : null)
                    .build();

            prompt.addPromptVariable(variable); // 연관관계 편의 메서드
            promptVariables.add(variable);
        }
        return promptVariables;
    }

    // 룩북 이미지 및 옵션 매핑 로직
    private void processLookbookImages(Prompt prompt, List<PromptVariable> variables, List<LookbookImageCreateDto> imageDtos) {
        if (imageDtos == null) return;

        // 성능 최적화: 리스트 루프 대신 Map으로 변환 (Key -> PromptVariable)
        Map<String, PromptVariable> variableMap = variables.stream()
                .collect(Collectors.toMap(PromptVariable::getKeyName, v -> v));

        for (LookbookImageCreateDto imgDto : imageDtos) {
            // 이미지 엔티티 생성
            LookbookImage lookbookImage = new LookbookImage(
                    prompt,
                    imageManager.extractKey(imgDto.getImageUrl()),
                    imgDto.getIsRepresentative(),
                    imgDto.getIsPreview()
            );
            prompt.addLookbookImage(lookbookImage);

            // 옵션 값 매핑
            if (imgDto.getOptionValues() != null) {
                imgDto.getOptionValues().forEach((key, value) -> {
                    String cleanKey = key.trim();

                    // 안전한 조회: Map에서 찾기
                    PromptVariable targetVar = variableMap.get(cleanKey);

                    if (targetVar != null) {
                        lookbookImage.addVariableOption(targetVar, value);
                    } else {
                        throw new BusinessException(ErrorCode.UNDEFINED_PROMPT_VARIABLE);
                    }
                });
            }
        }
    }

    // 가격 정책 검증 메서드
    private void validatePricePolicy(Integer price) {
        // 1. 단위 검증 (DTO가 못 잡는 부분 - 필수!)
        if (price % PRICE_UNIT != 0) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_UNIT);
        }

        // 2. 범위 검증 (DTO와 이중 체크 - 안전장치)
        if (price < MIN_PRICE || price > MAX_PRICE) {
            throw new BusinessException(ErrorCode.INVALID_PRICE_RANGE);
        }
    }

    // 태그 처리 로직
    private Set<String> validateAndSanitizeTags(List<String> rawTags) {
        if (rawTags == null || rawTags.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_TAG_COUNT);
        }

        // 중복 제거 & 공백 제거
        Set<String> uniqueTags = rawTags.stream()
                .filter(tag -> tag != null && !tag.trim().isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());

        // 개수 검증 (2~5개)
        if (uniqueTags.size() < 2 || uniqueTags.size() > 5) {
            throw new BusinessException(ErrorCode.INVALID_TAG_COUNT);
        }

        // 각 태그별 길이 및 형식 검증
        for (String tag : uniqueTags) {
            // 길이 검증 (2~12자)
            if (tag.length() < 2 || tag.length() > 12) {
                throw new BusinessException(ErrorCode.INVALID_TAG_LENGTH);
            }

            // 형식 검증 (한글, 영문, 숫자만 허용)
            if (!tag.matches("^[가-힣a-zA-Z0-9]+$")) {
                throw new BusinessException(ErrorCode.INVALID_TAG_FORMAT);
            }
        }

        return uniqueTags;
    }

    private String validateAndGetPreviewKey(List<LookbookImageCreateDto> images) {
        if (images == null || images.isEmpty()) {
            throw new BusinessException(ErrorCode.LOOKBOOK_IMAGE_REQUIRED);
        }

        // 1. 대표 이미지 개수 검증 (1~3개)
        long representativeCount = images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsRepresentative()))
                .count();

        if (representativeCount < 1 || representativeCount > 3) {
            throw new BusinessException(ErrorCode.INVALID_REPRESENTATIVE_IMAGE_COUNT); // "대표 이미지는 1장 이상 3장 이하이어야 합니다"
        }

        // 프리뷰 이미지 개수 검증 (정확히 1개여야 함)
        List<LookbookImageCreateDto> previewImages = images.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPreview()))
                .toList();

        if (previewImages.size() != 1) {
            // "프리뷰 이미지는 반드시 1장 지정해야 합니다." (0개거나 2개 이상이면 에러)
            throw new BusinessException(ErrorCode.INVALID_PREVIEW_IMAGE_COUNT);
        }

        LookbookImageCreateDto previewImage = previewImages.getFirst();

        // 프리뷰로 지정된 이미지는 반드시 '대표 이미지'여야 함
        if (!Boolean.TRUE.equals(previewImage.getIsRepresentative())) {
            throw new BusinessException(ErrorCode.PREVIEW_MUST_BE_REPRESENTATIVE);
        }

        // 키 추출
        return imageManager.extractKey(previewImage.getImageUrl());
    }

    // 프롬프트 본문에서 [변수] 추출 로직
    private Set<String> extractVariables(String masterPrompt) {
        // null 방어
        if (masterPrompt == null || masterPrompt.isBlank()) {
            return new HashSet<>(); // 수정 가능한 빈 Set 반환 (Set.of()보다 안전)
        }

        Set<String> variables = new HashSet<>();

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\[([a-zA-Z0-9_\\s\\-]+)\\]");
        java.util.regex.Matcher matcher = pattern.matcher(masterPrompt);

        while (matcher.find()) {
            // 앞뒤 공백 제거 후 저장
            String extracted = matcher.group(1).trim();
            if (!extracted.isEmpty()) {
                variables.add(extracted);
            }
        }
        return variables;
    }

    @Transactional
    public Long updateProduct(Long userId, Long productId, ProductUpdateRequest request) {
        Prompt prompt = promptRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        // 판매자 본인 확인
        validateProductOwnership(prompt, userId);

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryService.getCategoryOrThrow(request.getCategoryId());
        }

        String newPreviewImageUrl = prompt.getPreviewImageUrl();

        // 1. 대표 이미지 변경 요청이 들어온 경우
        if (request.getRepresentativeImageIds() != null) {
            Set<Long> requestIds = new HashSet<>(request.getRepresentativeImageIds());

            // 개수 검증
            if (requestIds.isEmpty() || requestIds.size() > 3) {
                throw new BusinessException(ErrorCode.INVALID_REPRESENTATIVE_IMAGE_COUNT);
            }

            // 내 이미지인지 확인
            Set<Long> existingIds = prompt.getLookbookImages().stream()
                    .map(LookbookImage::getId)
                    .collect(Collectors.toSet());

            if (!existingIds.containsAll(requestIds)) {
                throw new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT);
            }

            // 상태 업데이트
            for (LookbookImage image : prompt.getLookbookImages()) {
                image.setRepresentative(requestIds.contains(image.getId()));
            }

            if (request.getPreviewImageId() == null) {
                // 현재 프리뷰 URL에 해당하는 이미지 ID 찾기
                LookbookImage currentPreviewImage = prompt.getLookbookImages().stream()
                        .filter(img -> img.getImageUrl().equals(prompt.getPreviewImageUrl()))
                        .findFirst()
                        .orElse(null); // 혹시 모를 데이터 불일치 대비

                // 기존 프리뷰 이미지가 존재하는데, 이번 요청된 대표 이미지 목록(requestIds)에 없다면?
                if (currentPreviewImage != null && !requestIds.contains(currentPreviewImage.getId())) {
                    // "기존 프리뷰가 대표 이미지에서 해제되었습니다. 새로운 프리뷰 이미지를 지정해야 합니다."
                    throw new BusinessException(ErrorCode.PREVIEW_MUST_BE_REPRESENTATIVE);
                }
            }
        }

        // 2. 프리뷰 이미지 변경 요청이 들어온 경우
        if (request.getPreviewImageId() != null) {
            LookbookImage targetImage = prompt.getLookbookImages().stream()
                    .filter(img -> img.getId().equals(request.getPreviewImageId()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_BELONG_TO_PRODUCT));

            // 위에서 상태 업데이트(setRepresentative)를 먼저 했기 때문에, 여기서 바뀐 상태(true/false)로 정확히 체크됨
            if (!Boolean.TRUE.equals(targetImage.getIsRepresentative())) {
                throw new BusinessException(ErrorCode.PREVIEW_MUST_BE_REPRESENTATIVE);
            }

            newPreviewImageUrl = targetImage.getImageUrl();
        }

        if (request.getPrice() != null) {
            validatePricePolicy(request.getPrice());
        }

        prompt.updateInfo(
                category,
                request.getTitle(),
                request.getDescription(),
                (request.getPrice() != null) ? request.getPrice() / CASH_TO_CREDIT_RATE : null, // 원 단위 -> 크레딧 변환
                newPreviewImageUrl
        );

        if (request.getTags() != null) {
            // 검증&서비스 로직 재사용
            Set<String> uniqueTagNames = validateAndSanitizeTags(request.getTags());
            Set<Tag> tags = tagService.findOrCreateTags(uniqueTagNames);

            prompt.updateTags(tags);
        }

        return prompt.getId();
    }

    @Transactional
    public void deleteProduct(Long userId, Long promptId) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        validateProductOwnership(prompt, userId);

        boolean hasPurchased = purchaseRepository.existsByPromptId(promptId);
        if (hasPurchased) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_PURCHASED_ITEM);
        }

        promptRepository.softDeleteById(promptId);
    }

    private void validateProductOwnership(Prompt prompt, Long userId) {
        if (!prompt.getSeller().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    public Page<ProductResponse> getProducts(Long categoryId, Long userId, Pageable pageable) {
        Page<Prompt> prompts;
        if (categoryId != null) {
            prompts = promptRepository.findAllByCategoryWithDetails(categoryId, pageable);
        } else {
            prompts = promptRepository.findAllWithDetails(pageable);
        }
        return prompts.map(p -> toProductResponse(p, userId));
    }

    public Page<ProductResponse> searchProducts(String keyword, Long categoryId, Long userId, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProducts(categoryId, userId, pageable);
        }

        Page<Prompt> prompts;

        boolean isLocal = activeProfile.equals("local");

        if (categoryId != null) {
            if (isLocal) {
                prompts = promptRepository.searchByKeywordAndCategoryBasic(keyword, categoryId, pageable);
            } else {
                prompts = promptRepository.searchByKeywordAndCategoryFullText(keyword, categoryId, pageable);
            }
        } else {
            if (isLocal) {
                prompts = promptRepository.searchByKeywordBasic(keyword, pageable);
            } else {
                prompts = promptRepository.searchByKeywordFullText(keyword, pageable);
            }
        }

        return prompts.map(p -> toProductResponse(p, userId));
    }

    public ProductResponse getProductDetail(Long promptId, Long userId) {
        Prompt prompt = promptRepository.findByIdWithDetails(promptId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMPT_NOT_FOUND));

        return toProductResponse(prompt, userId);
    }

    private ProductResponse toProductResponse(Prompt prompt, Long userId) {
        UserProductStatus userStatus = determineUserStatus(prompt, userId);

        return ProductResponse.from(prompt, userStatus, imageManager::getPublicUrl);
    }

    private UserProductStatus determineUserStatus(Prompt prompt, Long userId) {
        if (userId == null) {
            return UserProductStatus.GUEST;
        }
        if (prompt.getSeller().getId().equals(userId)) {
            return UserProductStatus.OWNER;
        }
        if (purchaseRepository.existsByUserIdAndPromptId(userId, prompt.getId())) {
            return UserProductStatus.PURCHASED;
        }
        return UserProductStatus.NOT_PURCHASED;
    }
}