package com.northpay.backend.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final PusherService pusherService;

    /**
     * Notificación genérica (mantenemos el método actual para casos simples).
     */
    @Transactional
    public void notify(String userEmail, String message) {
        Notification notification = Notification.builder()
                .userEmail(userEmail)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        emailService.sendNotificationEmail(userEmail, "Notificación NorthPay", message);
        // Envío genérico a Pusher (sin estructura de evento)
        pusherService.notifyOperators(new PusherEvent(
                "notification", null, null, null, null, null, message, null));
    }

    /**
     * Notificación con evento estructurado para el dashboard.
     * @param event     PusherEvent ya construido (status-changed, correction-requested, etc.)
     * @param userEmail email del contratista (o del operador si aplica)
     */
    @Transactional
    public void notifyEvent(PusherEvent event, String userEmail) {
        Notification notification = Notification.builder()
                .userEmail(userEmail)
                .message(event.message())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        emailService.sendNotificationEmail(userEmail, "NorthPay: " + event.type(), event.message());

        pusherService.notifyOperators(event);
    }
}