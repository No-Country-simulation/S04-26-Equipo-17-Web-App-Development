package com.northpay.backend.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pusher")
@Tag(name = "Notificaciones", description = "Endpoint de autenticación Pusher")
public class PusherAuthController {

    private final PusherAuthService pusherAuthService;

    public PusherAuthController(PusherAuthService pusherAuthService) {
        this.pusherAuthService = pusherAuthService;
    }

    @PostMapping("/auth")
    @Operation(summary = "Autenticación Pusher", description = "Autentica un cliente Pusher")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    public ResponseEntity<String> authenticate(
            @RequestParam("channel_name") String channelName,
            @RequestParam("socket_id") String socketId,
            HttpServletRequest request) {

        String auth = pusherAuthService.authenticateChannel(channelName, socketId);
        return ResponseEntity.ok(auth);
    }
}