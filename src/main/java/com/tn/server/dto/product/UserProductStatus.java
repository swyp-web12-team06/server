package com.tn.server.dto.product;

public enum UserProductStatus {
    GUEST,          // 비로그인
    NOT_PURCHASED,  // 로그인 O, 구매 안 함
    PURCHASED,      // 로그인 O, 구매 함
    OWNER           // 판매자 본인 (수정/삭제 권한)
}
