package org.example.apiserver.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.apiserver.exception.domain.CustomException;
import org.example.apiserver.exception.domain.ErrorCode;
import org.example.apiserver.exception.dto.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * ExceptionHandler 호출 우선순위
 * 1. 가장 구체적인 예외 유형의 핸들러
 * 2. 더 먼저(더 위에) 정의된 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomErrorResponse<Object>> handleCustomException(CustomException ex) {
        logError(ex);
        ErrorCode errorCode = ex.getErrorCode();
        return createResponseEntity(errorCode.name(), errorCode.getMessage(), null, errorCode.getHttpStatus());
    }

    private <T> ResponseEntity<CustomErrorResponse<T>> createResponseEntity(String code, String message, T errors, HttpStatus httpStatus) {
        CustomErrorResponse<T> customErrorResponse = new CustomErrorResponse<>(code, message, errors);
        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(customErrorResponse);
    }

    private void logError(Exception ex) {
        log.error(ex.getClass().getSimpleName(), ex);
    }

}
