package com.northpay.backend.operations.dto;

public record OperatorResponse(
        Long id,
        String fullName,
        String email,
        String role
) {}