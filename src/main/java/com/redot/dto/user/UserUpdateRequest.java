package com.redot.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣-_]{2,15}$",
                message = "닉네임은 숫자, 영어, 한글, -, _ 만 사용하여 2~15자여야 합니다."
        )
        String nickname,

        @Size(max = 200, message = "소개글은 최대 200자까지만 작성할 수 있습니다.")
        String bio,

        String profileImageKey
) {}