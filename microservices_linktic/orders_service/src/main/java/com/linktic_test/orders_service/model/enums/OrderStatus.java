package com.linktic_test.orders_service.model.enums;

/**
 * Enum que representa los diferentes estados de una orden
 */
public enum OrderStatus {
    PLACED,     // Orden colocada/creada
    CANCELLED,  // Orden cancelada
    SHIPPED,    // Orden enviada
    DELIVERED   // Orden entregada
}
