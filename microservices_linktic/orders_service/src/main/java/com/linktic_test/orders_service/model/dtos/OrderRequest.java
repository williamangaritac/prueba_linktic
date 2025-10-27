package com.linktic_test.orders_service.model.dtos;

import lombok.*;

import java.util.List;

/**
 * DTO para solicitudes de creación de órdenes
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private String customerEmail;
    private List<OrderItemRequest> orderItems;
}
