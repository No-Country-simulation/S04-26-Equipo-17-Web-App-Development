package com.northpay.backend.invitation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record InvitationResponse (
        Long onboardingId,
        String token,
        LocalDateTime expiresAt,
        String invitationLink
){
}
