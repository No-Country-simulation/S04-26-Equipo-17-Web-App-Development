package com.northpay.backend.notification;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;

@Service
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    public EmailTemplateService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildInvitationHtml(String link) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process("email/invitation", context);
    }

    public String buildNotificationHtml(String message) {
        Context context = new Context();
        context.setVariable("message", message);
        return templateEngine.process("email/notification", context);
    }

    public String buildPlainTextFallback(String link) {
        return "Bienvenido a NorthPay\n\n" +
                "Has sido invitado. Completa tu onboarding aquí:\n" + link +
                "\n\nEl enlace expira en 24 horas.\n© 2026 NorthPay";
    }
}