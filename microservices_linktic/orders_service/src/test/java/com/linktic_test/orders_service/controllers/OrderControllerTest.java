package com.linktic_test.orders_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.orders_service.model.dtos.OrderItemRequest;
import com.linktic_test.orders_service.model.dtos.OrderItemsResponse;
import com.linktic_test.orders_service.model.dtos.OrderRequest;
import com.linktic_test.orders_service.model.dtos.OrderResponse;
import com.linktic_test.orders_service.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderResponse testOrderResponse;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        // Setup test order response - OrderResponse is a record, use constructor
        OrderItemsResponse itemResponse = new OrderItemsResponse(1L, "TEST001", 99.99, 2L);

        testOrderResponse = new OrderResponse(1L, "ORD-20250127-001", Arrays.asList(itemResponse));

        // Setup test order request
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setSku("TEST001");
        itemRequest.setPrice(99.99);
        itemRequest.setQuantity(2L);

        testOrderRequest = new OrderRequest();
        testOrderRequest.setOrderItems(Arrays.asList(itemRequest));
    }

    @Test
    void createOrder_Success() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(testOrderResponse);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250127-001"))
                .andExpect(jsonPath("$.orderItems.length()").value(1))
                .andExpect(jsonPath("$.orderItems[0].sku").value("TEST001"));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void createOrder_InvalidRequest_BadRequest() throws Exception {
        // Arrange - Empty order items
        OrderRequest invalidRequest = new OrderRequest();
        invalidRequest.setOrderItems(Arrays.asList());

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_Success() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(java.util.Optional.of(testOrderResponse));

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250127-001"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void getOrderByOrderNumber_Success() throws Exception {
        // Arrange
        when(orderService.getOrderByOrderNumber("ORD-20250127-001")).thenReturn(java.util.Optional.of(testOrderResponse));

        // Act & Assert
        mockMvc.perform(get("/orders/number/ORD-20250127-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250127-001"));

        verify(orderService, times(1)).getOrderByOrderNumber("ORD-20250127-001");
    }

    @Test
    void getAllOrders_Success() throws Exception {
        // Arrange
        OrderItemsResponse item1 = new OrderItemsResponse(1L, "TEST001", 99.99, 2L);

        OrderResponse order1 = new OrderResponse(1L, "ORD-20250127-001", Arrays.asList(item1));
        OrderResponse order2 = new OrderResponse(2L, "ORD-20250127-002", Arrays.asList(item1));

        List<OrderResponse> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderNumber").value("ORD-20250127-001"))
                .andExpect(jsonPath("$[1].orderNumber").value("ORD-20250127-002"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void createOrder_WithMultipleItems_Success() throws Exception {
        // Arrange
        OrderItemRequest item1 = new OrderItemRequest();
        item1.setSku("TEST001");
        item1.setPrice(99.99);
        item1.setQuantity(2L);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setSku("TEST002");
        item2.setPrice(149.99);
        item2.setQuantity(1L);

        OrderRequest multiItemRequest = new OrderRequest();
        multiItemRequest.setOrderItems(Arrays.asList(item1, item2));

        OrderItemsResponse responseItem1 = new OrderItemsResponse(1L, "TEST001", 99.99, 2L);
        OrderItemsResponse responseItem2 = new OrderItemsResponse(2L, "TEST002", 149.99, 1L);

        OrderResponse multiItemResponse = new OrderResponse(1L, "ORD-20250127-003", Arrays.asList(responseItem1, responseItem2));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(multiItemResponse);

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(multiItemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderItems.length()").value(2))
                .andExpect(jsonPath("$.orderItems[0].sku").value("TEST001"))
                .andExpect(jsonPath("$.orderItems[1].sku").value("TEST002"));

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }

    @Test
    void createOrder_ServiceException_InternalServerError() throws Exception {
        // Arrange
        when(orderService.createOrder(any(OrderRequest.class)))
                .thenThrow(new RuntimeException("Failed to update inventory"));

        // Act & Assert
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }
}

