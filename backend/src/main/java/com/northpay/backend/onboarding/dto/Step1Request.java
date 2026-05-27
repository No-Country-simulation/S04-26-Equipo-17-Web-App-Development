package com.northpay.backend.onboarding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.northpay.backend.common.validation.MinAge;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Datos personales del contratista (Paso 1).")
public record Step1Request(
        @NotBlank(message = "Los nombres son requeridos")
        @Size(max = 80, message = "Los nombres no pueden exceder 80 caracteres")
        @Schema(description = "Nombres del contratista",
                example = "Juan Carlos",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @NotBlank(message = "Los apellidos son requeridos")
        @Size(max = 80, message = "Los apellidos no pueden exceder 80 caracteres")
        @Schema(description = "Apellidos del contratista",
                example = "Pérez Gómez",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        @Size(max = 80, message = "El nombre preferido no puede exceder 80 caracteres")
        @Schema(description = "Cómo te llamamos (nombre preferido). Opcional.",
                example = "Juan")
        String preferredName,

        @NotNull(message = "La fecha de nacimiento es requerida")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        @MinAge(value = 18, message = "El contratista debe ser mayor de 18 años")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Schema(description = "Fecha de nacimiento en formato ISO YYYY-MM-DD",
                example = "1990-05-23",
                requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate birthDate,

        @NotBlank(message = "El código de país es requerido")
        @Size(min = 2, max = 2, message = "El código de país debe ser ISO de 2 letras")
        @Schema(description = "Código ISO de 2 letras del país de residencia",
                example = "MX",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String countryIso,

        @NotBlank(message = "El documento de identificación es requerido")
        @Size(max = 50, message = "El documento no puede exceder 50 caracteres")
        @Pattern(regexp = "^[0-9]+$", message = "El documento debe contener solo números")
        @Schema(description = "Número de documento de identificación (solo dígitos)",
                example = "12345678",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String idDocumentNumber,

        @NotBlank(message = "El régimen tributario es requerido")
        @Size(max = 80, message = "El régimen tributario no puede exceder 80 caracteres")
        @Schema(description = "Régimen tributario aplicable al contratista",
                example = "Monotributo",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String taxRegime,

        @NotBlank(message = "El teléfono es requerido")
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$",
                message = "El teléfono debe tener formato E.164 (ej. +5491123456789)")
        @Schema(description = "Teléfono en formato E.164",
                example = "+5491123456789",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String phone
) {
}
