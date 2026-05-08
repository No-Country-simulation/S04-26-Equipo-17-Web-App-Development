package com.northpay.backend.invitation;


import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
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
}