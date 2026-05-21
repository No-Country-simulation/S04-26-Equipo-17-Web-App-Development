package com.northpay.backend.onboarding;

import com.northpay.backend.common.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

    @Query("SELECT eh FROM EventHistory eh WHERE eh.onboarding.id = :onboardingId ORDER BY eh.createdAt DESC")
    List<EventHistory> findLatest(Long onboardingId);

    @Query("SELECT eh FROM EventHistory eh " +
           "WHERE eh.onboarding.id = :onboardingId AND eh.event = :event " +
           "ORDER BY eh.createdAt ASC")
    List<EventHistory> findByOnboardingIdAndEventOrderByCreatedAtAsc(
            Long onboardingId, EventType event);
}
