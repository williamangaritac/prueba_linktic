package com.linktic_test.orders_service.model.dtos;

import java.util.List;

/**
 * DTO para respuestas de órdenes
 */
public record OrderResponse(Long id, String orderNumber, List<OrderItemsResponse> orderItems) {
}
