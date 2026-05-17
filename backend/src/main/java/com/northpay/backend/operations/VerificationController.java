package com.northpay.backend.operations;

import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.operations.dto.CorrectionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Operadores", description = "Endpoint de verificación")
public class VerificationController {

    private final AuthService authService;
    private final VerificationService verificationService;

    @PostMapping("/{id}/request-correction")
    @Operation(summary = "Solicitar corrección", description = "Solicita una corrección al onboarding")
    public ResponseEntity<ApiResponseBackend<Void>> requestCorrection(
            @AuthenticationPrincipal String principal,
            @PathVariable
            @Schema(description = "ID del onboarding")
            Long id,
            @Valid @RequestBody CorrectionRequest request) {

        authService.getCurrentOperator(principal);
        verificationService.requestCorrection(id, request.stepNumber(), request.observations());

        return ResponseEntity.ok(ApiResponseBackend.ok("Corrección solicitada", null));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activar cuenta", description = "Aprueba definitivamente el onboarding. Solo desde PENDING_VERIFICATION.")
    public ResponseEntity<ApiResponseBackend<Void>> activate(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id) {

        authService.getCurrentOperator(principal);
        verificationService.activate(id);
        return ResponseEntity.ok(ApiResponseBackend.ok("Cuenta activada", null));
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Rechazar cuenta", description = "Rechaza el onboarding (fraude/riesgo). Válido desde cualquier estado.")
    public ResponseEntity<ApiResponseBackend<Void>> reject(
            @AuthenticationPrincipal String principal,
            @PathVariable Long id) {

        authService.getCurrentOperator(principal);
        verificationService.reject(id);
        return ResponseEntity.ok(ApiResponseBackend.ok("Cuenta rechazada", null));
    }
}