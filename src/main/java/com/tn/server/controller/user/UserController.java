package com.tn.server.controller.user;

import com.tn.server.dto.common.ApiResponse;
import com.tn.server.dto.user.SignupRequest;
import com.tn.server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController  {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, String>>> signup(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SignupRequest request // @Valid가 DTO의 @Pattern을 검사
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String newAccessToken = userService.signup(userId, request);

        Map<String, String> data = new HashMap<>();
        data.put("accessToken", newAccessToken);

        // 기존 Refresh Token은 유지함
        return ResponseEntity.ok()
                .body(ApiResponse.success("회원가입에 성공했습니다.", data));
    }


}
