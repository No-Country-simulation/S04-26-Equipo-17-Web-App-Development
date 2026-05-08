package com.northpay.backend.invitation;

import com.northpay.backend.invitation.dto.InvitationRequest;
import com.northpay.backend.invitation.dto.InvitationResponse;
import com.northpay.backend.notification.EmailService;
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
    //private final OnboardingRepository onboardingRepository;
    private final EmailService emailService;

    @Transactional
    public InvitationResponse sendInvitation(InvitationRequest request) {
        //Buscar o crear al contratista
        Contractor contractor = contractorRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    Contractor newContractor = new Contractor();
                    newContractor.setEmail(request.email());
                    return contractorRepository.save(newContractor);
                });


        // Generar token y expiración
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(24);


        // Crear onboarding
//        Onboarding onboarding = Onboarding.builder()
//                .contractorId(contractor.getId())
//                .currentStep(1)
//                .status(OnboardingStatus.INVITED)
//                .invitationToken(token)
//                .tokenExpiresAt(expiresAt)
//                .startedAt(now)
//                .updatedAt(now)
//                .build();
//        onboarding = onboardingRepository.save(onboarding);

        // Disparar envío de email (próximo paso)
        emailService.sendInvitationEmail(contractor.getEmail(), token);

        log.info("Email enviado al contratista: {}", contractor.getEmail());

        // Construir respuesta
        String invitationLink = "https://northpay-s04-26-e17.pages.dev/onboarding?token=" + token;
        return InvitationResponse.builder()
                .onboardingId(1L)
                .token(token)
                .expiresAt(expiresAt)
                .invitationLink(invitationLink)
                .build();
    }
}