package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.InvalidStateTransitionException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.invitation.Contractor;
import com.northpay.backend.notification.NotificationService;
import com.northpay.backend.notification.PusherEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.northpay.backend.common.enums.OnboardingStatus.*;
import static com.northpay.backend.onboarding.OnboardingAction.*;

@Service
@RequiredArgsConstructor
public class StateMachineService {

    private static final Logger log = LoggerFactory.getLogger(StateMachineService.class);

    private record TransitionKey(OnboardingStatus from, OnboardingAction action) {}

    private record TransitionTarget(OnboardingStatus to, Integer nextStep) {}

    private static final Map<TransitionKey, TransitionTarget> TRANSITIONS = Map.ofEntries(
            Map.entry(new TransitionKey(INVITED, OPEN_LINK),
                    new TransitionTarget(IN_PROGRESS, 1)),
            Map.entry(new TransitionKey(IN_PROGRESS, UPLOAD_DOCUMENTS),
                    new TransitionTarget(DOCUMENTS_UPLOADED, 3)),
            Map.entry(new TransitionKey(DOCUMENTS_UPLOADED, SIGN_CONTRACT),
                    new TransitionTarget(CONTRACT_SIGNED, 4)),
            Map.entry(new TransitionKey(CONTRACT_SIGNED, CONFIGURE_PAYMENT),
                    new TransitionTarget(PAYMENT_CONFIGURED, 5)),
            Map.entry(new TransitionKey(PAYMENT_CONFIGURED, SUBMIT_SELFIE),
                    new TransitionTarget(PENDING_VERIFICATION, 5)),
            Map.entry(new TransitionKey(CORRECTION_REQUIRED, CORRECT_ERROR),
                    new TransitionTarget(IN_PROGRESS, 1)),
            Map.entry(new TransitionKey(PENDING_VERIFICATION, APPROVE),
                    new TransitionTarget(ACTIVATED, 5))
    );

    private static final Set<OnboardingStatus> CORRECTABLE_FROM = Set.of(
            IN_PROGRESS, DOCUMENTS_UPLOADED, CONTRACT_SIGNED,
            PAYMENT_CONFIGURED, PENDING_VERIFICATION
    );

    private static final Set<OnboardingStatus> REJECTABLE_FROM = Set.of(
            INVITED, IN_PROGRESS, DOCUMENTS_UPLOADED, CONTRACT_SIGNED,
            PAYMENT_CONFIGURED, PENDING_VERIFICATION, CORRECTION_REQUIRED
    );

    private final OnboardingRepository onboardingRepository;
    private final NotificationService notificationService;

    @Transactional
    public Onboarding transition(Long onboardingId, OnboardingAction action) {
        Onboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Onboarding %d no encontrado".formatted(onboardingId)));

        OnboardingStatus from = onboarding.getStatus();
        TransitionTarget target = resolveTarget(from, action);

        onboarding.setStatus(target.to());
        if (target.nextStep() != null) {
            onboarding.setCurrentStep(target.nextStep());
        }


        Onboarding saved = onboardingRepository.save(onboarding);

        log.info("Onboarding {} transición {} -> {} (acción {})",
                onboardingId, from, target.to(), action);

        notifyTransition(saved, from, target.to(), action);

        return saved;
    }

    private TransitionTarget resolveTarget(OnboardingStatus from, OnboardingAction action) {
        if (action == REQUEST_CORRECTION) {
            if (!CORRECTABLE_FROM.contains(from)) {
                throw invalidTransition(from, action);
            }
            // Preserva current_step: el operador indicará el paso fallido en su propio endpoint.
            return new TransitionTarget(CORRECTION_REQUIRED, null);
        }
        if (action == REJECT) {
            if (!REJECTABLE_FROM.contains(from)) {
                throw invalidTransition(from, action);
            }
            return new TransitionTarget(REJECTED, null);
        }

        TransitionTarget target = TRANSITIONS.get(new TransitionKey(from, action));
        if (target == null) {
            throw invalidTransition(from, action);
        }
        return target;
    }

    private InvalidStateTransitionException invalidTransition(OnboardingStatus from, OnboardingAction action) {
        return new InvalidStateTransitionException(
                "Transición inválida: no se puede aplicar %s desde %s".formatted(action, from));
    }

    private void notifyTransition(Onboarding onboarding,
                                  OnboardingStatus from,
                                  OnboardingStatus to,
                                  OnboardingAction action) {
        String email = Optional.ofNullable(onboarding.getContractor())
                .map(Contractor::getEmail)
                .orElse(null);
        if (email == null) {
            log.warn("Onboarding {} sin contractor.email; se omite notificación", onboarding.getId());
            return;
        }

        PusherEvent event = PusherEvent.statusChanged(
                onboarding.getId(),
                email,
                from.name(),
                to.name(),
                action.name()
        );

        notificationService.notifyEvent(event, email);
    }
}
