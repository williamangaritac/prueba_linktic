package com.linktic_test.inventory_service.services;

import com.linktic_test.inventory_service.model.dtos.InventoryResponse;
import com.linktic_test.inventory_service.model.dtos.InventoryUpdateRequest;
import com.linktic_test.inventory_service.model.dtos.OrderItemRequest;
import com.linktic_test.inventory_service.model.entities.Inventory;
import com.linktic_test.inventory_service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for inventory operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Get inventory quantity by SKU
     * @param sku the product SKU
     * @return InventoryResponse with quantity information
     */
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryBySku(String sku) {
        log.debug("Getting inventory for SKU: {}", sku);
        
        Optional<Inventory> inventory = inventoryRepository.findBySku(sku);
        
        if (inventory.isEmpty()) {
            log.warn("Inventory not found for SKU: {}", sku);
            throw new RuntimeException("Inventory not found for SKU: " + sku);
        }
        
        Inventory inv = inventory.get();
        log.debug("Found inventory for SKU {}: quantity = {}", sku, inv.getQuantity());
        
        return mapToResponse(inv);
    }

    /**
     * Update inventory quantity after purchase
     * @param orderItems list of order items to process
     * @return true if all items were successfully updated
     */
    public boolean updateInventoryAfterPurchase(List<OrderItemRequest> orderItems) {
        log.info("Processing inventory update for {} items", orderItems.size());
        
        try {
            for (OrderItemRequest item : orderItems) {
                updateSingleItem(item);
            }
            
            log.info("Successfully updated inventory for all items");
            emitInventoryChangeEvent("PURCHASE_COMPLETED", orderItems);
            return true;
            
        } catch (Exception e) {
            log.error("Error updating inventory: {}", e.getMessage());
            throw new RuntimeException("Failed to update inventory: " + e.getMessage());
        }
    }

    /**
     * Update inventory quantity for a specific SKU
     * @param request the update request
     * @return updated inventory response
     */
    public InventoryResponse updateInventoryQuantity(InventoryUpdateRequest request) {
        log.debug("Updating inventory quantity for SKU: {} to {}", request.getSku(), request.getQuantity());
        
        Optional<Inventory> inventoryOpt = inventoryRepository.findBySku(request.getSku());
        
        if (inventoryOpt.isEmpty()) {
            log.warn("Inventory not found for SKU: {}", request.getSku());
            throw new RuntimeException("Inventory not found for SKU: " + request.getSku());
        }
        
        Inventory inventory = inventoryOpt.get();
        Long oldQuantity = inventory.getQuantity();
        inventory.setQuantity(request.getQuantity());
        
        Inventory savedInventory = inventoryRepository.save(inventory);
        
        log.info("Updated inventory for SKU {}: {} -> {}", 
                request.getSku(), oldQuantity, request.getQuantity());
        
        emitInventoryChangeEvent("QUANTITY_UPDATED", savedInventory);
        
        return mapToResponse(savedInventory);
    }

    /**
     * Get all inventory items
     * @return list of all inventory responses
     */
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        log.debug("Getting all inventory items");
        
        List<Inventory> inventories = inventoryRepository.findAll();
        
        return inventories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Update a single inventory item
     * @param item the order item to process
     */
    private void updateSingleItem(OrderItemRequest item) {
        log.debug("Processing item: SKU={}, quantity={}", item.getSku(), item.getQuantity());
        
        Optional<Inventory> inventoryOpt = inventoryRepository.findBySku(item.getSku());
        
        if (inventoryOpt.isEmpty()) {
            log.error("Inventory not found for SKU: {}", item.getSku());
            throw new RuntimeException("Inventory not found for SKU: " + item.getSku());
        }
        
        Inventory inventory = inventoryOpt.get();
        
        if (inventory.getQuantity() < item.getQuantity()) {
            log.error("Insufficient inventory for SKU {}: available={}, requested={}", 
                    item.getSku(), inventory.getQuantity(), item.getQuantity());
            throw new RuntimeException("Insufficient inventory for SKU: " + item.getSku());
        }
        
        Long newQuantity = inventory.getQuantity() - item.getQuantity();
        inventory.setQuantity(newQuantity);
        
        inventoryRepository.save(inventory);
        
        log.debug("Updated inventory for SKU {}: new quantity = {}", item.getSku(), newQuantity);
    }

    /**
     * Emit inventory change event (basic logging implementation)
     * @param eventType the type of event
     * @param data the event data
     */
    private void emitInventoryChangeEvent(String eventType, Object data) {
        log.info("=== INVENTORY EVENT ===");
        log.info("Event Type: {}", eventType);
        log.info("Timestamp: {}", java.time.LocalDateTime.now());
        log.info("Data: {}", data);
        log.info("=====================");
    }

    /**
     * Map Inventory entity to InventoryResponse DTO
     * @param inventory the inventory entity
     * @return the inventory response DTO
     */
    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .sku(inventory.getSku())
                .quantity(inventory.getQuantity())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
