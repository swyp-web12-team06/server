package com.redot.auth.controller;

import com.redot.auth.dto.TokenReissueResponse;
import com.redot.auth.service.AuthService;
import com.redot.dto.common.ApiResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${app.cookie.secure}")
    private boolean secure;

    @Value("${app.cookie.same-site}")
    private String sameSite;

    @Value("${security.allowed-origins}")
    private String allowedOrigins;

    @PostMapping("/reissue")
    public ApiResponse<TokenReissueResponse> reissue(
            @RequestHeader(value = "Origin", required = false) String origin,
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (origin == null ||
                Arrays.stream(allowedOrigins.split(","))
                        .noneMatch(origin::equals)) {
            throw new BusinessException(ErrorCode.INVALID_ORIGIN); //403 허용되지 않은 요청 출처
        }
        // 쿠키가 아예 없는 경우 (400)
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        TokenReissueResponse response = authService.reissue(refreshToken);

        return ApiResponse.success("토큰이 재발급되었습니다.", response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        // 쿠키가 없는 경우 (400)
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_MISSING);
        }

        // 토큰 유효성 검증, DB에서 지움
        authService.logout(refreshToken);

        // 쿠키 삭제를 위한 ResponseCookie 생성
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "") // 값을 비움
                .path("/")
                .sameSite(sameSite)
                .httpOnly(true)
                .secure(secure)
                .maxAge(0) // 수명을 0으로 설정하면 브라우저가 즉시 삭제함
                .build();

        // 응답 생성
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString()) // 헤더에 쿠키 삭제 명령 추가
                .body(new ApiResponse<>(
                        "LOGOUT_SUCCESS",
                        "로그아웃 되었습니다.",
                        null
                ));
    }
}