package com.northpay.backend.onboarding;

import com.northpay.backend.common.dto.ApiResponseBackend;
import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.exception.InvalidTokenException;
import com.northpay.backend.document.Document;
import com.northpay.backend.onboarding.dto.ContractSignResponse;
import com.northpay.backend.onboarding.dto.DocumentResponse;
import com.northpay.backend.onboarding.dto.DocumentsUploadResponse;
import com.northpay.backend.onboarding.dto.CorrectionCommentResponse;
import com.northpay.backend.onboarding.dto.OnboardingStatusResponse;
import com.northpay.backend.onboarding.dto.Step1Request;
import com.northpay.backend.onboarding.dto.Step1Response;
import com.northpay.backend.onboarding.dto.Step4PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
@Tag(name = "Onboarding", description = "Flujo de onboarding del contratista (Pasos 1-5)")
public class OnboardingController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final OnboardingService onboardingService;

    @PutMapping("/{id}/step1")
    @Operation(summary = "Paso 1: guardar datos personales del contratista",
            description = "Actualiza nombre completo y código de país. Requiere Authorization: Bearer {invitation_token}.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Datos guardados"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite la operación"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<Step1Response>> updateStep1(
            @PathVariable
            @Schema(description = "ID del onboarding",
                    example = "1",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Step1Request request) {

        String token = extractBearer(authHeader);
        Onboarding updated = onboardingService.updateStep1(id, token, request);

        Step1Response payload = new Step1Response(
                updated.getId(),
                updated.getStatus(),
                updated.getCurrentStep(),
                updated.getContractor().getFirstName(),
                updated.getContractor().getLastName(),
                updated.getContractor().getPreferredName(),
                updated.getContractor().getBirthDate(),
                updated.getContractor().getCountryIso(),
                updated.getContractor().getIdDocumentNumber(),
                updated.getContractor().getTaxRegime(),
                updated.getContractor().getPhone(),
                updated.getContractor().getEmail(),
                updated.getUpdatedAt());
        return ResponseEntity.ok(ApiResponseBackend.ok("Paso 1 guardado", payload));
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Paso 2: subir documentos de identidad",
            description = "Sube uno o más archivos a Supabase Storage y los registra en la tabla documents. "
                    + "Requiere arrays paralelos 'files' y 'types' del mismo tamaño. "
                    + "Al completar dispara la transición a DOCUMENTS_UPLOADED.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documentos subidos"),
            @ApiResponse(responseCode = "400", description = "Listas inconsistentes o archivo inválido"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite la operación"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<DocumentsUploadResponse>> uploadDocuments(
            @PathVariable
            @Schema(description = "ID del onboarding",
                    example = "1",
                    requiredMode = Schema.RequiredMode.REQUIRED)
            Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("types") List<DocumentType> types) {

        String token = extractBearer(authHeader);
        OnboardingService.DocumentUploadResult result = onboardingService.uploadDocuments(id, token, files, types);

        List<DocumentResponse> docs = result.documents().stream()
                .map(this::toDocumentResponse)
                .toList();

        DocumentsUploadResponse payload = new DocumentsUploadResponse(
                result.onboarding().getId(),
                result.onboarding().getStatus(),
                result.onboarding().getCurrentStep(),
                docs);

        return ResponseEntity.ok(ApiResponseBackend.ok("Documentos subidos", payload));
    }

    @GetMapping("/{id}/contract-preview")
    @Operation(summary = "Paso 3: previsualizar el contrato (PDF)",
            description = "Genera el PDF del contrato con los datos del Paso 1 y lo devuelve "
                    + "como binario application/pdf. NO cambia el estado ni persiste el documento.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "PDF del contrato"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "El Paso 1 aún no está completo"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<byte[]> contractPreview(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        byte[] pdf = onboardingService.generateContractPreview(id, token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("contrato-%d.pdf".formatted(id)).build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

    @PostMapping("/{id}/step3/contract")
    @Operation(summary = "Paso 3: firmar el contrato",
            description = "Genera y guarda el PDF como SIGNED_CONTRACT y transiciona a "
                    + "CONTRACT_SIGNED (current_step=4). Requiere estado DOCUMENTS_UPLOADED.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrato firmado"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite la operación"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<ContractSignResponse>> signContract(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        OnboardingService.ContractSignResult result = onboardingService.signContract(id, token);

        ContractSignResponse payload = new ContractSignResponse(
                result.onboarding().getId(),
                result.onboarding().getStatus(),
                result.onboarding().getCurrentStep(),
                result.documentUrl());
        return ResponseEntity.ok(ApiResponseBackend.ok("Contrato firmado", payload));
    }

    @PostMapping("/{id}/step4/payment")
    @Operation(summary = "Paso 4: configurar método de pago",
            description = "Guarda el método de pago (JSONB) y transiciona a "
                    + "PAYMENT_CONFIGURED. Requiere estado CONTRACT_SIGNED.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Método de pago guardado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite la operación"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<OnboardingStatusResponse>> configurePayment(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody Step4PaymentRequest request) {

        String token = extractBearer(authHeader);
        Onboarding updated = onboardingService.configurePayment(id, token, request);
        return ResponseEntity.ok(ApiResponseBackend.ok("Método de pago configurado",
                toStatusResponse(updated)));
    }

    @PostMapping(value = "/{id}/step5/selfie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Paso 5: subir selfie y finalizar",
            description = "Sube la selfie (tipo SELFIE) a Supabase Storage y transiciona a "
                    + "PENDING_VERIFICATION. Requiere estado PAYMENT_CONFIGURED.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Selfie subida"),
            @ApiResponse(responseCode = "400", description = "Archivo inválido"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite la operación"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<OnboardingStatusResponse>> submitSelfie(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam("file") MultipartFile file) {

        String token = extractBearer(authHeader);
        Onboarding updated = onboardingService.submitSelfie(id, token, file);
        return ResponseEntity.ok(ApiResponseBackend.ok("Selfie subida",
                toStatusResponse(updated)));
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Estado y datos del contratista",
            description = "Devuelve el estado actual del onboarding, el paso vigente y los datos "
                    + "personales ya guardados del contratista (incluido el email pre-cargado de la "
                    + "invitación). El frontend del Paso 1 usa este endpoint para mostrar el email "
                    + "como read-only y rellenar los campos al volver a entrar.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado del onboarding"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<OnboardingStatusResponse>> getStatus(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        Onboarding onboarding = onboardingService.getStatus(id, token);
        return ResponseEntity.ok(ApiResponseBackend.ok("Estado del onboarding",
                toStatusResponse(onboarding)));
    }

    @GetMapping("/{id}/documents")
    @Operation(summary = "Listar documentos del onboarding",
            description = "Devuelve los documentos subidos en el Paso 2 (o generados como SIGNED_CONTRACT/SELFIE). "
                    + "Acepta el query param 'type' opcional para filtrar por DocumentType.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de documentos"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<List<DocumentResponse>>> listDocuments(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "type", required = false) DocumentType type) {

        String token = extractBearer(authHeader);
        List<DocumentResponse> payload = onboardingService.listDocuments(id, token, type).stream()
                .map(this::toDocumentResponse)
                .toList();
        return ResponseEntity.ok(ApiResponseBackend.ok("Documentos del onboarding", payload));
    }

    @GetMapping("/{id}/documents/{documentId}")
    @Operation(summary = "Obtener un documento del onboarding",
            description = "Devuelve la metadata (incluyendo file_url pública del bucket) de un documento "
                    + "específico. El frontend puede usar el file_url para previsualizar el archivo.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<DocumentResponse>> getDocument(
            @PathVariable Long id,
            @PathVariable Long documentId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        Document doc = onboardingService.getDocument(id, documentId, token);
        return ResponseEntity.ok(ApiResponseBackend.ok("Documento encontrado",
                toDocumentResponse(doc)));
    }

    @DeleteMapping("/{id}/documents/{documentId}")
    @Operation(summary = "Borrar un documento del onboarding",
            description = "Elimina el archivo del bucket Supabase y la fila en BD. "
                    + "Permitido en cualquier estado pre-verificación (IN_PROGRESS, DOCUMENTS_UPLOADED, "
                    + "CONTRACT_SIGNED, PAYMENT_CONFIGURED, CORRECTION_REQUIRED). Para reemplazar un "
                    + "documento, hacer DELETE seguido de POST /documents.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documento borrado"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Estado actual no permite borrar"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<Void>> deleteDocument(
            @PathVariable Long id,
            @PathVariable Long documentId,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        onboardingService.deleteDocument(id, documentId, token);
        return ResponseEntity.ok(ApiResponseBackend.ok("Documento borrado", null));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "HU-09: comentarios de corrección",
            description = "Devuelve las entradas de event_history con "
                    + "event=CORRECTION_REQUESTED y sus observaciones, ordenadas por fecha.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de comentarios de corrección"),
            @ApiResponse(responseCode = "401", description = "Token de invitación inválido"),
            @ApiResponse(responseCode = "410", description = "Token de invitación expirado")
    })
    public ResponseEntity<ApiResponseBackend<List<CorrectionCommentResponse>>> comments(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String token = extractBearer(authHeader);
        List<CorrectionCommentResponse> payload = onboardingService.getCorrectionComments(id, token);
        return ResponseEntity.ok(ApiResponseBackend.ok("Comentarios de corrección", payload));
    }

    private OnboardingStatusResponse toStatusResponse(Onboarding o) {
        var c = o.getContractor();
        return new OnboardingStatusResponse(
                o.getId(),
                o.getStatus(),
                o.getCurrentStep(),
                c.getFirstName(),
                c.getLastName(),
                c.getPreferredName(),
                c.getBirthDate(),
                c.getCountryIso(),
                c.getIdDocumentNumber(),
                c.getTaxRegime(),
                c.getPhone(),
                c.getEmail(),
                o.getUpdatedAt());
    }

    private DocumentResponse toDocumentResponse(Document d) {
        return new DocumentResponse(
                d.getId(),
                d.getDocType(),
                d.getFileUrl(),
                d.getStatus(),
                d.getUploadedAt());
    }

    private String extractBearer(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidTokenException("Encabezado Authorization debe ser 'Bearer <token>'");
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
