package com.northpay.backend.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio compartido de métodos de pago (paquete payment/, Rol B).
 * Expuesto como @Service para que otros roles lo inyecten.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Upsert del método de pago activo del contratista: actualiza el existente
     * o crea uno nuevo si no hay ninguno activo.
     */
    @Transactional
    public PaymentMethod savePaymentMethod(Long contractorId,
                                           String accountType,
                                           Map<String, Object> details) {
        Map<String, Object> safeDetails = details != null ? details : new HashMap<>();
        PaymentMethod method = paymentRepository.findByContractorIdAndIsActiveTrue(contractorId)
                .orElseGet(() -> PaymentMethod.builder()
                        .contractorId(contractorId)
                        .isActive(true)
                        .build());
        method.setAccountType(accountType);
        method.setDetailsJson(safeDetails);
        return paymentRepository.save(method);
    }
}
