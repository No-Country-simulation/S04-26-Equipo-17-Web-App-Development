package com.northpay.backend.document;

import com.northpay.backend.common.enums.DocumentType;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.onboarding.Onboarding;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "onboarding_id", nullable = false)
    private Onboarding onboarding;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "doc_type", nullable = false, columnDefinition = "document_type")
    private DocumentType docType;

    @Column(name = "file_url", nullable = false, columnDefinition = "text")
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "onboarding_status")
    private OnboardingStatus status = OnboardingStatus.IN_PROGRESS;

    @Column(name = "operator_notes", columnDefinition = "text")
    private String operatorNotes;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
}
