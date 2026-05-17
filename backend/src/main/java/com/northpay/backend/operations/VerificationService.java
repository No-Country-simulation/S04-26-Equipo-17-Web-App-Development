package com.northpay.backend.operations;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.InvalidStateTransitionException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.document.Document;
import com.northpay.backend.document.DocumentRepository;
import com.northpay.backend.notification.NotificationService;
import com.northpay.backend.notification.PusherEvent;
import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingAction;
import com.northpay.backend.onboarding.OnboardingRepository;
import com.northpay.backend.onboarding.StateMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final OnboardingRepository onboardingRepository;
    private final StateMachineService stateMachineService;
    private final DocumentRepository documentRepository;
    private final NotificationService notificationService;

    @Transactional
    public Onboarding requestCorrection(Long onboardingId, Integer stepNumber, String observations) {
        Onboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding %d no encontrado".formatted(onboardingId)));

        // Validar que el estado actual permite corrección
        if (!isCorrectable(onboarding.getStatus())) {
            throw new InvalidStateTransitionException(
                    "No se puede corregir un onboarding en estado " + onboarding.getStatus());
        }

        // Ejecutar transición a CORRECTION_REQUIRED
        Onboarding updated = stateMachineService.transition(onboardingId, OnboardingAction.REQUEST_CORRECTION);

        // Forzar current_step al paso fallido
        updated.setCurrentStep(stepNumber);
        onboardingRepository.save(updated);

        // Lógica de cascada: si el paso rechazado es el 2, invalidar contrato
        if (stepNumber == 2) {
            invalidateContract(onboardingId);
        }

        // Notificar al contratista
        notifyCorrection(updated, stepNumber, observations);

        return updated;
    }

    private boolean isCorrectable(OnboardingStatus status) {
        return switch (status) {
            case IN_PROGRESS, DOCUMENTS_UPLOADED, CONTRACT_SIGNED,
                 PAYMENT_CONFIGURED, PENDING_VERIFICATION -> true;
            default -> false;
        };
    }

    private void invalidateContract(Long onboardingId) {
        Optional<Document> contract = documentRepository
                .findByOnboardingIdAndDocType(onboardingId, DocumentType.SIGNED_CONTRACT);
        contract.ifPresent(doc -> {
            doc.setStatus(OnboardingStatus.CORRECTION_REQUIRED);
            documentRepository.save(doc);
        });
    }

    private void notifyCorrection(Onboarding onboarding, Integer stepNumber, String observations) {
        String email = onboarding.getContractor().getEmail();
        PusherEvent event = PusherEvent.correctionRequested(
                onboarding.getId(),
                email,
                String.valueOf(stepNumber),
                observations
        );
        notificationService.notifyEvent(event, email);
    }

    @Transactional
    public Onboarding activate(Long onboardingId) {
        Onboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Onboarding %d no encontrado".formatted(onboardingId)));

        if (onboarding.getStatus() != OnboardingStatus.PENDING_VERIFICATION) {
            throw new InvalidStateTransitionException(
                    "Solo se puede activar un onboarding en PENDING_VERIFICATION");
        }

        Onboarding updated = stateMachineService.transition(onboardingId, OnboardingAction.APPROVE);
        updated.setCompletedAt(LocalDateTime.now());
        onboardingRepository.save(updated);

        notifyActivation(updated);
        return updated;
    }

    @Transactional
    public Onboarding reject(Long onboardingId) {
        Onboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Onboarding %d no encontrado".formatted(onboardingId)));

        Onboarding updated = stateMachineService.transition(onboardingId, OnboardingAction.REJECT);

        notifyRejection(updated);
        return updated;
    }

    private void notifyActivation(Onboarding onboarding) {
        String email = onboarding.getContractor().getEmail();
        PusherEvent event = PusherEvent.activationComplete(onboarding.getId(), email);
        notificationService.notifyEvent(event, email);
    }

    private void notifyRejection(Onboarding onboarding) {
        String email = onboarding.getContractor().getEmail();
        String message = "Tu cuenta ha sido rechazada. Contacta a soporte.";
        notificationService.notify(email, message);
    }
}