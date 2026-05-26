package com.northpay.backend.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.northpay.backend.common.enums.OnboardingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record Step1Response(
        Long onboardingId,
        OnboardingStatus status,
        Integer currentStep,
        String firstName,
        String lastName,
        String preferredName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthDate,
        String countryIso,
        String idDocumentNumber,
        String taxRegime,
        String phone,
        String email,
        LocalDateTime updatedAt
) {
}
