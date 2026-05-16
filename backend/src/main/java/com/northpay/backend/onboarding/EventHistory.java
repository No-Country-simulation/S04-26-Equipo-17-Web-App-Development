package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.EventType;
import com.northpay.backend.common.enums.OnboardingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Entidad de SOLO LECTURA sobre event_history. Rol C escribe los eventos
 * (p. ej. CORRECTION_REQUESTED desde request-correction); Rol B la lee
 * para HU-09 (GET /api/onboarding/{id}/comments).
 */
@Entity
@Table(name = "event_history")
@Getter
@NoArgsConstructor
public class EventHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "onboarding_id", nullable = false)
    private Long onboardingId;

    @Column(name = "operator_id")
    private Long operatorId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "event_type")
    private EventType event;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "previous_status", columnDefinition = "onboarding_status")
    private OnboardingStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "new_status", columnDefinition = "onboarding_status")
    private OnboardingStatus newStatus;

    @Column(columnDefinition = "text")
    private String observations;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
