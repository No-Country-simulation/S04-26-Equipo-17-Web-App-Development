package com.northpay.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.brevo")
public record BrevoConfig(String apiKey, String fromEmail, String fromName) {}