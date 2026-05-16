package com.northpay.backend.notification;

import com.northpay.backend.common.config.PusherConfig;
import com.pusher.rest.Pusher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pusher")
public class PusherAuthController {

    private final PusherConfig config;

    public PusherAuthController(PusherConfig config) {
        this.config = config;
    }

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(
            @RequestParam("channel_name") String channelName,
            @RequestParam("socket_id") String socketId,
            HttpServletRequest request) {

        if (!"private-onboarding".equals(channelName)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Pusher pusher = new Pusher(config.appId(), config.key(), config.secret());
        String auth = pusher.authenticate(socketId, channelName);
        return ResponseEntity.ok(auth);
    }
}