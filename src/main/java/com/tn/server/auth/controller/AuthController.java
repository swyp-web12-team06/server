package com.tn.server.auth.controller;

import com.tn.server.auth.dto.TokenReissueResponse;
import com.tn.server.auth.service.AuthService;
import com.tn.server.dto.common.ApiResponse;
import com.tn.server.exception.BusinessException;
import com.tn.server.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}