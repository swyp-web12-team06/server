package com.tn.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
    REFRESH_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "리프레시 토큰이 없습니다."),
    NICKNAME_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "닉네임은 2~15자의 영문, 숫자, 한글, -, _ 만 사용 가능합니다."),
    NICKNAME_MISSING(HttpStatus.BAD_REQUEST, "닉네임은 필수 입력 값입니다."),
    BIO_TOO_LONG(HttpStatus.BAD_REQUEST, "소개글은 최대 200자까지만 작성할 수 있습니다."),
    TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "필수 약관에 동의해야 합니다."),
    MARKETING_CONSENT_MISSING(HttpStatus.BAD_REQUEST, "마케팅 수신 동의 여부는 필수입니다."),
    VARIABLE_OPTION_MISMATCH(HttpStatus.BAD_REQUEST, "옵션 매칭 실패"),
    ALREADY_PURCHASED(HttpStatus.BAD_REQUEST, "이미 구매한 상품입니다."),
    INSUFFICIENT_CREDIT(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    NICKNAME_UPDATE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "닉네임은 30일에 한 번만 변경할 수 있습니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    // 403 Forbidden
    ALREADY_REGISTERED(HttpStatus.FORBIDDEN, "이미 가입이 완료된 회원입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_ORIGIN(HttpStatus.FORBIDDEN, "허용되지 않은 요청 출처입니다."),
    NOT_PURCHASED_ITEM(HttpStatus.FORBIDDEN, "구매하지 않은 프롬프트입니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_NOT_FOUND_LOGOUT(HttpStatus.NOT_FOUND, "이미 로그아웃 된 사용자입니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),
    PROMPT_NOT_FOUND(HttpStatus.NOT_FOUND, "프롬프트 정보를 찾을 수 없습니다."),
    ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 탈퇴한 사용자입니다."),

    // 409 Conflict
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다."),

    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String getCode() {
        return this.name(); // "NICKNAME_DUPLICATION" 같은 이름 반환
    }
}