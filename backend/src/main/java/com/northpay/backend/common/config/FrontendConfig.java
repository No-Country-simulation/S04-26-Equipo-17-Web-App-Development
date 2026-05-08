package com.northpay.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.frontend")
public record FrontendConfig(
        String url,
        String onboardingPath
) {
}
