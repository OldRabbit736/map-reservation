package org.example.apiserver.exception.dto;


public record CustomErrorResponse<T>(String code, String message, T errors) {
}
