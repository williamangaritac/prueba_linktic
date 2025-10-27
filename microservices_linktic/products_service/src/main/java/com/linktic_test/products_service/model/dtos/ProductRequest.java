package com.linktic_test.products_service.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Product creation and update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Product request data transfer object")
public class ProductRequest {

    @Schema(description = "Product SKU", example = "SKU001", required = true)
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    @Schema(description = "Product name", example = "Laptop Dell Inspiron", required = true)
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Schema(description = "Product description", example = "High-performance laptop for professional use")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Schema(description = "Product price", example = "1299.99", required = true)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must have at most 8 integer digits and 2 decimal places")
    private BigDecimal price;

    @Schema(description = "Product status", example = "true", required = true)
    @NotNull(message = "Status is required")
    private Boolean status;
}
