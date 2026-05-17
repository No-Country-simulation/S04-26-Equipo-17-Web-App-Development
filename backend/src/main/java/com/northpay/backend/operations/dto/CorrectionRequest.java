package com.northpay.backend.operations.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Solicitud de corrección")
public record CorrectionRequest(
        @NotNull
        @Min(1) @Max(5)
        @Schema(description = "Número del paso",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        Integer stepNumber,

        @NotBlank
        @Schema(description = "Observaciones",
                example = "Observaciones",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String observations
) {}