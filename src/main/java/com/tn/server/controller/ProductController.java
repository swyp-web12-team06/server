package com.tn.server.controller;

import com.tn.server.dto.product.ProductCreateRequest;
import com.tn.server.dto.product.ProductDetailResponse;
import com.tn.server.dto.product.ProductListResponse;
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
    public ResponseEntity<Long> registerProduct(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody @Valid ProductCreateRequest request
    ) {
        Long userId = Long.parseLong(user.getUsername());
        Long productId = productService.registerProduct(userId, request);

        return ResponseEntity.ok(productId);
    }

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<Page<ProductListResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") String sort // 정렬 조건
    ) {
        // 정렬 기준 설정: ID 역순(최신순)
        Sort sortDir = Sort.by("id").descending();
        Pageable pageable = PageRequest.of(page, size, sortDir);

        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    // 개별 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
        ProductDetailResponse response = productService.getProductDetail(id);
        return ResponseEntity.ok(response);
    }
}
