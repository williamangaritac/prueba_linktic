package com.linktic_test.orders_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.orders_service.model.dtos.OrderItemRequest;
import com.linktic_test.orders_service.model.dtos.OrderRequest;
import com.linktic_test.orders_service.model.dtos.OrderResponse;
import com.linktic_test.orders_service.services.OrderService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
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
        // Setup test order response
        OrderResponse.OrderItemResponse itemResponse = new OrderResponse.OrderItemResponse();
        itemResponse.setSku("TEST001");
        itemResponse.setPrice(99.99);
        itemResponse.setQuantity(2L);

        testOrderResponse = new OrderResponse();
        testOrderResponse.setId(1L);
        testOrderResponse.setOrderNumber("ORD-20250127-001");
        testOrderResponse.setOrderItems(Arrays.asList(itemResponse));

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
        mockMvc.perform(post("/api/v1/orders")
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
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_Success() throws Exception {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(testOrderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250127-001"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        // Arrange
        when(orderService.getOrderById(999L))
                .thenThrow(new RuntimeException("Order not found with ID: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/999"))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).getOrderById(999L);
    }

    @Test
    void getOrderByOrderNumber_Success() throws Exception {
        // Arrange
        when(orderService.getOrderByOrderNumber("ORD-20250127-001")).thenReturn(testOrderResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders/number/ORD-20250127-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-20250127-001"));

        verify(orderService, times(1)).getOrderByOrderNumber("ORD-20250127-001");
    }

    @Test
    void getAllOrders_Success() throws Exception {
        // Arrange
        OrderResponse.OrderItemResponse item1 = new OrderResponse.OrderItemResponse();
        item1.setSku("TEST001");
        item1.setPrice(99.99);
        item1.setQuantity(2L);

        OrderResponse order1 = new OrderResponse();
        order1.setId(1L);
        order1.setOrderNumber("ORD-20250127-001");
        order1.setOrderItems(Arrays.asList(item1));

        OrderResponse order2 = new OrderResponse();
        order2.setId(2L);
        order2.setOrderNumber("ORD-20250127-002");
        order2.setOrderItems(Arrays.asList(item1));

        List<OrderResponse> orders = Arrays.asList(order1, order2);
        when(orderService.getAllOrders()).thenReturn(orders);

        // Act & Assert
        mockMvc.perform(get("/api/v1/orders"))
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

        OrderResponse.OrderItemResponse responseItem1 = new OrderResponse.OrderItemResponse();
        responseItem1.setSku("TEST001");
        responseItem1.setPrice(99.99);
        responseItem1.setQuantity(2L);

        OrderResponse.OrderItemResponse responseItem2 = new OrderResponse.OrderItemResponse();
        responseItem2.setSku("TEST002");
        responseItem2.setPrice(149.99);
        responseItem2.setQuantity(1L);

        OrderResponse multiItemResponse = new OrderResponse();
        multiItemResponse.setId(1L);
        multiItemResponse.setOrderNumber("ORD-20250127-003");
        multiItemResponse.setOrderItems(Arrays.asList(responseItem1, responseItem2));

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(multiItemResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders")
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
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).createOrder(any(OrderRequest.class));
    }
}

