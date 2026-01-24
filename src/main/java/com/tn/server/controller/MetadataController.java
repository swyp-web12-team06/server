package com.tn.server.controller;

import com.tn.server.dto.common.ApiResponse;
import com.tn.server.dto.product.metadata.AiModelDto;
import com.tn.server.dto.product.metadata.CategoryDto;
import com.tn.server.service.AiModelService;
import com.tn.server.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/metadata")
@RequiredArgsConstructor
@Tag(name = "메타데이터 API", description = "카테고리, AI 모델 목록 등 공통 기준 정보 조회")
public class MetadataController {

    private final CategoryService categoryService;
    private final AiModelService aiModelService;

    @GetMapping("/categories")
    @Operation(summary = "카테고리 목록 조회", description = "상품 등록/검색 시 사용되는 카테고리 전체 목록을 반환합니다.")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success("카테고리 조회에 성공했습니다.", categoryService.getActiveCategories()));
    }

    @GetMapping("/ai-models")
    @Operation(summary = "AI 모델 목록 조회", description = "지원하는 AI 모델(Midjourney, Stable Diffusion 등) 목록을 반환합니다.")
    public ResponseEntity<ApiResponse<List<AiModelDto>>> getAiModels() {
        return ResponseEntity.ok(ApiResponse.success("", aiModelService.getActiveAiModels()));
    }
}