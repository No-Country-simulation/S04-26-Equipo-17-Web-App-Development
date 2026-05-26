package com.northpay.backend.onboarding.contract;

/**
 * Términos económicos y datos de la empresa contratante usados por el
 * generador del PDF del contrato y por el endpoint GET /contract-details.
 *
 * Duplica intencionalmente los literales de {@code InvitationService} (Rol A)
 * para no cruzar fronteras de paquete. Si en el futuro se migra a yml o a una
 * tabla, esta clase es el único punto a refactorizar.
 */
public final class ContractTerms {

    public static final String COMPANY_NAME = "Lattice & Loop, Inc.";
    public static final String MONTHLY_AMOUNT = "5,200";
    public static final String CURRENCY_PRIMARY = "USD";
    public static final String CURRENCY_ALTERNATE = "COP";
    public static final int DURATION_MONTHS = 12;
    public static final int DAYS_UNTIL_START = 15;
    public static final int CONFIDENTIALITY_YEARS = 3;
    public static final String SERVICES_DESCRIPTION = "diseño de producto";
    public static final int PAYMENT_DAY_OF_MONTH = 5;

    private ContractTerms() {}
}
