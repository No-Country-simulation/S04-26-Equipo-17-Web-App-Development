package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.OnboardingStatus;

import java.util.List;

public record DocumentsUploadResponse(
        Long onboardingId,
        OnboardingStatus status,
        Integer currentStep,
        List<DocumentResponse> documents
) {
}
