package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.common.exception.InvalidStateTransitionException;
import com.northpay.backend.common.exception.InvalidTokenException;
import com.northpay.backend.common.exception.ResourceNotFoundException;
import com.northpay.backend.common.exception.TokenExpiredException;
import com.northpay.backend.document.Document;
import com.northpay.backend.document.DocumentService;
import com.northpay.backend.invitation.Contractor;
import com.northpay.backend.invitation.ContractorRepository;
import com.northpay.backend.onboarding.dto.Step1Request;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final ContractorRepository contractorRepository;
    private final StateMachineService stateMachineService;
    private final DocumentService documentService;

    @Transactional
    public Onboarding openLink(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("Token de invitación ausente");
        }
        Onboarding onboarding = onboardingRepository.findByInvitationToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token de invitación inválido"));

        if (onboarding.getTokenExpiresAt() != null
                && onboarding.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("El token de invitación ha expirado");
        }

        if (onboarding.getStatus() == OnboardingStatus.INVITED) {
            return stateMachineService.transition(onboarding.getId(), OnboardingAction.OPEN_LINK);
        }
        // Idempotente: si ya está IN_PROGRESS o posterior, retorna sin cambiar estado.
        return onboarding;
    }

    @Transactional
    public Onboarding updateStep1(Long id, String bearerToken, Step1Request request) {
        Onboarding onboarding = authorize(id, bearerToken);
        requireStatus(onboarding, OnboardingStatus.IN_PROGRESS);

        Contractor contractor = onboarding.getContractor();
        contractor.setFullName(request.fullName());
        contractor.setCountryIso(request.countryIso().toUpperCase());
        contractorRepository.save(contractor);

        onboarding.setCurrentStep(2);
        return onboardingRepository.save(onboarding);
    }

    @Transactional
    public DocumentUploadResult uploadDocuments(Long id,
                                                String bearerToken,
                                                List<MultipartFile> files,
                                                List<DocumentType> types) {
        if (files == null || types == null || files.size() != types.size() || files.isEmpty()) {
            throw new IllegalArgumentException(
                    "Las listas 'files' y 'types' deben tener el mismo tamaño y al menos un elemento");
        }
        Onboarding onboarding = authorize(id, bearerToken);
        requireStatus(onboarding, OnboardingStatus.IN_PROGRESS);

        List<Document> stored = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            stored.add(documentService.store(onboarding, types.get(i), files.get(i)));
        }

        Onboarding updated = stateMachineService.transition(id, OnboardingAction.UPLOAD_DOCUMENTS);
        return new DocumentUploadResult(updated, stored);
    }

    private Onboarding authorize(Long id, String bearerToken) {
        if (bearerToken == null || bearerToken.isBlank()) {
            throw new InvalidTokenException("Token de invitación ausente en Authorization");
        }
        Onboarding onboarding = onboardingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Onboarding %d no encontrado".formatted(id)));
        if (!bearerToken.equals(onboarding.getInvitationToken())) {
            throw new InvalidTokenException("Token no coincide con el onboarding solicitado");
        }
        if (onboarding.getTokenExpiresAt() != null
                && onboarding.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("El token de invitación ha expirado");
        }
        return onboarding;
    }

    private void requireStatus(Onboarding onboarding, OnboardingStatus expected) {
        if (onboarding.getStatus() != expected) {
            throw new InvalidStateTransitionException(
                    "Operación inválida: el onboarding está en estado %s, se esperaba %s"
                            .formatted(onboarding.getStatus(), expected));
        }
    }

    public record DocumentUploadResult(Onboarding onboarding, List<Document> documents) {}
}
