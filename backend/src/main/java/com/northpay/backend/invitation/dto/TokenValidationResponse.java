package com.northpay.backend.invitation.dto;

public record TokenValidationResponse(
        Long onboardingId,
        String sessionToken,
        Integer currentStep,
        String status,
        String fullName,
        String email
) {}