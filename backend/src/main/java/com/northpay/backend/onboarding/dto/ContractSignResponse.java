package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.OnboardingStatus;

public record ContractSignResponse(
        Long id,
        OnboardingStatus status,
        Integer currentStep,
        String documentUrl
) {
}
