package com.northpay.backend.operations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Inicio de sesión")
public record LoginRequest(
        @NotBlank @Email
        @Schema(description = "Correo electrónico del operador",
                example = "admin@northpay.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank
        @Schema(description = "Contraseña del operador",
                example = "NorthPay123",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}