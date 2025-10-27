package com.linktic_test.inventory_service.controllers;

import com.linktic_test.inventory_service.model.dtos.BaseResponse;
import com.linktic_test.inventory_service.model.dtos.InventoryResponse;
import com.linktic_test.inventory_service.model.dtos.InventoryUpdateRequest;
import com.linktic_test.inventory_service.model.dtos.OrderItemRequest;
import com.linktic_test.inventory_service.services.InventoryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Inventory operations
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Inventory", description = "API for managing product inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Health check endpoint
     */
    @GetMapping("/status")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running!");
    }

    /**
     * Get inventory quantity by SKU
     */
    @GetMapping("/{sku}")
    @Operation(summary = "Get inventory by SKU", description = "Retrieve inventory information for a specific product SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<?> getInventoryBySku(
            @Parameter(description = "Product SKU", required = true, example = "SKU001")
            @PathVariable String sku) {
        
        log.info("GET /inventory/{} - Getting inventory for SKU", sku);
        
        try {
            InventoryResponse inventory = inventoryService.getInventoryBySku(sku);
            log.info("Successfully retrieved inventory for SKU: {}", sku);
            return ResponseEntity.ok(inventory);
            
        } catch (RuntimeException e) {
            log.error("Error getting inventory for SKU {}: {}", sku, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(new String[]{e.getMessage()}));
        } catch (Exception e) {
            log.error("Unexpected error getting inventory for SKU {}: {}", sku, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(new String[]{"An unexpected error occurred"}));
        }
    }

    /**
     * Update inventory quantity for a specific SKU
     */
    @PutMapping("/{sku}")
    @Operation(summary = "Update inventory quantity", description = "Update the quantity of a specific product in inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inventory not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<?> updateInventoryQuantity(
            @Parameter(description = "Product SKU", required = true, example = "SKU001")
            @PathVariable String sku,
            @Parameter(description = "Inventory update request", required = true)
            @Valid @RequestBody InventoryUpdateRequest request) {
        
        log.info("PUT /inventory/{} - Updating inventory quantity to {}", sku, request.getQuantity());
        
        try {
            // Ensure SKU in path matches SKU in request body
            request.setSku(sku);
            
            InventoryResponse updatedInventory = inventoryService.updateInventoryQuantity(request);
            log.info("Successfully updated inventory for SKU: {}", sku);
            return ResponseEntity.ok(updatedInventory);
            
        } catch (RuntimeException e) {
            log.error("Error updating inventory for SKU {}: {}", sku, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse(new String[]{e.getMessage()}));
        } catch (Exception e) {
            log.error("Unexpected error updating inventory for SKU {}: {}", sku, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(new String[]{"An unexpected error occurred"}));
        }
    }

    /**
     * Update inventory after purchase (process multiple items)
     */
    @PostMapping("/purchase")
    @Operation(summary = "Update inventory after purchase", description = "Update inventory quantities after a purchase transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient inventory",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<BaseResponse> updateInventoryAfterPurchase(
            @Parameter(description = "List of order items to process", required = true)
            @Valid @RequestBody List<OrderItemRequest> orderItems) {
        
        log.info("POST /inventory/purchase - Processing {} order items", orderItems.size());
        
        try {
            boolean success = inventoryService.updateInventoryAfterPurchase(orderItems);
            
            if (success) {
                log.info("Successfully processed purchase for {} items", orderItems.size());
                return ResponseEntity.ok(new BaseResponse(null));
            } else {
                log.error("Failed to process purchase");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse(new String[]{"Failed to process purchase"}));
            }
            
        } catch (RuntimeException e) {
            log.error("Error processing purchase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse(new String[]{e.getMessage()}));
        } catch (Exception e) {
            log.error("Unexpected error processing purchase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(new String[]{"An unexpected error occurred"}));
        }
    }

    /**
     * Get all inventory items
     */
    @GetMapping
    @Operation(summary = "Get all inventory", description = "Retrieve all inventory items")
    @ApiResponse(responseCode = "200", description = "Inventory list retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventoryResponse.class)))
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        log.info("GET /inventory - Getting all inventory items");
        
        try {
            List<InventoryResponse> inventories = inventoryService.getAllInventory();
            log.info("Successfully retrieved {} inventory items", inventories.size());
            return ResponseEntity.ok(inventories);
            
        } catch (Exception e) {
            log.error("Error getting all inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
