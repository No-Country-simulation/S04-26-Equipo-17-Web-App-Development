package com.northpay.backend.onboarding.dto;

import java.time.LocalDate;

public record ContractDetailsResponse(
        String monthlyAmount,
        String currency,
        String alternateCurrency,
        Integer durationMonths,
        LocalDate celebrationDate,
        LocalDate startDate,
        String company,
        boolean signed
) {
}
