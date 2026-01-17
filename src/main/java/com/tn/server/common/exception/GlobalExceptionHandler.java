package com.tn.server.common.exception;

import com.tn.server.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .badRequest() // 기본 400 에러
                .body(ErrorResponse.builder()
                        .code(e.getErrorCode())
                        .message(e.getMessage())
                        .build());
    }
}