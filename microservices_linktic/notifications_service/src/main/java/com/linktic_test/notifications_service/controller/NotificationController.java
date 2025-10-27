package com.linktic_test.notifications_service.controller;

import com.linktic_test.notifications_service.model.dto.NotificationDTO;
import com.linktic_test.notifications_service.model.entities.NotificationStatus;
import com.linktic_test.notifications_service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar notificaciones
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "API para gestionar notificaciones por email")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Obtiene todas las notificaciones
     */
    @GetMapping
    @Operation(summary = "Obtener todas las notificaciones", description = "Retorna una lista de todas las notificaciones enviadas")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        log.debug("GET /notifications - Fetching all notifications");
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones por número de orden
     */
    @GetMapping("/order/{orderNumber}")
    @Operation(summary = "Obtener notificaciones por número de orden", description = "Retorna las notificaciones asociadas a un número de orden específico")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByOrderNumber(
            @Parameter(description = "Número de orden", example = "ORD-2024-001")
            @PathVariable String orderNumber) {
        log.debug("GET /notifications/order/{} - Fetching notifications by order number", orderNumber);
        List<NotificationDTO> notifications = notificationService.getNotificationsByOrderNumber(orderNumber);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Obtiene notificaciones por estado
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener notificaciones por estado", description = "Retorna las notificaciones filtradas por estado (PENDING, SENT, FAILED)")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByStatus(
            @Parameter(description = "Estado de la notificación", example = "SENT")
            @PathVariable NotificationStatus status) {
        log.debug("GET /notifications/status/{} - Fetching notifications by status", status);
        List<NotificationDTO> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que el servicio de notificaciones esté funcionando")
    public ResponseEntity<String> healthCheck() {
        log.debug("GET /notifications/health - Health check");
        return ResponseEntity.ok("Notifications Service is running");
    }
}

