package com.linktic_test.notifications_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.notifications_service.model.dto.OrderEventDTO;
import com.linktic_test.notifications_service.model.dto.OrderItemDTO;
import com.linktic_test.notifications_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    private String testOrderEventJson;
    private OrderEventDTO testOrderEvent;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test order event
        OrderItemDTO item1 = OrderItemDTO.builder()
                .sku("TEST001")
                .productName("Test Product 1")
                .quantity(2L)
                .price(99.99)
                .build();

        testOrderEvent = OrderEventDTO.builder()
                .orderId(1L)
                .orderNumber("ORD-20250127-001")
                .customerEmail("test@example.com")
                .items(Arrays.asList(item1))
                .totalAmount(199.98)
                .eventType("ORDER_CREATED")
                .build();

        testOrderEventJson = """
                {
                    "orderId": 1,
                    "orderNumber": "ORD-20250127-001",
                    "items": [
                        {
                            "sku": "TEST001",
                            "productName": "Test Product 1",
                            "quantity": 2,
                            "price": 99.99
                        }
                    ],
                    "totalAmount": 199.98,
                    "eventType": "ORDER_CREATED"
                }
                """;
    }

    @Test
    void consumeOrderEvent_ValidJson_Success() throws Exception {
        // Arrange
        when(objectMapper.readValue(testOrderEventJson, OrderEventDTO.class)).thenReturn(testOrderEvent);
        doNothing().when(notificationService).processOrderEvent(any(OrderEventDTO.class));

        // Act
        orderEventConsumer.consumeOrderEvent(testOrderEventJson);

        // Assert
        verify(objectMapper, times(1)).readValue(testOrderEventJson, OrderEventDTO.class);
        verify(notificationService, times(1)).processOrderEvent(testOrderEvent);
    }

    @Test
    void consumeOrderEvent_InvalidJson_HandlesException() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";
        when(objectMapper.readValue(invalidJson, OrderEventDTO.class))
                .thenThrow(new RuntimeException("Invalid JSON"));

        // Act
        orderEventConsumer.consumeOrderEvent(invalidJson);

        // Assert
        verify(objectMapper, times(1)).readValue(invalidJson, OrderEventDTO.class);
        verify(notificationService, never()).processOrderEvent(any(OrderEventDTO.class));
    }

    @Test
    void consumeOrderEvent_NullMessage_HandlesGracefully() throws Exception {
        // Act & Assert
        assertDoesNotThrow(() -> {
            orderEventConsumer.consumeOrderEvent(null);
        });

        verify(objectMapper, never()).readValue(anyString(), eq(OrderEventDTO.class));
        verify(notificationService, never()).processOrderEvent(any(OrderEventDTO.class));
    }

    @Test
    void consumeOrderEvent_EmptyMessage_HandlesGracefully() throws Exception {
        // Arrange
        String emptyJson = "";

        // Act
        orderEventConsumer.consumeOrderEvent(emptyJson);

        // Assert
        // Should handle empty message gracefully
        verify(notificationService, never()).processOrderEvent(any(OrderEventDTO.class));
    }

    @Test
    void consumeOrderEvent_WithMultipleItems_Success() throws Exception {
        // Arrange
        OrderItemDTO item1 = OrderItemDTO.builder()
                .sku("TEST001")
                .productName("Product 1")
                .quantity(2L)
                .price(50.00)
                .build();

        OrderItemDTO item2 = OrderItemDTO.builder()
                .sku("TEST002")
                .productName("Product 2")
                .quantity(1L)
                .price(100.00)
                .build();

        OrderEventDTO multiItemEvent = OrderEventDTO.builder()
                .orderId(2L)
                .orderNumber("ORD-20250127-002")
                .items(Arrays.asList(item1, item2))
                .totalAmount(200.00)
                .eventType("ORDER_CREATED")
                .build();

        String multiItemJson = """
                {
                    "orderId": 2,
                    "orderNumber": "ORD-20250127-002",
                    "items": [
                        {
                            "sku": "TEST001",
                            "productName": "Product 1",
                            "quantity": 2,
                            "price": 50.00
                        },
                        {
                            "sku": "TEST002",
                            "productName": "Product 2",
                            "quantity": 1,
                            "price": 100.00
                        }
                    ],
                    "totalAmount": 200.00,
                    "eventType": "ORDER_CREATED"
                }
                """;

        when(objectMapper.readValue(multiItemJson, OrderEventDTO.class)).thenReturn(multiItemEvent);
        doNothing().when(notificationService).processOrderEvent(any(OrderEventDTO.class));

        // Act
        orderEventConsumer.consumeOrderEvent(multiItemJson);

        // Assert
        verify(objectMapper, times(1)).readValue(multiItemJson, OrderEventDTO.class);
        verify(notificationService, times(1)).processOrderEvent(multiItemEvent);
    }

    @Test
    void consumeOrderEvent_ServiceThrowsException_LogsError() throws Exception {
        // Arrange
        when(objectMapper.readValue(testOrderEventJson, OrderEventDTO.class)).thenReturn(testOrderEvent);
        doThrow(new RuntimeException("Database connection failed"))
                .when(notificationService).processOrderEvent(any(OrderEventDTO.class));

        // Act
        orderEventConsumer.consumeOrderEvent(testOrderEventJson);

        // Assert
        verify(objectMapper, times(1)).readValue(testOrderEventJson, OrderEventDTO.class);
        verify(notificationService, times(1)).processOrderEvent(testOrderEvent);
        // Exception should be logged but not propagated
    }

    @Test
    void consumeOrderEvent_MalformedJson_HandlesException() throws Exception {
        // Arrange
        String malformedJson = "{ \"orderId\": \"not-a-number\" }";
        when(objectMapper.readValue(malformedJson, OrderEventDTO.class))
                .thenThrow(new RuntimeException("Cannot deserialize"));

        // Act
        orderEventConsumer.consumeOrderEvent(malformedJson);

        // Assert
        verify(objectMapper, times(1)).readValue(malformedJson, OrderEventDTO.class);
        verify(notificationService, never()).processOrderEvent(any(OrderEventDTO.class));
    }

    @Test
    void consumeOrderEvent_MissingRequiredFields_HandlesGracefully() throws Exception {
        // Arrange
        String incompleteJson = """
                {
                    "orderNumber": "ORD-20250127-003"
                }
                """;

        OrderEventDTO incompleteEvent = OrderEventDTO.builder()
                .orderNumber("ORD-20250127-003")
                .build();

        when(objectMapper.readValue(incompleteJson, OrderEventDTO.class)).thenReturn(incompleteEvent);
        doNothing().when(notificationService).processOrderEvent(any(OrderEventDTO.class));

        // Act
        orderEventConsumer.consumeOrderEvent(incompleteJson);

        // Assert
        verify(objectMapper, times(1)).readValue(incompleteJson, OrderEventDTO.class);
        verify(notificationService, times(1)).processOrderEvent(incompleteEvent);
    }
}

