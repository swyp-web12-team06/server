package com.redot.controller;

import java.util.List;
import java.util.Map;
import com.redot.dto.common.ApiResponse;
import com.redot.dto.library.LibrarySalesResponse;
import com.redot.dto.product.ProductCreateRequest;
import com.redot.dto.product.ProductPurchaseResponse;
import com.redot.dto.product.ProductResponse;
import com.redot.dto.product.ProductUpdateRequest;
import com.redot.dto.prompt.GenerationRequest;
import com.redot.service.GenerationService;
import com.redot.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "프롬프트(상품) API", description = "카테고리 목록 조회, AI 모델 조회, 프롬프트 등록, 수정, 상품 조회")
public class ProductController {

    private final ProductService productService;

    private final GenerationService generationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> registerProduct(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody @Valid ProductCreateRequest request
    ) {
        Long userId = Long.parseLong(user.getUsername());
        Long productId = productService.registerProduct(userId, request);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 등록되었습니다.", Map.of("promptId", productId)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateProduct(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestBody @Valid ProductUpdateRequest request
    ) {
        Long userId = Long.parseLong(user.getUsername());
        Long productId = productService.updateProduct(userId, id, request);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 수정되었습니다.", Map.of("promptId", productId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id
    ) {
        Long userId = Long.parseLong(user.getUsername());
        productService.deleteProduct(userId, id);

        return ResponseEntity.ok(ApiResponse.success("성공적으로 삭제되었습니다.", null));
    }

    // 상품 목록 조회 (검색 포함) - 상세 정보 포함
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sort
    ) {
        Long userId = (user != null) ? Long.parseLong(user.getUsername()) : null;

        Sort sortDir = switch (sort) {
            case "OLDEST" -> Sort.by("createdAt").ascending();
            case "PRICE_HIGH" -> Sort.by("price").descending();
            case "PRICE_LOW" -> Sort.by("price").ascending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortDir);

        Page<ProductResponse> result;
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = productService.searchProducts(keyword, categoryId, userId, pageable);
        } else {
            result = productService.getProducts(categoryId, userId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("상품 목록 조회에 성공했습니다", result));
    }

    // 개별 상품 조회 (상세 페이지용)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id
    ) {
        Long userId = (user != null) ? Long.parseLong(user.getUsername()) : null;
        ProductResponse response = productService.getProductDetail(id, userId);
        return ResponseEntity.ok(ApiResponse.success("상품 조회에 성공했습니다.", response));
    }

    // 구매 페이지용 상품 조회 (경량화, 모델 옵션 포함)
    @GetMapping("/{id}/purchase")
    public ResponseEntity<ApiResponse<ProductPurchaseResponse>> getProductForPurchase(
            @PathVariable Long id
    ) {
        ProductPurchaseResponse response = productService.getProductForPurchase(id);
        return ResponseEntity.ok(ApiResponse.success("구매 페이지 상품 조회에 성공했습니다.", response));
    }

    @PostMapping("/{id}/estimate")
    public ResponseEntity<ApiResponse<Integer>> estimatePrice(
            @PathVariable("id") Long promptId,
            @RequestBody GenerationRequest request
    ) {
        int estimatedPrice = generationService.getEstimatedPrice(promptId, request);

        return ResponseEntity.ok(ApiResponse.success("공통 메시지(예: 조회 성공)", estimatedPrice));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<LibrarySalesResponse>>> getUserProducts(
            @PathVariable Long userId
    ) {
        log.info(">>> [ProductController] 타인 판매 목록 조회. userId: {}", userId);

        List<LibrarySalesResponse> response = productService.getUserProductList(userId);
        return ResponseEntity.ok(ApiResponse.success("판매 목록 조회 성공", response));
    }
}