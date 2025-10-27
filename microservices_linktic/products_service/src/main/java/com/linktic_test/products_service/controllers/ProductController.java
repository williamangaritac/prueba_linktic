package com.linktic_test.products_service.controllers;

import com.linktic_test.products_service.model.dtos.ProductRequest;
import com.linktic_test.products_service.model.dtos.ProductResponse;
import com.linktic_test.products_service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product operations
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "API for managing products")
public class ProductController {

    private final ProductService productService;

    /**
     * Health check endpoint
     */
    @GetMapping("/status")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Products Service is running!");
    }

    @Operation(summary = "Create a new product", description = "Creates a new product in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Product with SKU already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to create product with SKU: {}", productRequest.getSku());
        
        try {
            ProductResponse response = productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Error creating product: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get product by ID", description = "Retrieves a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        log.info("Received request to get product with ID: {}", id);

        try {
            ProductResponse response = productService.getProductById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting product: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get product by SKU", description = "Retrieves a product by its SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(
            @Parameter(description = "Product SKU", required = true)
            @PathVariable String sku) {
        log.info("Received request to get product with SKU: {}", sku);

        try {
            ProductResponse response = productService.getProductBySku(sku);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting product: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Update product by ID", description = "Updates an existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Product with SKU already exists",
                    content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest productRequest) {
        log.info("Received request to update product with ID: {}", id);
        
        try {
            ProductResponse response = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating product: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Delete product by ID", description = "Deletes a product from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        log.info("Received request to delete product with ID: {}", id);
        
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error deleting product: {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "Get all products with pagination", description = "Retrieves all products with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        log.info("Received request to get all products with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ProductResponse> response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get active products with pagination", description = "Retrieves only active products with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active products retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/active")
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(
            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        log.info("Received request to get active products with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductResponse> response = productService.getActiveProducts(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all products without pagination", description = "Retrieves all products in the system without pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All products retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductResponse>> getAllProductsWithoutPagination() {
        log.info("Received request to get all products without pagination");

        List<ProductResponse> response = productService.getAllProductsWithoutPagination();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all active products without pagination", description = "Retrieves all active products in the system without pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All active products retrieved successfully",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/active/all")
    public ResponseEntity<List<ProductResponse>> getAllActiveProductsWithoutPagination() {
        log.info("Received request to get all active products without pagination");

        List<ProductResponse> response = productService.getAllActiveProductsWithoutPagination();
        return ResponseEntity.ok(response);
    }
}
