package com.northpay.backend.invitation;

import com.northpay.backend.notification.NotificationService;
import com.northpay.backend.notification.PusherEvent;
import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SlaScheduler {

    private final OnboardingRepository onboardingRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * * * *") // cada hora en punto
    public void checkSlaBreaches() {
        log.info("Iniciando verificación de SLA...");
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);

        List<Onboarding> stale = onboardingRepository.findStaleOnboardings(cutoff);

        if (stale.isEmpty()) {
            log.info("Ningún onboarding excede las 48h sin cambios.");
            return;
        }

        log.warn("Se encontraron {} onboardings que exceden 48h sin cambios.", stale.size());

        for (Onboarding o : stale) {
            String email = o.getContractor() != null ? o.getContractor().getEmail() : null;
            if (email == null) {
                log.warn("Onboarding {} sin email de contratista; se omite notificación SLA.", o.getId());
                continue;
            }

            long hoursSinceUpdate = ChronoUnit.HOURS.between(o.getUpdatedAt(), LocalDateTime.now());

            PusherEvent event = PusherEvent.slaBreach(
                    o.getId(),
                    email,
                    o.getStatus().name(),
                    hoursSinceUpdate
            );

            notificationService.notifyEvent(event, "admin@northpay.com");
        }

        log.info("Verificación de SLA finalizada. {} alertas enviadas.", stale.size());
    }
}