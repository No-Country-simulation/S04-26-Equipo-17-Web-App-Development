package com.northpay.backend.onboarding.fx;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tasas de cambio fiat vía {@code open.er-api.com} (free, sin API key).
 * Se eligió sobre Frankfurter porque éste no cubre COP (Peso Colombiano).
 *
 * El endpoint devuelve todas las tasas con la base solicitada; filtramos
 * a los targets que pide el caller.
 */
@Component
class ExchangeRateClient {

    private static final String BASE_URL = "https://open.er-api.com/v6";

    private final RestClient http = RestClient.builder().baseUrl(BASE_URL).build();

    @Cacheable(FxCacheConfig.CACHE_FIAT)
    public Map<String, BigDecimal> rates(String base, List<String> targets) {
        OpenErResponse body = http.get()
                .uri("/latest/{base}", base.toUpperCase())
                .retrieve()
                .body(OpenErResponse.class);

        if (body == null || !"success".equals(body.result()) || body.rates() == null) {
            throw new IllegalStateException("open.er-api.com devolvió respuesta vacía o con error");
        }

        Map<String, BigDecimal> out = new HashMap<>();
        for (String target : targets) {
            String key = target.toUpperCase();
            BigDecimal rate = body.rates().get(key);
            if (rate == null) {
                throw new IllegalStateException("Sin tasa para " + target);
            }
            out.put(key, rate);
        }
        return out;
    }

    record OpenErResponse(String result, Map<String, BigDecimal> rates) {}
}
