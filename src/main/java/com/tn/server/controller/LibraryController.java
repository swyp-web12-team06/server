package com.tn.server.controller;

import com.tn.server.common.response.ApiResponse;
import com.tn.server.dto.library.LibraryResponse;
import com.tn.server.dto.library.LibrarySalesResponse;
import com.tn.server.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/me/library")
public class LibraryController {

    private final LibraryService libraryService;

    /**
     * [구매 목록 조회]
     * GET /user/me/library/purchases
     * 명세서 규격에 맞춘 공통 응답 포맷(code, message, data) 적용
     */
    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<LibraryResponse>>> getPurchases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId // 실제 운영 환경에서는 세션/토큰에서 가져오나 현재는 파라미터로 처리
    ) {
        List<LibraryResponse> data = libraryService.getMyPurchases(userId);

        return ResponseEntity.ok(ApiResponse.<List<LibraryResponse>>builder()
                .code("SUCCESS")
                .message("조회 성공")
                .data(data)
                .build());
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<LibrarySalesResponse>>> getSales(@RequestParam Long userId) {
        List<LibrarySalesResponse> data = libraryService.getMySalesList(userId);

        return ResponseEntity.ok(ApiResponse.<List<LibrarySalesResponse>>builder()
                .code("SUCCESS")
                .message("판매 내역 조회 성공")
                .data(data)
                .build());
    }
}