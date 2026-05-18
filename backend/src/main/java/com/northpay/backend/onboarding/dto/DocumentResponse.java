package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.OnboardingStatus;

import java.time.LocalDateTime;

public record DocumentResponse(
        Long id,
        DocumentType docType,
        String fileUrl,
        OnboardingStatus status,
        LocalDateTime uploadedAt
) {
}
