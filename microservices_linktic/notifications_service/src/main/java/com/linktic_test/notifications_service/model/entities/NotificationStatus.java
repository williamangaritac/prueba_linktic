package com.linktic_test.notifications_service.model.entities;

/**
 * Estados posibles de una notificación
 */
public enum NotificationStatus {
    PENDING,    // Pendiente de envío
    SENT,       // Enviada exitosamente
    FAILED      // Falló el envío
}

