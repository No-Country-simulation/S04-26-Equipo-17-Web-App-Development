package com.northpay.backend.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PusherEvent(
        String type,
        Long onboardingId,
        String contractorEmail,
        String previousStatus,
        String newStatus,
        String action,
        String message,
        LocalDateTime timestamp
) {
    public static PusherEvent statusChanged(Long onboardingId, String contractorEmail,
                                            String previousStatus, String newStatus,
                                            String action) {
        return new PusherEvent(
                "status-changed",
                onboardingId,
                contractorEmail,
                previousStatus,
                newStatus,
                action,
                "Onboarding #%d: %s → %s".formatted(onboardingId, previousStatus, newStatus),
                LocalDateTime.now()
        );
    }

    public static PusherEvent correctionRequested(Long onboardingId, String contractorEmail,
                                                  String step, String observations) {
        return new PusherEvent(
                "correction-requested",
                onboardingId,
                contractorEmail,
                null,
                null,
                step,
                "Corrección solicitada en Paso %s: %s".formatted(step, observations),
                LocalDateTime.now()
        );
    }

    public static PusherEvent activationComplete(Long onboardingId, String contractorEmail) {
        return new PusherEvent(
                "activation-complete",
                onboardingId,
                contractorEmail,
                null,
                null,
                null,
                "Onboarding #%d activado".formatted(onboardingId),
                LocalDateTime.now()
        );
    }

    public static PusherEvent slaBreach(Long onboardingId, String contractorEmail,
                                        String status, Long hoursSinceUpdate) {
        return new PusherEvent(
                "sla-breach",
                onboardingId,
                contractorEmail,
                null,
                null,
                null,
                "Onboarding #%d lleva %d horas sin cambios (estado: %s)"
                        .formatted(onboardingId, hoursSinceUpdate, status),
                LocalDateTime.now()
        );
    }
}