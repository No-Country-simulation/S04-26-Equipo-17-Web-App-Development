package com.northpay.backend.notification;

import com.northpay.backend.common.config.BrevoConfig;
import com.northpay.backend.common.config.FrontendConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final BrevoConfig config;
    private final RestClient restClient;
    private final EmailTemplateService templateService;
    private final FrontendConfig frontendConfig;

    public EmailService(BrevoConfig config, EmailTemplateService templateService, FrontendConfig frontendConfig) {
        this.config = config;
        this.templateService = templateService;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", config.apiKey())
                .build();
        this.frontendConfig = frontendConfig;
    }

    public void sendInvitationEmail(String toEmail, String token) {
        String link = frontendConfig.url() + frontendConfig.onboardingPath() + "?token=" + token;
        String htmlContent = templateService.buildInvitationHtml(link);
        String plainText = templateService.buildPlainTextFallback(link);
        sendEmail(toEmail, "Invitación a NorthPay", htmlContent, plainText);
    }

    public void sendNotificationEmail(String toEmail, String subject, String message) {
        String htmlContent = templateService.buildNotificationHtml(message);
        String plainText = message;
        sendEmail(toEmail, subject, htmlContent, plainText);
    }

    private void sendEmail(String toEmail, String subject, String htmlContent, String plainTextContent) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", config.fromName(), "email", config.fromEmail()),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "htmlContent", htmlContent,
                "textContent", plainTextContent   // Brevo admite ambas
        );

        restClient.post()
                .uri("/smtp/email")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}