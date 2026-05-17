package com.northpay.backend.operations;

import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.common.exception.InvalidTokenException;
import com.northpay.backend.operations.dto.LoginRequest;
import com.northpay.backend.operations.dto.LoginResponse;
import com.northpay.backend.operations.dto.OperatorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Operadores", description = "Endpoint de autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Endpoint para iniciar sesión")
    @ApiResponse(
            responseCode = "200",
            description = "Login exitoso"
    )
    public ResponseEntity<ApiResponseBackend<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponseBackend.ok("Login exitoso", response));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener información del operador autenticado", description = "Endpoint para obtener información del operador autenticado")
    @ApiResponse(
            responseCode = "200",
            description = "Operador autenticado"
    )
    public ResponseEntity<ApiResponseBackend<OperatorResponse>> me(
            @AuthenticationPrincipal String principal) {

        Operator operator = authService.getCurrentOperator(principal);

        OperatorResponse response = new OperatorResponse(
                operator.getId(),
                operator.getFullName(),
                operator.getEmail(),
                operator.getRole()
        );

        return ResponseEntity.ok(ApiResponseBackend.ok("Sesión válida", response));
    }
}