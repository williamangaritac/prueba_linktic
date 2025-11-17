package com.linktic_test.inventory_service.services;

import com.linktic_test.inventory_service.model.dtos.InventoryResponse;
import com.linktic_test.inventory_service.model.dtos.InventoryUpdateRequest;
import com.linktic_test.inventory_service.model.dtos.OrderItemRequest;
import com.linktic_test.inventory_service.model.entities.Inventory;
import com.linktic_test.inventory_service.repositories.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory testInventory;
    private InventoryUpdateRequest testInventoryUpdateRequest;

    @BeforeEach
    void setUp() {
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setSku("TEST001");
        testInventory.setQuantity(100L);

        testInventoryUpdateRequest = new InventoryUpdateRequest();
        testInventoryUpdateRequest.setSku("TEST001");
        testInventoryUpdateRequest.setQuantity(100L);
    }

    @Test
    void getInventoryBySku_Success() {
        // Arrange
        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.of(testInventory));

        // Act
        InventoryResponse response = inventoryService.getInventoryBySku("TEST001");

        // Assert
        assertNotNull(response);
        assertEquals("TEST001", response.getSku());
        assertEquals(100L, response.getQuantity());
        verify(inventoryRepository, times(1)).findBySku("TEST001");
    }

    @Test
    void getInventoryBySku_NotFound_ThrowsException() {
        // Arrange
        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.getInventoryBySku("TEST001");
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(inventoryRepository, times(1)).findBySku("TEST001");
    }

    @Test
    void updateInventoryAfterPurchase_Success() {
        // Arrange
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setSku("TEST001");
        item1.setQuantity(10L);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setSku("TEST002");
        item2.setQuantity(5L);

        List<OrderItemRequest> orderItems = Arrays.asList(item1, item2);

        Inventory inventory1 = new Inventory();
        inventory1.setSku("TEST001");
        inventory1.setQuantity(100L);

        Inventory inventory2 = new Inventory();
        inventory2.setSku("TEST002");
        inventory2.setQuantity(50L);

        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.of(inventory1));
        when(inventoryRepository.findBySku("TEST002")).thenReturn(Optional.of(inventory2));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        boolean result = inventoryService.updateInventoryAfterPurchase(orderItems);

        // Assert
        assertTrue(result);
        assertEquals(90L, inventory1.getQuantity());
        assertEquals(45L, inventory2.getQuantity());
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
    }

    @Test
    void updateInventoryAfterPurchase_InsufficientStock_ThrowsException() {
        // Arrange
        OrderItemRequest item = new OrderItemRequest();
        item.setSku("TEST001");
        item.setQuantity(150L); // More than available

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        Inventory inventory = new Inventory();
        inventory.setSku("TEST001");
        inventory.setQuantity(100L);

        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.of(inventory));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateInventoryAfterPurchase(orderItems);
        });
        
        assertTrue(exception.getMessage().contains("Insufficient inventory"));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void updateInventoryAfterPurchase_ItemNotFound_ThrowsException() {
        // Arrange
        OrderItemRequest item = new OrderItemRequest();
        item.setSku("NONEXISTENT");
        item.setQuantity(10L);

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        when(inventoryRepository.findBySku("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateInventoryAfterPurchase(orderItems);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void updateInventoryQuantity_Success() {
        // Arrange
        InventoryUpdateRequest updateRequest = new InventoryUpdateRequest();
        updateRequest.setSku("TEST001");
        updateRequest.setQuantity(200L);

        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // Act
        InventoryResponse response = inventoryService.updateInventoryQuantity(updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("TEST001", response.getSku());
        verify(inventoryRepository, times(1)).findBySku("TEST001");
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void getAllInventory_Success() {
        // Arrange
        Inventory inventory1 = new Inventory();
        inventory1.setSku("TEST001");
        inventory1.setQuantity(100L);

        Inventory inventory2 = new Inventory();
        inventory2.setSku("TEST002");
        inventory2.setQuantity(200L);

        List<Inventory> inventories = Arrays.asList(inventory1, inventory2);
        when(inventoryRepository.findAll()).thenReturn(inventories);

        // Act
        List<InventoryResponse> response = inventoryService.getAllInventory();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("TEST001", response.get(0).getSku());
        assertEquals("TEST002", response.get(1).getSku());
        verify(inventoryRepository, times(1)).findAll();
    }

    @Test
    void updateSingleItem_Success() {
        // Arrange
        OrderItemRequest item = new OrderItemRequest();
        item.setSku("TEST001");
        item.setQuantity(10L);

        when(inventoryRepository.findBySku("TEST001")).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        inventoryService.updateInventoryAfterPurchase(Arrays.asList(item));

        // Assert
        assertEquals(90L, testInventory.getQuantity());
        verify(inventoryRepository, times(1)).save(testInventory);
    }

    @Test
    void updateInventoryQuantity_NotFound_ThrowsException() {
        // Arrange
        InventoryUpdateRequest updateRequest = new InventoryUpdateRequest();
        updateRequest.setSku("NONEXISTENT");
        updateRequest.setQuantity(100L);

        when(inventoryRepository.findBySku("NONEXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.updateInventoryQuantity(updateRequest);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }
}

