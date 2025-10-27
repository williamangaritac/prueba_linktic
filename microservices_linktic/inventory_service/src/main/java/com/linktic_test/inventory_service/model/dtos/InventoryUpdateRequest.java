package com.linktic_test.inventory_service.model.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO for inventory update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdateRequest {

    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Long quantity;
}
