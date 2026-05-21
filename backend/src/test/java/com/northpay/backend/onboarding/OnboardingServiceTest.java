package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.EventType;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.InvalidStateTransitionException;
import com.northpay.backend.document.Document;
import com.northpay.backend.document.DocumentService;
import com.northpay.backend.invitation.Contractor;
import com.northpay.backend.invitation.ContractorRepository;
import com.northpay.backend.onboarding.dto.CorrectionCommentResponse;
import com.northpay.backend.onboarding.dto.Step4PaymentRequest;
import com.northpay.backend.payment.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    private static final Long ID = 7L;
    private static final String TOKEN = "tok-123";

    @Mock private OnboardingRepository onboardingRepository;
    @Mock private ContractorRepository contractorRepository;
    @Mock private StateMachineService stateMachineService;
    @Mock private DocumentService documentService;
    @Mock private ContractPdfService contractPdfService;
    @Mock private PaymentService paymentService;
    @Mock private EventHistoryRepository eventHistoryRepository;

    private OnboardingService service() {
        return new OnboardingService(onboardingRepository, contractorRepository,
                stateMachineService, documentService, contractPdfService,
                paymentService, eventHistoryRepository);
    }

    private Onboarding onboarding(OnboardingStatus status) {
        Contractor contractor = Contractor.builder()
                .id(1L).email("c@test.com").fullName("Juan Pérez").countryIso("MX").build();
        return Onboarding.builder()
                .id(ID).contractor(contractor).status(status).currentStep(3)
                .invitationToken(TOKEN).build();
    }

    // ---------- signContract ----------

    @Test
    void signContractDesdeEstadoIncorrectoLanza() {
        when(onboardingRepository.findById(ID))
                .thenReturn(Optional.of(onboarding(OnboardingStatus.IN_PROGRESS)));

        assertThatThrownBy(() -> service().signContract(ID, TOKEN))
                .isInstanceOf(InvalidStateTransitionException.class);

        verifyNoInteractions(stateMachineService);
        verify(documentService, never()).storeOrReplaceGenerated(any(), any(), any(), any(), any());
    }

    @Test
    void signContractHappyFirmaYTransiciona() {
        Onboarding ob = onboarding(OnboardingStatus.DOCUMENTS_UPLOADED);
        Onboarding signed = onboarding(OnboardingStatus.CONTRACT_SIGNED);
        signed.setCurrentStep(4);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));
        when(contractPdfService.generate(ob)).thenReturn(new byte[]{1, 2, 3});
        when(documentService.storeOrReplaceGenerated(eq(ob), eq(DocumentType.SIGNED_CONTRACT),
                any(), anyString(), eq("application/pdf")))
                .thenReturn(Document.builder().fileUrl("https://x/contrato.pdf").build());
        when(stateMachineService.transition(ID, OnboardingAction.SIGN_CONTRACT)).thenReturn(signed);

        OnboardingService.ContractSignResult result = service().signContract(ID, TOKEN);

        assertThat(result.documentUrl()).isEqualTo("https://x/contrato.pdf");
        assertThat(result.onboarding().getStatus()).isEqualTo(OnboardingStatus.CONTRACT_SIGNED);
        assertThat(result.onboarding().getCurrentStep()).isEqualTo(4);
    }

    // ---------- contract-preview ----------

    @Test
    void contractPreviewDesdeInvitedLanza() {
        Onboarding ob = onboarding(OnboardingStatus.INVITED);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));

        assertThatThrownBy(() -> service().generateContractPreview(ID, TOKEN))
                .isInstanceOf(InvalidStateTransitionException.class);

        verifyNoInteractions(contractPdfService);
    }

    @Test
    void contractPreviewHappyDevuelvePdfSinPersistir() {
        Onboarding ob = onboarding(OnboardingStatus.DOCUMENTS_UPLOADED);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));
        when(contractPdfService.generate(ob)).thenReturn(new byte[]{9, 9});

        byte[] pdf = service().generateContractPreview(ID, TOKEN);

        assertThat(pdf).containsExactly(9, 9);
        verifyNoInteractions(stateMachineService);
        verify(documentService, never()).storeOrReplaceGenerated(any(), any(), any(), any(), any());
    }

    // ---------- configurePayment ----------

    @Test
    void configurePaymentDesdeEstadoIncorrectoLanza() {
        when(onboardingRepository.findById(ID))
                .thenReturn(Optional.of(onboarding(OnboardingStatus.IN_PROGRESS)));

        assertThatThrownBy(() -> service().configurePayment(ID, TOKEN,
                new Step4PaymentRequest("BANK", Map.of("iban", "X"))))
                .isInstanceOf(InvalidStateTransitionException.class);

        verifyNoInteractions(stateMachineService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void configurePaymentHappyGuardaYTransiciona() {
        Onboarding ob = onboarding(OnboardingStatus.CONTRACT_SIGNED);
        Onboarding done = onboarding(OnboardingStatus.PAYMENT_CONFIGURED);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));
        when(stateMachineService.transition(ID, OnboardingAction.CONFIGURE_PAYMENT)).thenReturn(done);

        Onboarding result = service().configurePayment(ID, TOKEN,
                new Step4PaymentRequest("BANK", Map.of("iban", "ES00")));

        assertThat(result.getStatus()).isEqualTo(OnboardingStatus.PAYMENT_CONFIGURED);
        verify(paymentService).savePaymentMethod(eq(1L), eq("BANK"), anyMap());
    }

    // ---------- submitSelfie ----------

    @Test
    void submitSelfieDesdeEstadoIncorrectoLanza() {
        when(onboardingRepository.findById(ID))
                .thenReturn(Optional.of(onboarding(OnboardingStatus.CONTRACT_SIGNED)));
        MockMultipartFile file = new MockMultipartFile("file", "s.jpg", "image/jpeg", new byte[]{1});

        assertThatThrownBy(() -> service().submitSelfie(ID, TOKEN, file))
                .isInstanceOf(InvalidStateTransitionException.class);

        verifyNoInteractions(stateMachineService);
        verify(documentService, never()).store(any(), any(), any());
    }

    @Test
    void submitSelfieHappySubeYTransiciona() {
        Onboarding ob = onboarding(OnboardingStatus.PAYMENT_CONFIGURED);
        Onboarding done = onboarding(OnboardingStatus.PENDING_VERIFICATION);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));
        when(stateMachineService.transition(ID, OnboardingAction.SUBMIT_SELFIE)).thenReturn(done);
        MockMultipartFile file = new MockMultipartFile("file", "s.jpg", "image/jpeg", new byte[]{1});

        Onboarding result = service().submitSelfie(ID, TOKEN, file);

        assertThat(result.getStatus()).isEqualTo(OnboardingStatus.PENDING_VERIFICATION);
        verify(documentService).store(ob, DocumentType.SELFIE, file);
    }

    // ---------- getCorrectionComments ----------

    @Test
    void getCorrectionCommentsMapeaEventHistory() {
        Onboarding ob = onboarding(OnboardingStatus.CORRECTION_REQUIRED);
        when(onboardingRepository.findById(ID)).thenReturn(Optional.of(ob));
        EventHistory eh = new EventHistory();
        when(eventHistoryRepository.findByOnboardingIdAndEventOrderByCreatedAtAsc(
                ID, EventType.CORRECTION_REQUESTED)).thenReturn(List.of(eh));

        List<CorrectionCommentResponse> result = service().getCorrectionComments(ID, TOKEN);

        assertThat(result).hasSize(1);
    }
}
