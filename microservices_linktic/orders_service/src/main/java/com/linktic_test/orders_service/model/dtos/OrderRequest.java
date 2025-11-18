package com.linktic_test.orders_service.model.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "La lista de items no puede ser nula")
    @NotEmpty(message = "La lista de items no puede estar vacía")
    @Valid
    private List<OrderItemRequest> orderItems;
}
