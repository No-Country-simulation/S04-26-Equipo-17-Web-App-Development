package com.northpay.backend.operations;

import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.operations.dto.DashboardItem;
import com.northpay.backend.operations.dto.TimelineEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Operadores", description = "Endpoint de dashboard")
public class DashboardController {

    private final AuthService authService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    @Operation(summary = "Obtener el dashboard", description = "Endpoint para obtener el dashboard")
    @ApiResponse(
            responseCode = "200",
            description = "Dashboard cargado"
    )
    public ResponseEntity<ApiResponseBackend<List<DashboardItem>>> dashboard(
            @AuthenticationPrincipal String principal,
            @RequestParam(required = false)
            @Schema(description = "Filtro por estado")
            String status,
            @RequestParam(required = false)
            @Schema(description = "Filtro por paso")
            Integer step,
            @RequestParam(required = false)
            @Schema(description = "Filtro por país")
            String country) {

        authService.getCurrentOperator(principal);

        List<DashboardItem> items = dashboardService.getDashboard(status, step, country);
        return ResponseEntity.ok(ApiResponseBackend.ok("Dashboard cargado", items));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Obtener el historial de un onboarding", description = "Endpoint para obtener el historial de un onboarding")
    @ApiResponse(
            responseCode = "200",
            description = "Historial cargado"
    )
    public ResponseEntity<ApiResponseBackend<List<TimelineEvent>>> timeline(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {

        authService.getCurrentOperator(authHeader);
        List<TimelineEvent> timeline = dashboardService.getTimeline(id);
        return ResponseEntity.ok(ApiResponseBackend.ok("Historial cargado", timeline));
    }
}