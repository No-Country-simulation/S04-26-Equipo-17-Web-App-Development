package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.OnboardingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    Optional<Onboarding> findByInvitationToken(String invitationToken);

    Optional<Onboarding> findByContractorId(Long contractorId);

    List<Onboarding> findByStatus(OnboardingStatus status);
}
