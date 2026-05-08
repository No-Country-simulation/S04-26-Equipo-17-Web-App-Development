package com.northpay.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseBackend<T>(
        boolean success,
        String message,
        T data
) {
    public static <T> ApiResponseBackend<T> ok(T data) {
        return new ApiResponseBackend<>(true, "Operación exitosa", data);
    }

    public static <T> ApiResponseBackend<T> ok(String message, T data) {
        return new ApiResponseBackend<>(true, message, data);
    }
}
