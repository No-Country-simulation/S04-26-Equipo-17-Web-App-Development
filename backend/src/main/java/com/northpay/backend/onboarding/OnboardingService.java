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
import com.northpay.backend.onboarding.dto.Step4PaymentRequest;
import com.northpay.backend.onboarding.dto.CorrectionCommentResponse;
import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.EventType;
import com.northpay.backend.payment.PaymentService;
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
    private final ContractPdfService contractPdfService;
    private final PaymentService paymentService;
    private final EventHistoryRepository eventHistoryRepository;

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
        contractor.setFirstName(request.firstName().trim());
        contractor.setLastName(request.lastName().trim());
        contractor.setPreferredName(
                request.preferredName() == null ? null : request.preferredName().trim());
        contractor.setBirthDate(request.birthDate());
        contractor.setCountryIso(request.countryIso().toUpperCase());
        contractor.setIdDocumentNumber(request.idDocumentNumber().trim());
        contractor.setTaxRegime(request.taxRegime().trim());
        contractor.setPhone(request.phone().trim());
        // full_name se mantiene autocompuesto para no romper ContractPdfService.
        contractor.setFullName(contractor.getFirstName() + " " + contractor.getLastName());
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

    /**
     * HU-03: genera el PDF del contrato con los datos del Paso 1.
     * No cambia estado ni persiste (preview ≠ firma).
     */
    @Transactional(readOnly = true)
    public byte[] generateContractPreview(Long id, String bearerToken) {
        Onboarding onboarding = authorize(id, bearerToken);
        Contractor contractor = onboarding.getContractor();
        if (onboarding.getStatus() == OnboardingStatus.INVITED
                || contractor.getFullName() == null
                || contractor.getFullName().isBlank()) {
            throw new InvalidStateTransitionException(
                    "El contrato no puede generarse: el Paso 1 aún no está completo");
        }
        return contractPdfService.generate(onboarding);
    }

    /**
     * HU-03: el contratista firma. Genera el PDF, lo guarda como SIGNED_CONTRACT
     * y transiciona DOCUMENTS_UPLOADED → CONTRACT_SIGNED (current_step=4).
     */
    @Transactional
    public ContractSignResult signContract(Long id, String bearerToken) {
        Onboarding onboarding = authorize(id, bearerToken);
        requireStatus(onboarding, OnboardingStatus.DOCUMENTS_UPLOADED);

        byte[] pdf = contractPdfService.generate(onboarding);
        Document doc = documentService.storeOrReplaceGenerated(
                onboarding,
                DocumentType.SIGNED_CONTRACT,
                pdf,
                "contrato-%d.pdf".formatted(id),
                "application/pdf");

        Onboarding updated = stateMachineService.transition(id, OnboardingAction.SIGN_CONTRACT);
        return new ContractSignResult(updated, doc.getFileUrl());
    }

    /**
     * HU-04: guarda el método de pago (JSONB) y transiciona
     * CONTRACT_SIGNED → PAYMENT_CONFIGURED (current_step=5).
     */
    @Transactional
    public Onboarding configurePayment(Long id, String bearerToken, Step4PaymentRequest request) {
        Onboarding onboarding = authorize(id, bearerToken);
        requireStatus(onboarding, OnboardingStatus.CONTRACT_SIGNED);

        paymentService.savePaymentMethod(
                onboarding.getContractor().getId(),
                request.accountType(),
                request.details());

        return stateMachineService.transition(id, OnboardingAction.CONFIGURE_PAYMENT);
    }

    /**
     * HU-04: sube la selfie (tipo SELFIE) y transiciona
     * PAYMENT_CONFIGURED → PENDING_VERIFICATION.
     */
    @Transactional
    public Onboarding submitSelfie(Long id, String bearerToken, MultipartFile file) {
        Onboarding onboarding = authorize(id, bearerToken);
        requireStatus(onboarding, OnboardingStatus.PAYMENT_CONFIGURED);

        documentService.store(onboarding, DocumentType.SELFIE, file);

        return stateMachineService.transition(id, OnboardingAction.SUBMIT_SELFIE);
    }

    /**
     * Estado y datos del contratista (incluye email pre-cargado de la invitación
     * para que el frontend del Paso 1 lo muestre como read-only).
     * No cambia estado.
     */
    @Transactional(readOnly = true)
    public Onboarding getStatus(Long id, String bearerToken) {
        return authorize(id, bearerToken);
    }

    /**
     * HU-02: lista los documentos del onboarding. Si {@code type} es no-nulo,
     * filtra por ese tipo. Usado por el frontend para verificar qué subió.
     */
    @Transactional(readOnly = true)
    public List<Document> listDocuments(Long id, String bearerToken, DocumentType type) {
        authorize(id, bearerToken);
        List<Document> all = documentService.findByOnboarding(id);
        if (type == null) {
            return all;
        }
        return all.stream().filter(d -> d.getDocType() == type).toList();
    }

    /**
     * HU-02: devuelve un documento puntual. Valida que pertenezca al onboarding
     * del bearer (evita acceso cruzado entre invitaciones).
     */
    @Transactional(readOnly = true)
    public Document getDocument(Long onboardingId, Long documentId, String bearerToken) {
        authorize(onboardingId, bearerToken);
        Document doc = documentService.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento %d no encontrado".formatted(documentId)));
        if (!doc.getOnboarding().getId().equals(onboardingId)) {
            // Tratamos como "no encontrado" para no filtrar la existencia de IDs ajenos.
            throw new ResourceNotFoundException(
                    "Documento %d no encontrado".formatted(documentId));
        }
        return doc;
    }

    /**
     * HU-02: borra un documento del bucket y de la BD. Permitido en cualquier
     * estado pre-verificación: IN_PROGRESS, DOCUMENTS_UPLOADED, CONTRACT_SIGNED,
     * PAYMENT_CONFIGURED y CORRECTION_REQUIRED. Bloqueado en PENDING_VERIFICATION,
     * ACTIVATED y REJECTED (el operador ya tomó decisiones sobre el documento).
     */
    @Transactional
    public void deleteDocument(Long onboardingId, Long documentId, String bearerToken) {
        Onboarding onboarding = authorize(onboardingId, bearerToken);
        OnboardingStatus s = onboarding.getStatus();
        if (s == OnboardingStatus.PENDING_VERIFICATION
                || s == OnboardingStatus.ACTIVATED
                || s == OnboardingStatus.REJECTED) {
            throw new InvalidStateTransitionException(
                    "No se puede borrar documentos en estado %s".formatted(s));
        }

        Document doc = documentService.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Documento %d no encontrado".formatted(documentId)));
        if (!doc.getOnboarding().getId().equals(onboardingId)) {
            throw new ResourceNotFoundException(
                    "Documento %d no encontrado".formatted(documentId));
        }

        // TODO: cuando exista documents.needs_resign (HU correcciones),
        // marcar el SIGNED_CONTRACT como needs_resign=true si se borra
        // un documento de identidad con status >= CONTRACT_SIGNED.

        documentService.delete(doc);
    }

    /**
     * HU-09: comentarios de corrección. Lee event_history con
     * event=CORRECTION_REQUESTED ordenado por fecha.
     */
    @Transactional(readOnly = true)
    public List<CorrectionCommentResponse> getCorrectionComments(Long id, String bearerToken) {
        authorize(id, bearerToken);
        return eventHistoryRepository
                .findByOnboardingIdAndEventOrderByCreatedAtAsc(id, EventType.CORRECTION_REQUESTED)
                .stream()
                .map(e -> new CorrectionCommentResponse(
                        e.getObservations(),
                        e.getPreviousStatus(),
                        e.getNewStatus(),
                        e.getCreatedAt()))
                .toList();
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

    public record ContractSignResult(Onboarding onboarding, String documentUrl) {}
}
