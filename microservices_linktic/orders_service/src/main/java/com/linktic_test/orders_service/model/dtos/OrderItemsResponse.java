package com.linktic_test.orders_service.model.dtos;

/**
 * DTO para respuestas de items de orden
 */
public record OrderItemsResponse(Long id, String sku, Double price, Long quantity) {
}
