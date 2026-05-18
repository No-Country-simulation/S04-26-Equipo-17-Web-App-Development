package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.OnboardingStatus;

import java.time.LocalDateTime;

public record Step1Response(
        Long onboardingId,
        OnboardingStatus status,
        Integer currentStep,
        String fullName,
        String countryIso,
        LocalDateTime updatedAt
) {
}
