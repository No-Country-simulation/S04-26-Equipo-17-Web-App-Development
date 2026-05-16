package com.northpay.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pusher")
public record PusherConfig(
        String appId,
        String key,
        String secret,
        String cluster
) {}