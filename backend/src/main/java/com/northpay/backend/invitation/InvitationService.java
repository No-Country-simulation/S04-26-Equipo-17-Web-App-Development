package com.northpay.backend.invitation;

import com.northpay.backend.common.config.FrontendConfig;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.ConflictException;
import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
import com.northpay.backend.notification.EmailService;
import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingRepository;
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
}
