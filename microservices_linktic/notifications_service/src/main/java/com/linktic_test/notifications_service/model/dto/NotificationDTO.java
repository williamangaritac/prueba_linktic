package com.linktic_test.notifications_service.model.dto;

import com.linktic_test.notifications_service.model.entities.NotificationStatus;
import com.linktic_test.notifications_service.model.entities.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de notificaciones
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    
    private Long id;
    private String orderNumber;
    private Long orderId;
    private String recipientEmail;
    private String subject;
    private String message;
    private NotificationStatus status;
    private NotificationType type;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private String errorMessage;
}

