package com.northpay.backend.onboarding.fx;

import com.northpay.backend.onboarding.contract.ContractTerms;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fachada de conversión de moneda. Combina rates fiat (Frankfurter) con el
 * precio de USDC (CoinGecko) y aplica el monto mensual del contrato.
 *
 * Targets fijos: USD, COP, EUR, USDC. Si se necesitan más, agregar al array.
 *
 * Lanza {@link RuntimeException} si cualquier proveedor falla; el caller
 * (OnboardingService) decide si tolerar el error.
 */
@Service
@RequiredArgsConstructor
public class FxService {

    private static final List<String> FIAT_TARGETS = List.of("USD", "COP", "EUR");
    private static final String USDC = "USDC";
    private static final int SCALE = 2;

    private final ExchangeRateClient exchangeRate;
    private final CoinGeckoClient coinGecko;

    /**
     * Convierte el monto mensual del contrato (USD por defecto) a USD, COP, EUR y USDC.
     * Retorna un map con orden estable de inserción.
     */
    public Map<String, BigDecimal> monthlyAmountConversions() {
        BigDecimal amount = new BigDecimal(ContractTerms.MONTHLY_AMOUNT.replace(",", ""));
        String base = ContractTerms.CURRENCY_PRIMARY;

        Map<String, BigDecimal> fiatRates = exchangeRate.rates(base, FIAT_TARGETS);
        BigDecimal usdPerUsdc = coinGecko.usdPerUsdc();

        Map<String, BigDecimal> out = new LinkedHashMap<>();
        for (String target : FIAT_TARGETS) {
            BigDecimal rate = fiatRates.get(target.toUpperCase());
            if (rate == null) {
                throw new IllegalStateException("Sin tasa para " + target);
            }
            out.put(target, amount.multiply(rate).setScale(SCALE, RoundingMode.HALF_UP));
        }
        // USDC: amount está en USD; 1 USDC = usdPerUsdc USD → USDC = USD / usdPerUsdc.
        BigDecimal usdcAmount = amount.divide(usdPerUsdc, SCALE + 6, RoundingMode.HALF_UP)
                .setScale(SCALE, RoundingMode.HALF_UP);
        out.put(USDC, usdcAmount);
        return out;
    }
}
