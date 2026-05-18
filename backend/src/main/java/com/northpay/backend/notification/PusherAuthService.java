package com.northpay.backend.notification;

import com.northpay.backend.common.config.PusherConfig;
import com.northpay.backend.common.exception.InvalidTokenException;
import com.pusher.rest.Pusher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PusherAuthService {

    private final PusherConfig config;

    public String authenticateChannel(String channelName, String socketId) {
        if (!"private-onboarding".equals(channelName)) {
            throw new InvalidTokenException("Canal no autorizado");
        }

        Pusher pusher = new Pusher(config.appId(), config.key(), config.secret());
        return pusher.authenticate(socketId, channelName);
    }
}