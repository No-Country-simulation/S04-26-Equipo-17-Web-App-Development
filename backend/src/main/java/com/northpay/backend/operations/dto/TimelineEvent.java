package com.northpay.backend.operations.dto;

import com.northpay.backend.common.enums.EventType;
import com.northpay.backend.common.enums.OnboardingStatus;
import java.time.LocalDateTime;

public record TimelineEvent(
        Long id,
        EventType event,
        OnboardingStatus previousStatus,
        OnboardingStatus newStatus,
        String observations,
        LocalDateTime createdAt
) {}