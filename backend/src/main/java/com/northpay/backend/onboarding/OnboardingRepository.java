package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    Optional<Onboarding> findByInvitationToken(String invitationToken);

    Optional<Onboarding> findByContractorId(Long contractorId);

    List<Onboarding> findByStatus(OnboardingStatus status);

    boolean existsByContractorIdAndStatusNot(Long contractorId, OnboardingStatus status);

    Optional<Onboarding> findByContractorIdAndStatusNot(Long contractorId, OnboardingStatus status);

    @Query("""
    SELECT o FROM Onboarding o
    WHERE o.updatedAt < :cutoff
      AND o.status NOT IN ('REJECTED', 'ACTIVATED')
      AND (o.status <> 'INVITED' OR o.tokenExpiresAt > CURRENT_TIMESTAMP)
    """)
    List<Onboarding> findStaleOnboardings(LocalDateTime cutoff);
}
