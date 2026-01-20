package com.tn.server.dto.user;

import com.tn.server.domain.user.User;

public record PublicUserProfileResponse(
        String nickname,
        String profileImageUrl,
        String bio
) {
    public static PublicUserProfileResponse from(User user) {
        return new PublicUserProfileResponse(
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getBio()
        );
    }
}