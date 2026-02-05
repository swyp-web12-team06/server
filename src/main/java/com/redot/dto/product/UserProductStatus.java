package com.redot.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자와 상품 간의 관계 상태")
public enum UserProductStatus {
    @Schema(description = "비로그인 사용자")
    GUEST,

    @Schema(description = "로그인했으나 구매하지 않은 사용자")
    NOT_PURCHASED,

    @Schema(description = "구매한 사용자")
    PURCHASED,

    @Schema(description = "상품 판매자 본인 (수정/삭제 권한)")
    OWNER
}
