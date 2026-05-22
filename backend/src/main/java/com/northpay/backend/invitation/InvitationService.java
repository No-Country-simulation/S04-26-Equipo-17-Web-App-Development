package com.northpay.backend.invitation;

import com.northpay.backend.common.config.FrontendConfig;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.ConflictException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
import com.northpay.backend.notification.EmailService;
import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingRepository;
import com.northpay.backend.onboarding.OnboardingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationService {

    private final ContractorRepository contractorRepository;
    private final OnboardingRepository onboardingRepository;
    private final EmailService emailService;
    private final FrontendConfig frontendConfig;
    private final OnboardingService onboardingService;

    @Transactional
    public InvitationResponse sendInvitation(InvitationRequest request) {
        Contractor contractor = contractorRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    Contractor newContractor = new Contractor();
                    newContractor.setEmail(request.email());
                    return contractorRepository.save(newContractor);
                });

        Optional<Onboarding> existing = onboardingRepository
                .findByContractorIdAndStatusNot(contractor.getId(), OnboardingStatus.REJECTED);

        if (existing.isPresent()) {
            Onboarding onboard = existing.get();
            String timeRemaining = formatTimeRemaining(onboard.getTokenExpiresAt());

            Map<String, Object> details = Map.of(
                    "onboardingId", onboard.getId(),
                    "token", onboard.getInvitationToken(),
                    "expiresAt", onboard.getTokenExpiresAt() != null ? onboard.getTokenExpiresAt().toString() : null,
                    "timeRemaining", timeRemaining
            );
            throw new ConflictException(
                    "Ya existe un onboarding activo para el email " + request.email(),
                    details
            );
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(7);

        Onboarding onboarding = Onboarding.builder()
                .contractor(contractor)
                .currentStep(1)
                .status(OnboardingStatus.INVITED)
                .invitationToken(token)
                .tokenExpiresAt(expiresAt)
                .startedAt(now)
                .updatedAt(now)
                .build();
        onboarding = onboardingRepository.save(onboarding);

        emailService.sendInvitationEmail(contractor.getEmail(), token);
        log.info("Invitación enviada: contractor={} onboarding={}", contractor.getEmail(), onboarding.getId());

        String monthlyFee = "USD 5.200";
        String contractDuration = "12 meses";
        String currency = "COP / USD";
        String company = "Lattice & Loop";

        String invitationLink = frontendConfig.url() + frontendConfig.onboardingPath() + "?token=" + token;
        return InvitationResponse.builder()
                .onboardingId(onboarding.getId())
                .token(token)
                .expiresAt(expiresAt)
                .invitationLink(invitationLink)
                .monthlyFee(monthlyFee)
                .contractDuration(contractDuration)
                .currency(currency)
                .company(company)
                .build();
    }

    public InvitationResponse validateToken(String token) {
        Onboarding onboarding = onboardingService.openLink(token);
        Contractor contractor = onboarding.getContractor();

        // Construir el enlace de invitación (mismo que se usa en sendInvitation)
        String invitationLink = frontendConfig.url() + frontendConfig.onboardingPath() + "?token=" + token;

        // Estos valores están hardcodeados en sendInvitation; los mantenemos consistentes.
        String monthlyFee = "USD 5.200";
        String contractDuration = "12 meses";
        String currency = "COP / USD";
        String company = "Lattice & Loop";

        return InvitationResponse.builder()
                .onboardingId(onboarding.getId())
                .token(token)
                .expiresAt(onboarding.getTokenExpiresAt())
                .invitationLink(invitationLink)
                .monthlyFee(monthlyFee)
                .contractDuration(contractDuration)
                .currency(currency)
                .company(company)
                .build();
    }

    @Transactional
    public InvitationResponse resendInvitation(Long onboardingId) {
        Onboarding onboarding = onboardingRepository.findById(onboardingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Onboarding %d no encontrado".formatted(onboardingId)));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        onboarding.setInvitationToken(token);
        onboarding.setTokenExpiresAt(expiresAt);
        onboarding.setUpdatedAt(LocalDateTime.now());
        onboardingRepository.save(onboarding);

        String email = onboarding.getContractor().getEmail();
        emailService.sendInvitationEmail(email, token);

        log.info("Invitación reenviada: onboarding={} contractor={}", onboardingId, email);

        String invitationLink = frontendConfig.url() + frontendConfig.onboardingPath() + "?token=" + token;
        return InvitationResponse.builder()
                .onboardingId(onboarding.getId())
                .token(token)
                .expiresAt(expiresAt)
                .invitationLink(invitationLink)
                .build();
    }

    private String formatTimeRemaining(LocalDateTime expiresAt) {
        if (expiresAt == null) return "sin límite";
        Duration duration = Duration.between(LocalDateTime.now(), expiresAt);
        if (duration.isNegative()) return "expirado";

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (hours > 0) {
            return String.format("%dh %dmin", hours, minutes);
        } else {
            return String.format("%dmin", minutes);
        }
    }
}
