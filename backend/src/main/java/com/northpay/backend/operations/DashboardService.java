package com.northpay.backend.operations;

import com.northpay.backend.onboarding.Onboarding;
import com.northpay.backend.onboarding.OnboardingRepository;
import com.northpay.backend.common.enums.OnboardingStatus;
import com.northpay.backend.operations.dto.DashboardItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OnboardingRepository onboardingRepository;

    @Transactional(readOnly = true)
    public List<DashboardItem> getDashboard(String statusFilter,
                                            Integer stepFilter,
                                            String countryFilter) {
        List<Onboarding> onboardings;

        if (statusFilter != null && !statusFilter.isBlank()) {
            OnboardingStatus status = OnboardingStatus.valueOf(statusFilter.toUpperCase());
            onboardings = onboardingRepository.findByStatus(status);
        } else {
            onboardings = onboardingRepository.findAll();
        }

        return onboardings.stream()
                .filter(o -> stepFilter == null || o.getCurrentStep().equals(stepFilter))
                .filter(o -> countryFilter == null
                        || o.getContractor().getCountryIso() != null
                        && countryFilter.equalsIgnoreCase(o.getContractor().getCountryIso()))
                .map(this::toDashboardItem)
                .sorted(Comparator.comparing(DashboardItem::updatedAt))
                .toList();
    }

    private DashboardItem toDashboardItem(Onboarding o) {
        long hoursSinceUpdate = ChronoUnit.HOURS.between(o.getUpdatedAt(), LocalDateTime.now());
        boolean slaBreach = hoursSinceUpdate > 48
                && o.getStatus() != OnboardingStatus.INVITED
                && o.getStatus() != OnboardingStatus.REJECTED
                && o.getStatus() != OnboardingStatus.ACTIVATED;

        return new DashboardItem(
                o.getId(),
                o.getContractor().getFullName(),
                o.getContractor().getEmail(),
                o.getContractor().getCountryIso(),
                o.getCurrentStep(),
                o.getStatus().name(),
                o.getUpdatedAt(),
                hoursSinceUpdate,
                slaBreach
        );
    }
}