package com.northpay.backend.document;

import com.northpay.backend.common.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOnboardingId(Long onboardingId);

    Optional<Document> findByOnboardingIdAndDocType(Long onboardingId, DocumentType docType);
}
