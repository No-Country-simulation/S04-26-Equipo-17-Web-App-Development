package com.northpay.backend.invitation;


import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
import com.northpay.backend.invitation.dto.TokenValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitaciones", description = "Endpoints para gestionar invitaciones a contratistas")
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    @Operation(summary = "Crear una invitación",
            description = "Envía una invitación al correo electrónico proporcionado para iniciar el onboarding")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitación creada exitosamente",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (email vacío o mal formado)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponseBackend<InvitationResponse>> createInvitation(
            @Valid @RequestBody InvitationRequest request) {
        InvitationResponse response = invitationService.sendInvitation(request);
        return ResponseEntity.ok(ApiResponseBackend.ok(response));
    }

    @GetMapping("/{token}")
    @Operation(summary = "Validar token de invitación",
            description = "Valida el token, activa el onboarding (INVITED→IN_PROGRESS) y devuelve los datos de sesión.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido, sesión iniciada"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "410", description = "Token expirado")
    })
    public ResponseEntity<ApiResponseBackend<TokenValidationResponse>> validateToken(
            @PathVariable String token) {
        TokenValidationResponse response = invitationService.validateToken(token);
        return ResponseEntity.ok(ApiResponseBackend.ok("Token válido", response));
    }

    @PostMapping("/{id}/resend")
    @Operation(summary = "Reenviar invitación",
            description = "Genera un nuevo token de invitación y envía un nuevo correo. Útil cuando el token expiró.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invitación reenviada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Onboarding no encontrado")
    })
    public ResponseEntity<ApiResponseBackend<InvitationResponse>> resendInvitation(
            @PathVariable
            @Schema(description = "ID del onboarding",
                    example = "123",
                    requiredMode = Schema.RequiredMode.REQUIRED
            )
            Long id) {
        InvitationResponse response = invitationService.resendInvitation(id);
        return ResponseEntity.ok(ApiResponseBackend.ok("Invitación reenviada", response));
    }
}