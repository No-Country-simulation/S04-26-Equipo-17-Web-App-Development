package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.OnboardingStatus;

import java.time.LocalDateTime;

public record OnboardingStatusResponse(
        Long id,
        OnboardingStatus status,
        Integer currentStep,
        LocalDateTime updatedAt
) {
}
