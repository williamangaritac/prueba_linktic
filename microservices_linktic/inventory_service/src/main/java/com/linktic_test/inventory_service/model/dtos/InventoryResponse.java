package com.linktic_test.inventory_service.model.dtos;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for inventory responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {

    private Long id;
    private String sku;
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
