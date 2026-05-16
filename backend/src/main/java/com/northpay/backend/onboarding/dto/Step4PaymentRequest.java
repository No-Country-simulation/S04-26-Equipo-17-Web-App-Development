package com.northpay.backend.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record Step4PaymentRequest(
        @NotBlank(message = "El tipo de cuenta es requerido")
        @Size(max = 50, message = "El tipo de cuenta no puede exceder 50 caracteres")
        String accountType,

        Map<String, Object> details
) {
}
