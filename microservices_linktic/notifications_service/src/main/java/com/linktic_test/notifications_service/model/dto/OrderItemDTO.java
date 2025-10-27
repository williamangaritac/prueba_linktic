package com.linktic_test.notifications_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para items de una orden
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    
    @JsonProperty("sku")
    private String sku;
    
    @JsonProperty("price")
    private Double price;
    
    @JsonProperty("quantity")
    private Long quantity;
    
    @JsonProperty("productName")
    private String productName;
}

