package com.linktic_test.orders_service.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.linktic_test.orders_service.model.enums.OrderStatus;

import java.util.List;

/**
 * Record que representa un evento de orden para notificaciones
 */
public record OrderEvent(
        @JsonProperty("orderId")
        Long orderId,

        @JsonProperty("orderNumber")
        String orderNumber,

        @JsonProperty("items")
        List<OrderEventItem> items,

        @JsonProperty("totalAmount")
        Double totalAmount,

        @JsonProperty("eventType")
        String eventType,

        @JsonProperty("orderStatus")
        OrderStatus orderStatus
) {
    /**
     * Record que representa un item de la orden en el evento
     */
    public record OrderEventItem(
            @JsonProperty("sku")
            String sku,

            @JsonProperty("productName")
            String productName,

            @JsonProperty("quantity")
            Integer quantity,

            @JsonProperty("price")
            Double price
    ) {
    }
}
