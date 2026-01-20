package com.tn.server.dto.user;
import com.tn.server.domain.user.User;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String email,
        String profileImageUrl,
        String bio,
        Integer creditBalance
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getBio(),
                user.getCreditBalance()
        );
    }
}