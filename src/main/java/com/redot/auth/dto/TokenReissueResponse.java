package com.redot.auth.dto;

public record TokenReissueResponse(
        String accessToken,
        Boolean isNewUser,
        String role
) {}