package com.linktic_test.notifications_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * DTO para recibir eventos de órdenes desde Kafka
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEventDTO {
    
    @JsonProperty("orderId")
    private Long orderId;
    
    @JsonProperty("orderNumber")
    private String orderNumber;
    
    @JsonProperty("items")
    private List<OrderItemDTO> items;
    
    @JsonProperty("totalAmount")
    private Double totalAmount;
    
    @JsonProperty("eventType")
    private String eventType; // "ORDER_CREATED", "ORDER_UPDATED", etc.
}

