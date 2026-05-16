package com.northpay.backend.payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Long CONTRACTOR_ID = 1L;

    @Mock private PaymentRepository paymentRepository;
    @InjectMocks private PaymentService paymentService;

    @Test
    void insertaCuandoNoHayMetodoActivo() {
        when(paymentRepository.findByContractorIdAndIsActiveTrue(CONTRACTOR_ID))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(PaymentMethod.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        paymentService.savePaymentMethod(CONTRACTOR_ID, "BANK", Map.of("iban", "ES00"));

        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        org.mockito.Mockito.verify(paymentRepository).save(captor.capture());
        PaymentMethod saved = captor.getValue();
        assertThat(saved.getContractorId()).isEqualTo(CONTRACTOR_ID);
        assertThat(saved.getAccountType()).isEqualTo("BANK");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getDetailsJson()).containsEntry("iban", "ES00");
    }

    @Test
    void actualizaElMetodoActivoExistente() {
        PaymentMethod existing = PaymentMethod.builder()
                .id(99L).contractorId(CONTRACTOR_ID).accountType("OLD")
                .isActive(true).build();
        when(paymentRepository.findByContractorIdAndIsActiveTrue(CONTRACTOR_ID))
                .thenReturn(Optional.of(existing));
        when(paymentRepository.save(any(PaymentMethod.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod result = paymentService.savePaymentMethod(
                CONTRACTOR_ID, "CRYPTO", Map.of("wallet", "0xabc"));

        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getAccountType()).isEqualTo("CRYPTO");
        assertThat(result.getDetailsJson()).containsEntry("wallet", "0xabc");
    }

    @Test
    void detallesNullSeGuardaComoMapaVacio() {
        when(paymentRepository.findByContractorIdAndIsActiveTrue(CONTRACTOR_ID))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(PaymentMethod.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod result = paymentService.savePaymentMethod(CONTRACTOR_ID, "BANK", null);

        assertThat(result.getDetailsJson()).isNotNull().isEmpty();
    }
}
