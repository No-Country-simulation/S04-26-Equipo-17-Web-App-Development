package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.EventType;
import com.northpay.backend.common.enums.OnboardingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "onboarding_id", nullable = false)
    private Onboarding onboarding;

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

    @Column(columnDefinition = "TEXT")
    private String observations;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}