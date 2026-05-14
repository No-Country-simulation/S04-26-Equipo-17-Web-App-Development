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

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
        String safeName = originalName.replaceAll("[^A-Za-z0-9._-]", "_");
        String key = "%d/%s/%s-%s".formatted(
                onboarding.getId(),
                type.name(),
                UUID.randomUUID(),
                safeName);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el archivo " + originalName, e);
        }

        String fileUrl = storageClient.upload(bytes, key, contentType);
        log.info("Documento subido: onboarding={} type={} key={}", onboarding.getId(), type, key);

        Document doc = Document.builder()
                .onboarding(onboarding)
                .docType(type)
                .fileUrl(fileUrl)
                .status(OnboardingStatus.IN_PROGRESS)
                .build();
        return documentRepository.save(doc);
    }

    public List<Document> findByOnboarding(Long onboardingId) {
        return documentRepository.findByOnboardingId(onboardingId);
    }
}
