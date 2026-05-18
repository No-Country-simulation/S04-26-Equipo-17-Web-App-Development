package com.northpay.backend.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos del contratista")
public record Step1Request(
        @NotBlank(message = "El nombre completo es requerido")
        @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
        @Schema(description = "Es el nombre completo del contratista",
                example = "John Doe",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String fullName,

        @NotBlank(message = "El código de país es requerido")
        @Size(min = 2, max = 2, message = "El código de país debe ser ISO de 2 letras")
        @Schema(description = "Es el código ISO de 2 letras del país del contratista",
                example = "MX",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String countryIso
) {
}
