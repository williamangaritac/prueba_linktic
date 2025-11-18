package com.linktic_test.inventory_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.inventory_service.model.dtos.InventoryResponse;
import com.linktic_test.inventory_service.model.dtos.InventoryUpdateRequest;
import com.linktic_test.inventory_service.model.dtos.OrderItemRequest;
import com.linktic_test.inventory_service.services.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryService inventoryService;

    private InventoryResponse testInventoryResponse;

    @BeforeEach
    void setUp() {
        testInventoryResponse = InventoryResponse.builder()
                .id(1L)
                .sku("TEST001")
                .quantity(100L)
                .build();
    }

    @Test
    void getInventoryBySku_Success() throws Exception {
        // Arrange
        when(inventoryService.getInventoryBySku("TEST001")).thenReturn(testInventoryResponse);

        // Act & Assert
        mockMvc.perform(get("/inventory/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST001"))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(inventoryService, times(1)).getInventoryBySku("TEST001");
    }

    @Test
    void getInventoryBySku_NotFound() throws Exception {
        // Arrange
        when(inventoryService.getInventoryBySku("NONEXISTENT"))
                .thenThrow(new RuntimeException("Inventory not found for SKU: NONEXISTENT"));

        // Act & Assert
        mockMvc.perform(get("/inventory/NONEXISTENT"))
                .andExpect(status().isNotFound());

        verify(inventoryService, times(1)).getInventoryBySku("NONEXISTENT");
    }

    @Test
    void getAllInventory_Success() throws Exception {
        // Arrange
        InventoryResponse response1 = InventoryResponse.builder()
                .sku("TEST001")
                .quantity(100L)
                .build();

        InventoryResponse response2 = InventoryResponse.builder()
                .sku("TEST002")
                .quantity(200L)
                .build();

        List<InventoryResponse> responses = Arrays.asList(response1, response2);
        when(inventoryService.getAllInventory()).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sku").value("TEST001"))
                .andExpect(jsonPath("$[1].sku").value("TEST002"));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void updateInventoryQuantity_Success() throws Exception {
        // Arrange
        InventoryUpdateRequest request = new InventoryUpdateRequest();
        request.setSku("TEST001");
        request.setQuantity(150L);

        InventoryResponse response = InventoryResponse.builder()
                .sku("TEST001")
                .quantity(150L)
                .build();

        when(inventoryService.updateInventoryQuantity(any(InventoryUpdateRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/inventory/TEST001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST001"))
                .andExpect(jsonPath("$.quantity").value(150));

        verify(inventoryService, times(1)).updateInventoryQuantity(any(InventoryUpdateRequest.class));
    }

    @Test
    void updateInventoryAfterPurchase_Success() throws Exception {
        // Arrange
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setSku("TEST001");
        item1.setPrice(100.0);
        item1.setQuantity(10L);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setSku("TEST002");
        item2.setPrice(200.0);
        item2.setQuantity(5L);

        List<OrderItemRequest> orderItems = Arrays.asList(item1, item2);

        when(inventoryService.updateInventoryAfterPurchase(anyList())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/inventory/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItems)))
                .andExpect(status().isOk());

        verify(inventoryService, times(1)).updateInventoryAfterPurchase(anyList());
    }

    @Test
    void updateInventoryAfterPurchase_Failure() throws Exception {
        // Arrange
        OrderItemRequest item = new OrderItemRequest();
        item.setSku("TEST001");
        item.setPrice(100.0);
        item.setQuantity(10L);

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        when(inventoryService.updateInventoryAfterPurchase(anyList())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/inventory/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItems)))
                .andExpect(status().isBadRequest());

        verify(inventoryService, times(1)).updateInventoryAfterPurchase(anyList());
    }

    @Test
    void updateInventoryAfterPurchase_InsufficientStock() throws Exception {
        // Arrange
        OrderItemRequest item = new OrderItemRequest();
        item.setSku("TEST001");
        item.setPrice(100.0);
        item.setQuantity(1000L);

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        when(inventoryService.updateInventoryAfterPurchase(anyList()))
                .thenThrow(new RuntimeException("Insufficient inventory for SKU: TEST001"));

        // Act & Assert
        mockMvc.perform(post("/inventory/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItems)))
                .andExpect(status().isBadRequest());

        verify(inventoryService, times(1)).updateInventoryAfterPurchase(anyList());
    }
}

