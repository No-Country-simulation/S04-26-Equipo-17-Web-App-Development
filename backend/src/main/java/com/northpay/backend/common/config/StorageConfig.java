package com.northpay.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.storage")
public record StorageConfig(
        String endpoint,
        String region,
        String bucket,
        String accessKey,
        String secretKey,
        String publicUrlPrefix
) {
}
