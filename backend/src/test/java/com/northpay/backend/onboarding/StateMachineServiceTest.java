package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.InvalidStateTransitionException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.invitation.Contractor;
import com.northpay.backend.notification.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateMachineServiceTest {

    private static final Long ONBOARDING_ID = 42L;
    private static final String CONTRACTOR_EMAIL = "contratista@test.com";

    @Mock
    private OnboardingRepository onboardingRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StateMachineService stateMachineService;

    // ---------- helpers ----------

    private Onboarding onboardingWith(OnboardingStatus status, Integer step, String email) {
        Contractor contractor = Contractor.builder()
                .id(1L)
                .email(email)
                .fullName("Juan Pérez")
                .build();
        return Onboarding.builder()
                .id(ONBOARDING_ID)
                .contractor(contractor)
                .status(status)
                .currentStep(step)
                .build();
    }

    private void stubFindAndSave(Onboarding onboarding) {
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.of(onboarding));
        when(onboardingRepository.save(any(Onboarding.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------- 1. Transiciones felices Tabla 1 ----------

    static Stream<Arguments> happyTransitions() {
        return Stream.of(
                Arguments.of(OnboardingStatus.INVITED, OnboardingAction.OPEN_LINK,
                        OnboardingStatus.IN_PROGRESS, 1),
                Arguments.of(OnboardingStatus.IN_PROGRESS, OnboardingAction.UPLOAD_DOCUMENTS,
                        OnboardingStatus.DOCUMENTS_UPLOADED, 3),
                Arguments.of(OnboardingStatus.DOCUMENTS_UPLOADED, OnboardingAction.SIGN_CONTRACT,
                        OnboardingStatus.CONTRACT_SIGNED, 4),
                Arguments.of(OnboardingStatus.CONTRACT_SIGNED, OnboardingAction.CONFIGURE_PAYMENT,
                        OnboardingStatus.PAYMENT_CONFIGURED, 5),
                Arguments.of(OnboardingStatus.PAYMENT_CONFIGURED, OnboardingAction.SUBMIT_SELFIE,
                        OnboardingStatus.PENDING_VERIFICATION, 5),
                Arguments.of(OnboardingStatus.CORRECTION_REQUIRED, OnboardingAction.CORRECT_ERROR,
                        OnboardingStatus.IN_PROGRESS, 1),
                Arguments.of(OnboardingStatus.PENDING_VERIFICATION, OnboardingAction.APPROVE,
                        OnboardingStatus.ACTIVATED, 5)
        );
    }

    @ParameterizedTest(name = "{0} + {1} -> {2}, step={3}")
    @MethodSource("happyTransitions")
    void transicionesValidasDeTabla1(OnboardingStatus from,
                                      OnboardingAction action,
                                      OnboardingStatus expectedTo,
                                      int expectedStep) {
        Onboarding onboarding = onboardingWith(from, 1, CONTRACTOR_EMAIL);
        stubFindAndSave(onboarding);

        Onboarding result = stateMachineService.transition(ONBOARDING_ID, action);

        assertThat(result.getStatus()).isEqualTo(expectedTo);
        assertThat(result.getCurrentStep()).isEqualTo(expectedStep);
        verify(onboardingRepository).save(onboarding);
        verify(notificationService).notify(eq(CONTRACTOR_EMAIL), anyString());
    }

    // ---------- 2. REQUEST_CORRECTION desde correctables preserva currentStep ----------

    @ParameterizedTest(name = "REQUEST_CORRECTION desde {0} preserva step")
    @EnumSource(value = OnboardingStatus.class,
            names = {"IN_PROGRESS", "DOCUMENTS_UPLOADED", "CONTRACT_SIGNED",
                    "PAYMENT_CONFIGURED", "PENDING_VERIFICATION"})
    void requestCorrectionDesdeCorrectablesPreservaStep(OnboardingStatus from) {
        int originalStep = 3;
        Onboarding onboarding = onboardingWith(from, originalStep, CONTRACTOR_EMAIL);
        stubFindAndSave(onboarding);

        Onboarding result = stateMachineService.transition(
                ONBOARDING_ID, OnboardingAction.REQUEST_CORRECTION);

        assertThat(result.getStatus()).isEqualTo(OnboardingStatus.CORRECTION_REQUIRED);
        assertThat(result.getCurrentStep()).isEqualTo(originalStep);
    }

    // ---------- 3. REQUEST_CORRECTION desde no-correctable lanza ----------

    @ParameterizedTest(name = "REQUEST_CORRECTION desde {0} lanza")
    @EnumSource(value = OnboardingStatus.class,
            names = {"INVITED", "CORRECTION_REQUIRED", "ACTIVATED", "REJECTED"})
    void requestCorrectionDesdeNoCorrectableLanza(OnboardingStatus from) {
        Onboarding onboarding = onboardingWith(from, 1, CONTRACTOR_EMAIL);
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.of(onboarding));

        assertThatThrownBy(() -> stateMachineService.transition(
                ONBOARDING_ID, OnboardingAction.REQUEST_CORRECTION))
                .isInstanceOf(InvalidStateTransitionException.class);

        verify(onboardingRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    // ---------- 4. REJECT desde rechazables ----------

    @ParameterizedTest(name = "REJECT desde {0}")
    @EnumSource(value = OnboardingStatus.class,
            names = {"INVITED", "IN_PROGRESS", "DOCUMENTS_UPLOADED", "CONTRACT_SIGNED",
                    "PAYMENT_CONFIGURED", "PENDING_VERIFICATION", "CORRECTION_REQUIRED"})
    void rejectDesdeRechazables(OnboardingStatus from) {
        Onboarding onboarding = onboardingWith(from, 2, CONTRACTOR_EMAIL);
        stubFindAndSave(onboarding);

        Onboarding result = stateMachineService.transition(ONBOARDING_ID, OnboardingAction.REJECT);

        assertThat(result.getStatus()).isEqualTo(OnboardingStatus.REJECTED);
    }

    // ---------- 5. REJECT desde ACTIVATED/REJECTED lanza ----------

    @ParameterizedTest
    @EnumSource(value = OnboardingStatus.class, names = {"ACTIVATED", "REJECTED"})
    void rejectDesdeEstadoFinalLanza(OnboardingStatus from) {
        Onboarding onboarding = onboardingWith(from, 5, CONTRACTOR_EMAIL);
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.of(onboarding));

        assertThatThrownBy(() -> stateMachineService.transition(ONBOARDING_ID, OnboardingAction.REJECT))
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    // ---------- 6. Transición arbitraria inválida ----------

    @Test
    @DisplayName("INVITED + UPLOAD_DOCUMENTS lanza InvalidStateTransitionException")
    void transicionArbitrariaInvalidaLanza() {
        Onboarding onboarding = onboardingWith(OnboardingStatus.INVITED, 1, CONTRACTOR_EMAIL);
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.of(onboarding));

        assertThatThrownBy(() -> stateMachineService.transition(
                ONBOARDING_ID, OnboardingAction.UPLOAD_DOCUMENTS))
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("UPLOAD_DOCUMENTS")
                .hasMessageContaining("INVITED");

        verify(onboardingRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    // ---------- 6b. No se puede saltar pasos hacia adelante ----------

    static Stream<Arguments> stepSkips() {
        return Stream.of(
                // saltar firma de contrato sin haber subido documentos
                Arguments.of(OnboardingStatus.IN_PROGRESS, OnboardingAction.SIGN_CONTRACT),
                // saltar del paso 2 al 4 (pago) sin firmar contrato
                Arguments.of(OnboardingStatus.IN_PROGRESS, OnboardingAction.CONFIGURE_PAYMENT),
                Arguments.of(OnboardingStatus.DOCUMENTS_UPLOADED, OnboardingAction.CONFIGURE_PAYMENT),
                // saltar a selfie sin configurar pago
                Arguments.of(OnboardingStatus.DOCUMENTS_UPLOADED, OnboardingAction.SUBMIT_SELFIE),
                Arguments.of(OnboardingStatus.CONTRACT_SIGNED, OnboardingAction.SUBMIT_SELFIE),
                // re-firmar un contrato ya firmado
                Arguments.of(OnboardingStatus.CONTRACT_SIGNED, OnboardingAction.SIGN_CONTRACT)
        );
    }

    @ParameterizedTest(name = "{0} + {1} (salto) lanza")
    @MethodSource("stepSkips")
    void noSePuedeSaltarPasos(OnboardingStatus from, OnboardingAction action) {
        Onboarding onboarding = onboardingWith(from, 2, CONTRACTOR_EMAIL);
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.of(onboarding));

        assertThatThrownBy(() -> stateMachineService.transition(ONBOARDING_ID, action))
                .isInstanceOf(InvalidStateTransitionException.class);

        verify(onboardingRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    // ---------- 7. ResourceNotFoundException si onboarding no existe ----------

    @Test
    void onboardingInexistenteLanzaResourceNotFound() {
        when(onboardingRepository.findById(ONBOARDING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stateMachineService.transition(ONBOARDING_ID, OnboardingAction.OPEN_LINK))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.valueOf(ONBOARDING_ID));

        verifyNoInteractions(notificationService);
    }

    // ---------- 8. NotificationService.notify se invoca con email correcto ----------

    @Test
    void notifyInvocadoConEmailDelContractor() {
        Onboarding onboarding = onboardingWith(OnboardingStatus.INVITED, 1, CONTRACTOR_EMAIL);
        stubFindAndSave(onboarding);

        stateMachineService.transition(ONBOARDING_ID, OnboardingAction.OPEN_LINK);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1))
                .notify(emailCaptor.capture(), messageCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo(CONTRACTOR_EMAIL);
        assertThat(messageCaptor.getValue())
                .contains("INVITED")
                .contains("IN_PROGRESS")
                .contains("OPEN_LINK");
    }

    // ---------- 9. Sin email no se notifica ----------

    @Test
    void sinEmailNoSeNotifica() {
        Onboarding onboarding = onboardingWith(OnboardingStatus.INVITED, 1, null);
        stubFindAndSave(onboarding);

        stateMachineService.transition(ONBOARDING_ID, OnboardingAction.OPEN_LINK);

        verifyNoInteractions(notificationService);
    }
}
