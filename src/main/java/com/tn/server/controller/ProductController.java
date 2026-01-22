package com.tn.server.controller;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import com.tn.server.common.response.ApiResponse;
import com.tn.server.dto.product.ProductCreateRequest;
import com.tn.server.dto.product.ProductDetailResponse;
import com.tn.server.dto.product.ProductListResponse;
import com.tn.server.dto.product.ProductUpdateRequest;
import com.tn.server.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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

    // 상품 목록 조회 (검색 포함)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductListResponse>>> getProducts(
            @RequestParam(required = false) String keyword, // 검색 키워드 (제목, 판매자명)
            @RequestParam(required = false) Long categoryId, // 카테고리 필터링
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sort // 정렬 조건
    ) {
        // 정렬 기준 설정
        Sort sortDir = switch (sort) {
            case "OLDEST" -> Sort.by("createdAt").ascending();
            case "PRICE_HIGH" -> Sort.by("price").descending();
            case "PRICE_LOW" -> Sort.by("price").ascending();
            default -> Sort.by("createdAt").descending(); // LATEST
        };

        Pageable pageable = PageRequest.of(page, size, sortDir);

        // 키워드가 있으면 검색, 없으면 전체 목록
        Page<ProductListResponse> result;
        if (keyword != null && !keyword.trim().isEmpty()) {
            result = productService.searchProducts(keyword, categoryId, pageable);
        } else {
            result = productService.getProducts(categoryId, pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // 개별 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDetailResponse>> getProductDetail(@PathVariable Long id) {
        ProductDetailResponse response = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
