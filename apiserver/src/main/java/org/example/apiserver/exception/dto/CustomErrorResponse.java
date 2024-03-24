package org.example.apiserver.exception.dto;


import org.example.apiserver.exception.domain.ErrorCode;

public record CustomErrorResponse<T>(String code, String message, T errors) {

    public static <T> CustomErrorResponse<T> from(ErrorCode errorCode, T errors) {
        return new CustomErrorResponse<>(errorCode.name(), errorCode.getMessage(), errors);
    }

}
