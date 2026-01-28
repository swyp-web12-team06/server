package com.redot.dto.user;
import com.redot.domain.user.User;

public record UserProfileResponse(
        Long userId,
        String nickname,
        String email,
        String profileImageUrl,
        String bio,
        Integer creditBalance
) {
    public static UserProfileResponse from(User user, String publicUrl) {
        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                publicUrl,
                user.getBio(),
                user.getCreditBalance()
        );
    }
}