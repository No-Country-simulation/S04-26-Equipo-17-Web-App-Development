package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.invitation.Contractor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "onboardings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Onboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contractor_id", nullable = false)
    private Contractor contractor;

    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 1;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "onboarding_status")
    private OnboardingStatus status = OnboardingStatus.INVITED;

    @Column(name = "assigned_operator_id")
    private Long assignedOperatorId;

    @Column(name = "invitation_token", unique = true, length = 128)
    private String invitationToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
