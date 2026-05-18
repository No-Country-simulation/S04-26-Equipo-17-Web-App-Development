package com.northpay.backend.operations.dto;

public record LoginResponse(
        String token,
        Long operatorId,
        String fullName,
        String email,
        String role
) {}