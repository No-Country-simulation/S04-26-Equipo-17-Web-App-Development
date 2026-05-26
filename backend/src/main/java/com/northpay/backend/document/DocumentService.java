package com.northpay.backend.document;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.onboarding.Onboarding;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final StorageClient storageClient;
    private final DocumentRepository documentRepository;

    @Transactional
    public Document store(Onboarding onboarding, DocumentType type, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo vacío para tipo " + type);
        }

        String key = buildKey(onboarding, type, file.getOriginalFilename());
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer el archivo " + file.getOriginalFilename(), e);
        }

        String fileUrl = storageClient.upload(bytes, key, contentType);
        log.info("Documento subido: onboarding={} type={} key={}", onboarding.getId(), type, key);

        Document doc = Document.builder()
                .onboarding(onboarding)
                .docType(type)
                .fileUrl(fileUrl)
                .storageKey(key)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        return documentRepository.save(doc);
    }

    /**
     * Almacena un documento generado por el backend (p. ej. el PDF del contrato),
     * sin pasar por un MultipartFile.
     */
    @Transactional
    public Document storeGenerated(Onboarding onboarding,
                                   DocumentType type,
                                   byte[] bytes,
                                   String filename,
                                   String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Contenido vacío para tipo " + type);
        }
        String key = buildKey(onboarding, type, filename);
        String resolvedContentType = contentType != null ? contentType : "application/octet-stream";

        String fileUrl = storageClient.upload(bytes, key, resolvedContentType);
        log.info("Documento generado subido: onboarding={} type={} key={}",
                onboarding.getId(), type, key);

        Document doc = Document.builder()
                .onboarding(onboarding)
                .docType(type)
                .fileUrl(fileUrl)
                .storageKey(key)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        return documentRepository.save(doc);
    }

    /**
     * Como {@link #storeGenerated}, pero si ya existe un documento de ese tipo
     * para el onboarding, reemplaza su URL en lugar de insertar otra fila.
     * Borra el archivo previo del bucket para no dejar huérfanos.
     */
    @Transactional
    public Document storeOrReplaceGenerated(Onboarding onboarding,
                                            DocumentType type,
                                            byte[] bytes,
                                            String filename,
                                            String contentType) {
        return documentRepository.findByOnboardingIdAndDocType(onboarding.getId(), type)
                .map(existing -> {
                    String newKey = buildKey(onboarding, type, filename);
                    String url = storageClient.upload(bytes, newKey,
                            contentType != null ? contentType : "application/octet-stream");

                    String previousKey = existing.getStorageKey();
                    if (previousKey != null && !previousKey.isBlank() && !previousKey.equals(newKey)) {
                        try {
                            storageClient.delete(previousKey);
                        } catch (RuntimeException ex) {
                            log.warn("No se pudo borrar la versión previa del bucket: onboarding={} type={} key={}",
                                    onboarding.getId(), type, previousKey, ex);
                        }
                    }

                    existing.setFileUrl(url);
                    existing.setStorageKey(newKey);
                    log.info("Documento {} reemplazado: onboarding={} type={}",
                            existing.getId(), onboarding.getId(), type);
                    return documentRepository.save(existing);
                })
                .orElseGet(() -> storeGenerated(onboarding, type, bytes, filename, contentType));
    }

    /**
     * Borra el documento del bucket y de la BD. Si la fila no tiene storage_key
     * (migrada desde antes de V5), solo borra la fila.
     */
    @Transactional
    public void delete(Document doc) {
        String key = doc.getStorageKey();
        if (key != null && !key.isBlank()) {
            try {
                storageClient.delete(key);
            } catch (RuntimeException ex) {
                log.warn("No se pudo borrar el objeto del bucket (continuamos con la fila DB): documentId={} key={}",
                        doc.getId(), key, ex);
            }
        } else {
            log.warn("Documento {} sin storage_key; se borra solo la fila DB", doc.getId());
        }
        documentRepository.delete(doc);
    }

    private String buildKey(Onboarding onboarding, DocumentType type, String filename) {
        String safeName = (filename == null || filename.isBlank() ? "file" : filename)
                .replaceAll("[^A-Za-z0-9._-]", "_");
        return "%d/%s/%s-%s".formatted(
                onboarding.getId(),
                type.name(),
                UUID.randomUUID(),
                safeName);
    }

    public List<Document> findByOnboarding(Long onboardingId) {
        return documentRepository.findByOnboardingId(onboardingId);
    }

    public java.util.Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    public java.util.Optional<Document> findByOnboardingAndType(Long onboardingId, DocumentType type) {
        return documentRepository.findByOnboardingIdAndDocType(onboardingId, type);
    }
}
