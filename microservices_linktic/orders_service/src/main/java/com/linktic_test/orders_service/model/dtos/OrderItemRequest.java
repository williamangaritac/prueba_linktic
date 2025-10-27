package com.linktic_test.orders_service.model.dtos;

import lombok.*;

/**
 * DTO para solicitudes de items de orden
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    private Long id;
    private String sku;
    private Double price;
    private Long quantity;
}
