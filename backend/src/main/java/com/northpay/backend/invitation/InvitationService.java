package com.northpay.backend.invitation;

import com.northpay.backend.common.config.FrontendConfig;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.ConflictException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
import com.northpay.backend.invitation.dto.TokenValidationResponse;
import com.northpay.backend.notification.EmailService;
import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingRepository;
import com.northpay.backend.onboarding.OnboardingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        if (onboardingRepository.existsByContractorIdAndStatusNot(
                contractor.getId(), OnboardingStatus.REJECTED)) {
            throw new ConflictException(
                    "Ya existe un onboarding activo para el email " + request.email());
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(24);

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

        String invitationLink = frontendConfig.url() + frontendConfig.onboardingPath() + "?token=" + token;
        return InvitationResponse.builder()
                .onboardingId(onboarding.getId())
                .token(token)
                .expiresAt(expiresAt)
                .invitationLink(invitationLink)
                .build();
    }

    public TokenValidationResponse validateToken(String token) {
        Onboarding onboarding = onboardingService.openLink(token);

        Contractor contractor = onboarding.getContractor();

        return new TokenValidationResponse(
                onboarding.getId(),
                token,
                onboarding.getCurrentStep(),
                onboarding.getStatus().name(),
                contractor.getFullName(),
                contractor.getEmail()
        );
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
}
