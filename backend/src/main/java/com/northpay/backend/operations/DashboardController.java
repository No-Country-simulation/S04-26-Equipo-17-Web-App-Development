package com.northpay.backend.operations;

import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.operations.dto.DashboardItem;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponseBackend<List<DashboardItem>>> dashboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(required = false)
            @Schema(description = "Filtro por estado")
            String status,
            @RequestParam(required = false)
            @Schema(description = "Filtro por paso")
            Integer step,
            @RequestParam(required = false)
            @Schema(description = "Filtro por país")
            String country) {

        authService.getCurrentOperator(authHeader);

        List<DashboardItem> items = dashboardService.getDashboard(status, step, country);
        return ResponseEntity.ok(ApiResponseBackend.ok("Dashboard cargado", items));
    }
}