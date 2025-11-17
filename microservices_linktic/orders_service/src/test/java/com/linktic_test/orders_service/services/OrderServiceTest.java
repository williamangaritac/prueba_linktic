package com.linktic_test.orders_service.services;

import com.linktic_test.orders_service.model.dtos.OrderItemRequest;
import com.linktic_test.orders_service.model.dtos.OrderRequest;
import com.linktic_test.orders_service.model.dtos.OrderResponse;
import com.linktic_test.orders_service.model.entities.Order;
import com.linktic_test.orders_service.model.entities.OrderItems;
import com.linktic_test.orders_service.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderRequest testOrderRequest;

    @BeforeEach
    void setUp() {
        // Setup test order
        testOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20250127-001")
                .build();

        OrderItems item1 = OrderItems.builder()
                .id(1L)
                .sku("TEST001")
                .price(99.99)
                .quantity(2L)
                .order(testOrder)
                .build();

        testOrder.setOrderItems(Arrays.asList(item1));

        // Setup test order request
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setSku("TEST001");
        itemRequest.setPrice(99.99);
        itemRequest.setQuantity(2L);

        testOrderRequest = new OrderRequest();
        testOrderRequest.setOrderItems(Arrays.asList(itemRequest));

        // Setup WebClient mock chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponse response = orderService.createOrder(testOrderRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.orderNumber());
        assertTrue(response.orderNumber().startsWith("ORD-"));
        assertEquals(1, response.orderItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_InventoryUpdateFails_ThrowsException() {
        // Arrange
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.error(new RuntimeException("Inventory service unavailable")));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(testOrderRequest);
        });

        assertTrue(exception.getMessage().contains("inventario"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<OrderResponse> response = orderService.getOrderById(1L);

        // Assert
        assertTrue(response.isPresent());
        assertEquals("ORD-20250127-001", response.get().orderNumber());
        assertEquals(1, response.get().orderItems().size());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_NotFound_ReturnsEmpty() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<OrderResponse> response = orderService.getOrderById(1L);

        // Assert
        assertFalse(response.isPresent());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderByOrderNumber_Success() {
        // Arrange
        when(orderRepository.findByOrderNumber("ORD-20250127-001")).thenReturn(Optional.of(testOrder));

        // Act
        Optional<OrderResponse> response = orderService.getOrderByOrderNumber("ORD-20250127-001");

        // Assert
        assertTrue(response.isPresent());
        assertEquals("ORD-20250127-001", response.get().orderNumber());
        verify(orderRepository, times(1)).findByOrderNumber("ORD-20250127-001");
    }

    @Test
    void getAllOrders_Success() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<OrderResponse> responses = orderService.getAllOrders();

        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("ORD-20250127-001", responses.get(0).orderNumber());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void generateOrderNumber_CreatesUniqueNumber() {
        // This test verifies the order number format
        // We can't directly test the private method, but we can verify through createOrder

        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act
        OrderResponse response = orderService.createOrder(testOrderRequest);

        // Assert
        assertNotNull(response.orderNumber());
        assertTrue(response.orderNumber().matches("ORD-\\d{8}-\\d{3}"));
    }

    @Test
    void createOrder_WithMultipleItems_Success() {
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

        Order multiItemOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20250127-002")
                .build();

        OrderItems orderItem1 = OrderItems.builder()
                .sku("TEST001")
                .price(99.99)
                .quantity(2L)
                .order(multiItemOrder)
                .build();

        OrderItems orderItem2 = OrderItems.builder()
                .sku("TEST002")
                .price(149.99)
                .quantity(1L)
                .order(multiItemOrder)
                .build();

        multiItemOrder.setOrderItems(Arrays.asList(orderItem1, orderItem2));

        when(orderRepository.save(any(Order.class))).thenReturn(multiItemOrder);

        // Act
        OrderResponse response = orderService.createOrder(multiItemRequest);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.orderItems().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_EmptyItems_ThrowsException() {
        // Arrange
        OrderRequest emptyRequest = new OrderRequest();
        emptyRequest.setOrderItems(Arrays.asList());

        // Act & Assert
        // This should throw an exception or handle empty items
        // Depending on your business logic, adjust this test
        assertThrows(Exception.class, () -> {
            orderService.createOrder(emptyRequest);
        });
    }
}

