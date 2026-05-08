package com.northpay.backend.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public void notify(String userEmail, String message) {
        Notification notification = Notification.builder()
                .userEmail(userEmail)
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        emailService.sendNotificationEmail(userEmail, "Notificación NorthPay", message);
    }
}