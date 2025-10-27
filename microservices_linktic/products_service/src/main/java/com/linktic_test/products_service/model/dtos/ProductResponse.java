package com.linktic_test.products_service.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Product response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product response data transfer object")
public class ProductResponse {

    @Schema(description = "Product ID", example = "1")
    private Long id;

    @Schema(description = "Product SKU", example = "SKU001")
    private String sku;

    @Schema(description = "Product name", example = "Laptop Dell Inspiron")
    private String name;

    @Schema(description = "Product description", example = "High-performance laptop for professional use")
    private String description;

    @Schema(description = "Product price", example = "1299.99")
    private BigDecimal price;

    @Schema(description = "Product status", example = "true")
    private Boolean status;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
