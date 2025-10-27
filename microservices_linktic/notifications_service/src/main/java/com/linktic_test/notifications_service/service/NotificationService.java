package com.linktic_test.notifications_service.service;

import com.linktic_test.notifications_service.model.dto.NotificationDTO;
import com.linktic_test.notifications_service.model.dto.OrderEventDTO;
import com.linktic_test.notifications_service.model.dto.OrderItemDTO;
import com.linktic_test.notifications_service.model.entities.Notification;
import com.linktic_test.notifications_service.model.entities.NotificationStatus;
import com.linktic_test.notifications_service.model.entities.NotificationType;
import com.linktic_test.notifications_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar notificaciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Value("${app.email.to}")
    private String defaultRecipientEmail;

    /**
     * Procesa un evento de orden y envía notificación
     */
    @Transactional
    public void processOrderEvent(OrderEventDTO orderEvent) {
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║          NUEVA ORDEN RECIBIDA - PROCESANDO NOTIFICACIÓN        ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("📦 Número de Orden: {}", orderEvent.getOrderNumber());
        log.info("🆔 ID de Orden: {}", orderEvent.getOrderId());
        log.info("💰 Monto Total: ${}", orderEvent.getTotalAmount());
        log.info("📋 Cantidad de Items: {}", orderEvent.getItems() != null ? orderEvent.getItems().size() : 0);

        try {
            // Crear registro de notificación
            Notification notification = createNotification(orderEvent);

            // Guardar notificación como PENDING
            notification = notificationRepository.save(notification);
            log.info("✅ Notificación guardada en base de datos con estado PENDING");

            // Construir detalles de la orden
            String orderDetails = buildOrderDetails(orderEvent);

            log.info("📧 Preparando envío de email a: {}", defaultRecipientEmail);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            log.info("DETALLES DE LA ORDEN:");
            log.info("{}", orderDetails);
            log.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // Enviar email
            try {
                emailService.sendOrderConfirmationEmail(
                    defaultRecipientEmail,
                    orderEvent.getOrderNumber(),
                    orderDetails
                );

                // Actualizar estado a SENT
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                log.info("✅ Email enviado exitosamente a: {}", defaultRecipientEmail);
                log.info("✅ Notificación procesada correctamente para orden: {}", orderEvent.getOrderNumber());

            } catch (Exception e) {
                // Actualizar estado a FAILED
                notification.setStatus(NotificationStatus.FAILED);
                notification.setErrorMessage(e.getMessage());
                log.error("❌ Error al enviar email para orden: {}", orderEvent.getOrderNumber(), e);
            }

            notificationRepository.save(notification);
            log.info("════════════════════════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ Error crítico procesando evento de orden: {}", orderEvent.getOrderNumber(), e);
            throw new RuntimeException("Failed to process order event", e);
        }
    }

    /**
     * Crea una entidad Notification desde un OrderEventDTO
     */
    private Notification createNotification(OrderEventDTO orderEvent) {
        NotificationType type = determineNotificationType(orderEvent.getEventType());
        
        return Notification.builder()
                .orderNumber(orderEvent.getOrderNumber())
                .orderId(orderEvent.getOrderId())
                .recipientEmail(defaultRecipientEmail)
                .subject("Confirmación de Orden - " + orderEvent.getOrderNumber())
                .message(buildOrderDetails(orderEvent))
                .status(NotificationStatus.PENDING)
                .type(type)
                .build();
    }

    /**
     * Determina el tipo de notificación basado en el tipo de evento
     */
    private NotificationType determineNotificationType(String eventType) {
        if (eventType == null) {
            return NotificationType.ORDER_CREATED;
        }
        
        return switch (eventType.toUpperCase()) {
            case "ORDER_CREATED" -> NotificationType.ORDER_CREATED;
            case "ORDER_UPDATED" -> NotificationType.ORDER_UPDATED;
            case "ORDER_CANCELLED" -> NotificationType.ORDER_CANCELLED;
            default -> NotificationType.GENERAL;
        };
    }

    /**
     * Construye los detalles de la orden para el email
     */
    private String buildOrderDetails(OrderEventDTO orderEvent) {
        StringBuilder details = new StringBuilder();
        
        if (orderEvent.getItems() != null && !orderEvent.getItems().isEmpty()) {
            details.append("Productos:\n");
            for (OrderItemDTO item : orderEvent.getItems()) {
                details.append(String.format("  - SKU: %s\n", item.getSku()));
                if (item.getProductName() != null) {
                    details.append(String.format("    Producto: %s\n", item.getProductName()));
                }
                details.append(String.format("    Cantidad: %d\n", item.getQuantity()));
                details.append(String.format("    Precio: $%.2f\n", item.getPrice()));
                details.append(String.format("    Subtotal: $%.2f\n\n", item.getPrice() * item.getQuantity()));
            }
        }
        
        if (orderEvent.getTotalAmount() != null) {
            details.append(String.format("Total: $%.2f\n", orderEvent.getTotalAmount()));
        }
        
        return details.toString();
    }

    /**
     * Obtiene todas las notificaciones
     */
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones por número de orden
     */
    public List<NotificationDTO> getNotificationsByOrderNumber(String orderNumber) {
        return notificationRepository.findByOrderNumber(orderNumber).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene notificaciones por estado
     */
    public List<NotificationDTO> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Notification a DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .orderNumber(notification.getOrderNumber())
                .orderId(notification.getOrderId())
                .recipientEmail(notification.getRecipientEmail())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .type(notification.getType())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .errorMessage(notification.getErrorMessage())
                .build();
    }
}

