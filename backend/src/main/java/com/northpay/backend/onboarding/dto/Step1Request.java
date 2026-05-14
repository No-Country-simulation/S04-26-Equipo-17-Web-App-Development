package com.northpay.backend.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Step1Request(
        @NotBlank(message = "El nombre completo es requerido")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        String fullName,

        @NotBlank(message = "El código de país es requerido")
        @Size(min = 2, max = 2, message = "El código de país debe ser ISO de 2 letras")
        String countryIso
) {
}
