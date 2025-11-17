package com.linktic_test.notifications_service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEventDTO {

    @JsonProperty("orderId")
    private Long orderId;

    @JsonProperty("orderNumber")
    private String orderNumber;

    @JsonProperty("customerEmail")
    private String customerEmail;

    @JsonProperty("items")
    private List<OrderItemDTO> items;

    @JsonProperty("totalAmount")
    private Double totalAmount;

    @JsonProperty("eventType")
    private String eventType; // "ORDER_CREATED", "ORDER_UPDATED", etc.

    @JsonProperty("orderStatus")
    private String orderStatus; // "PLACED", "CONFIRMED", "SHIPPED", etc.
}

