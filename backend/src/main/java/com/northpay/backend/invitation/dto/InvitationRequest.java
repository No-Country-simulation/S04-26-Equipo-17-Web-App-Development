package com.northpay.backend.invitation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para enviar una invitación")
public record InvitationRequest(
        @NotBlank
        @Email
        @Schema(description = "Correo electrónico del contratista",
                example = "contratista@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        String email
) {
}