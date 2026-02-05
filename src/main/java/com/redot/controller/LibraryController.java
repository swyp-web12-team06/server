package com.redot.controller;

import com.redot.dto.common.ApiResponse;
import com.redot.dto.library.LibraryResponse;
import com.redot.dto.library.LibrarySalesResponse;
import com.redot.service.LibraryService;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/me/library")
public class LibraryController {

    private final LibraryService libraryService;

    /**
     * [구매 목록 조회]
     */
    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<LibraryResponse>>> getPurchases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 💡 2. NPE 방지 및 userId 추출 로직 변경
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(userDetails.getUsername());

        log.info(">>> [조회] 유저 {}의 구매 목록을 조회합니다.", userId);
        List<LibraryResponse> data = libraryService.getMyPurchases(userId);

        return ResponseEntity.ok(ApiResponse.success("조회 성공", data));
    }

    /**
     * [판매 내역 조회]
     */
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<LibrarySalesResponse>>> getSales(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        Long userId = Long.parseLong(userDetails.getUsername());

        log.info(">>> [조회] 유저 {}의 판매 내역을 조회합니다.", userId);
        List<LibrarySalesResponse> data = libraryService.getMySalesList(userId);

        return ResponseEntity.ok(ApiResponse.success("판매 내역 조회 성공", data));
    }
}