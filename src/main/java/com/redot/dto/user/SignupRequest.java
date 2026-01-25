package com.redot.dto.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣-_]{2,15}$",
                message = "닉네임은 숫자, 영어, 한글, -, _ 만 사용하여 2~15자여야 합니다."
        )
        String nickname,

        @AssertTrue(message = "필수 약관에 동의해야 합니다.")
        @NotNull(message = "필수 약관 동의 여부는 필수입니다.")
        Boolean termsAgreed,

        @NotNull(message = "마케팅 수신 동의 여부는 필수입니다.")
        Boolean marketingConsent
) {}