package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

    List<EventHistory> findByOnboardingIdAndEventOrderByCreatedAtAsc(
            Long onboardingId, EventType event);
}
