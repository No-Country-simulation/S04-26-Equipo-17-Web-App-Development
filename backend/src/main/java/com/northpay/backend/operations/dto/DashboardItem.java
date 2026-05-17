package com.northpay.backend.operations.dto;

import java.time.LocalDateTime;

public record DashboardItem(
        Long onboardingId,
        String contractorName,
        String contractorEmail,
        String countryIso,
        Integer currentStep,
        String status,
        LocalDateTime updatedAt,
        Long hoursSinceUpdate,
        boolean slaBreach
) {}