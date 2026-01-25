package com.redot.controller;

import com.redot.auth.JwtTokenProvider;
import com.redot.domain.user.Role;
import com.redot.dto.common.ApiResponse;
import com.redot.exception.BusinessException;
import com.redot.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevLoginController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/token")
    public ResponseEntity<ApiResponse<Map<String, String>>> createDevToken(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "USER") String role
    ) {

        Role userRole;
        try {
            userRole = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_ROLE);
        }

        String accessToken = jwtTokenProvider.createAccessToken(userId, userRole);

        Map<String, String> data = Map.of("accessToken", accessToken);

        return ResponseEntity.ok()
                .body(ApiResponse.success("개발용 토큰 발급 성공", data));
    }
}