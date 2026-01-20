package com.tn.server.controller.user;

import com.tn.server.dto.common.ApiResponse;
import com.tn.server.dto.user.PublicUserProfileResponse;
import com.tn.server.dto.user.SignupRequest;
import com.tn.server.dto.user.UserProfileResponse;
import com.tn.server.dto.user.UserUpdateRequest;
import com.tn.server.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        userService.withdraw(userId);

        return ResponseEntity.ok()
                .body(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 토큰에서 userId 추출
        Long userId = Long.parseLong(userDetails.getUsername());

        UserProfileResponse profile = userService.getMyProfile(userId);

        return ResponseEntity.ok()
                .body(ApiResponse.success("조회 성공", profile));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid UserUpdateRequest request
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());

        userService.updateProfile(userId, request);

        return ResponseEntity.ok()
                .body(ApiResponse.success("프로필이 수정되었습니다."));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<PublicUserProfileResponse>> getUserProfile(
            @PathVariable Long userId
    ) {
        PublicUserProfileResponse response = userService.getPublicProfile(userId);

        return ResponseEntity.ok()
                .body(ApiResponse.success("조회 성공", response));
    }




}
