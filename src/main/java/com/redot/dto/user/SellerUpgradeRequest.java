package com.redot.dto.user;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record SellerUpgradeRequest(
        @NotNull(message = "약관 동의 여부는 필수입니다.")
        @AssertTrue(message = "판매자 약관에 동의해야 등업할 수 있습니다.")
        Boolean agreeToSellerTerms
) {}