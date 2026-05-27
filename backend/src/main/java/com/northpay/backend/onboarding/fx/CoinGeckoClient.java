package com.northpay.backend.onboarding.fx;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Cliente para el precio del stablecoin USDC. CoinGecko free tier, sin API key.
 * Devuelve cuántos USD vale 1 USDC (idealmente ~1.0).
 */
@Component
class CoinGeckoClient {

    private static final String BASE_URL = "https://api.coingecko.com/api/v3";
    private static final String USDC_ID = "usd-coin";

    private final RestClient http = RestClient.builder().baseUrl(BASE_URL).build();

    @Cacheable(FxCacheConfig.CACHE_USDC)
    public BigDecimal usdPerUsdc() {
        Map<String, Map<String, BigDecimal>> body = http.get()
                .uri(uri -> uri.path("/simple/price")
                        .queryParam("ids", USDC_ID)
                        .queryParam("vs_currencies", "usd")
                        .build())
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<>() {});

        if (body == null || body.get(USDC_ID) == null || body.get(USDC_ID).get("usd") == null) {
            throw new IllegalStateException("CoinGecko devolvió respuesta vacía para USDC");
        }
        return body.get(USDC_ID).get("usd");
    }
}
