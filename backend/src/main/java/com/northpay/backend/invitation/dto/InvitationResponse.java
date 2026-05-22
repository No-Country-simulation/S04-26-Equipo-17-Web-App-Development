package com.northpay.backend.invitation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InvitationResponse (
        Long onboardingId,
        String token,
        LocalDateTime expiresAt,
        String invitationLink,
        String monthlyFee,
        String contractDuration,
        String currency,
        String company,
        String timeRemaining
){
}
