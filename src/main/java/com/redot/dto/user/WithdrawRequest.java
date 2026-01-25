package com.redot.dto.user;

import jakarta.validation.constraints.Size;

public record WithdrawRequest(
        @Size(max = 300, message = "탈퇴 사유는 300자 이내여야 합니다.")
        String deleteReason
) {}