package com.tn.server.auth.dto;

public record TokenReissueResponse(
        String accessToken,
        Boolean isNewUser,
        String role
) {}