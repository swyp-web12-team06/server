package com.redot.exception;

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
    PAYMENT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "결제가 완료되지 않았거나 이미 취소되었습니다."),
    ALREADY_PROCESSED_PAYMENT(HttpStatus.BAD_REQUEST, "이미 처리된 결제입니다."),
    NICKNAME_UPDATE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "닉네임은 30일에 한 번만 변경할 수 있습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID 형식입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "유효하지 않은 ROLE입니다."),
    INVALID_PRICE_UNIT(HttpStatus.BAD_REQUEST, "가격은 100원 단위로 설정해야 합니다."),
    INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST, "가격은 500원에서 1,000원 사이여야 합니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 유효하지 않습니다. (최소 3,000원 ~ 최대 50,000원)"),
    INVALID_TAG_COUNT(HttpStatus.BAD_REQUEST, "태그는 최소 2개, 최대 5개까지 등록 가능합니다."),
    INVALID_TAG_LENGTH(HttpStatus.BAD_REQUEST, "태그는 2~12자 이내여야 합니다."),
    INVALID_TAG_FORMAT(HttpStatus.BAD_REQUEST, "태그는 한글, 영문, 숫자, 공백만 사용 가능합니다."),
    INVALID_MODEL_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 AI 모델명입니다."),

    // 상품(프롬프트) 관련 검증 (400 Bad Request)
    INVALID_PREVIEW_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "썸네일(미리보기) 이미지는 필수입니다."),
    LOOKBOOK_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "최소 1개 이상의 룩북 이미지가 필요합니다."),
    INVALID_REPRESENTATIVE_IMAGE_COUNT(HttpStatus.BAD_REQUEST, "대표 이미지는 최소 1장, 최대 3개까지 설정할 수 있습니다."),
    UNDEFINED_PROMPT_VARIABLE(HttpStatus.BAD_REQUEST, "프롬프트에 정의되지 않은 변수가 사용되었습니다."),
    INCOMPLETE_VARIABLE_OPTIONS(HttpStatus.BAD_REQUEST, "룩북 이미지의 모든 변수에 대한 값을 지정해야 합니다."),
    IMAGE_NOT_BELONG_TO_PRODUCT(HttpStatus.BAD_REQUEST, "해당 상품에 속하지 않는 이미지입니다."),
    PREVIEW_MUST_BE_REPRESENTATIVE(HttpStatus.BAD_REQUEST, "프리뷰로 지정된 이미지는 반드시 대표 이미지여야 합니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "룩북 이미지는 최대 10장입니다."),

    // 이미지 검증 관련 (400 Bad Request)
    IMAGE_FILE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있습니다."),
    IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일 크기는 5MB를 초과할 수 없습니다."),
    IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 형식입니다. (JPG, PNG, WebP만 가능)"),
    IMAGE_INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "잘못된 파일 확장자입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),

    // 403 Forbidden
    ALREADY_REGISTERED(HttpStatus.FORBIDDEN, "이미 가입이 완료된 회원입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_ORIGIN(HttpStatus.FORBIDDEN, "허용되지 않은 요청 출처입니다."),
    NOT_PURCHASED_ITEM(HttpStatus.FORBIDDEN, "구매하지 않은 프롬프트입니다."),
    CANNOT_DELETE_PURCHASED_ITEM(HttpStatus.BAD_REQUEST, "이미 판매 내역이 있는 상품은 삭제할 수 없습니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_NOT_FOUND_LOGOUT(HttpStatus.NOT_FOUND, "이미 로그아웃 된 사용자입니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이미지를 찾을 수 없습니다."),
    PROMPT_NOT_FOUND(HttpStatus.NOT_FOUND, "프롬프트 정보를 찾을 수 없습니다."),
    VARIABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 변수 정보를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다."),
    AI_MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 AI 모델이 존재하지 않습니다."),
    ALREADY_DELETED(HttpStatus.NOT_FOUND, "이미 탈퇴한 사용자입니다."),

    // 409 Conflict
    NICKNAME_DUPLICATION(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다."),

    // 429 TOO_MANY_REQUESTS
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),

    // 500 Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),
    PAYMENT_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생하여 자동 취소되었습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String getCode() {
        return this.name(); // "NICKNAME_DUPLICATION" 같은 이름 반환
    }
}