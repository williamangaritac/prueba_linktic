package com.linktic_test.inventory_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.inventory_service.model.dtos.InventoryRequest;
import com.linktic_test.inventory_service.model.dtos.InventoryResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
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
        testInventoryResponse = new InventoryResponse();
        testInventoryResponse.setId(1L);
        testInventoryResponse.setSku("TEST001");
        testInventoryResponse.setQuantity(100L);
    }

    @Test
    void getInventoryBySku_Success() throws Exception {
        // Arrange
        when(inventoryService.getInventoryBySku("TEST001")).thenReturn(testInventoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory/TEST001"))
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
        mockMvc.perform(get("/api/v1/inventory/NONEXISTENT"))
                .andExpect(status().isInternalServerError());

        verify(inventoryService, times(1)).getInventoryBySku("NONEXISTENT");
    }

    @Test
    void getAllInventory_Success() throws Exception {
        // Arrange
        InventoryResponse response1 = new InventoryResponse();
        response1.setSku("TEST001");
        response1.setQuantity(100L);

        InventoryResponse response2 = new InventoryResponse();
        response2.setSku("TEST002");
        response2.setQuantity(200L);

        List<InventoryResponse> responses = Arrays.asList(response1, response2);
        when(inventoryService.getAllInventory()).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sku").value("TEST001"))
                .andExpect(jsonPath("$[1].sku").value("TEST002"));

        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void updateInventory_Success() throws Exception {
        // Arrange
        InventoryRequest request = new InventoryRequest();
        request.setSku("TEST001");
        request.setQuantity(150L);

        InventoryResponse response = new InventoryResponse();
        response.setSku("TEST001");
        response.setQuantity(150L);

        when(inventoryService.updateInventory(any(InventoryRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/v1/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST001"))
                .andExpect(jsonPath("$.quantity").value(150));

        verify(inventoryService, times(1)).updateInventory(any(InventoryRequest.class));
    }

    @Test
    void updateInventoryAfterPurchase_Success() throws Exception {
        // Arrange
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setSku("TEST001");
        item1.setQuantity(10L);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setSku("TEST002");
        item2.setQuantity(5L);

        List<OrderItemRequest> orderItems = Arrays.asList(item1, item2);

        when(inventoryService.updateInventoryAfterPurchase(anyList())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory/purchase")
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
        item.setQuantity(10L);

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        when(inventoryService.updateInventoryAfterPurchase(anyList())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory/purchase")
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
        item.setQuantity(1000L);

        List<OrderItemRequest> orderItems = Arrays.asList(item);

        when(inventoryService.updateInventoryAfterPurchase(anyList()))
                .thenThrow(new RuntimeException("Insufficient inventory for SKU: TEST001"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/inventory/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItems)))
                .andExpect(status().isInternalServerError());

        verify(inventoryService, times(1)).updateInventoryAfterPurchase(anyList());
    }
}

