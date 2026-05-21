package com.northpay.backend.onboarding.dto;

import com.northpay.backend.common.enums.OnboardingStatus;

import java.time.LocalDateTime;

public record CorrectionCommentResponse(
        String observations,
        OnboardingStatus previousStatus,
        OnboardingStatus newStatus,
        LocalDateTime createdAt
) {
}
