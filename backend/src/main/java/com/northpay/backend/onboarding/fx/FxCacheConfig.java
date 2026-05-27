package com.northpay.backend.onboarding.fx;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class FxCacheConfig {

    static final String CACHE_FIAT = "fx-fiat";
    static final String CACHE_USDC = "fx-usdc";

    @Bean
    CacheManager fxCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(CACHE_FIAT, CACHE_USDC);
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(1))
                .maximumSize(100));
        return manager;
    }
}
