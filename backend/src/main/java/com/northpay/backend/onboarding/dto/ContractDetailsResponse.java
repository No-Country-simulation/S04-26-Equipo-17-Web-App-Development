package com.northpay.backend.onboarding.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record ContractDetailsResponse(
        String monthlyAmount,
        String currency,
        String alternateCurrency,
        Integer durationMonths,
        LocalDate celebrationDate,
        LocalDate startDate,
        String company,
        boolean signed,
        Map<String, BigDecimal> conversions
) {
}
