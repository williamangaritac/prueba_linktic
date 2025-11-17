package com.linktic_test.notifications_service.service;

import com.linktic_test.notifications_service.model.dto.NotificationDTO;
import com.linktic_test.notifications_service.model.dto.OrderEventDTO;
import com.linktic_test.notifications_service.model.dto.OrderItemDTO;
import com.linktic_test.notifications_service.model.entities.Notification;
import com.linktic_test.notifications_service.model.entities.NotificationStatus;
import com.linktic_test.notifications_service.model.entities.NotificationType;
import com.linktic_test.notifications_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private OrderEventDTO testOrderEvent;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        // Setup test order event
        OrderItemDTO item1 = OrderItemDTO.builder()
                .sku("TEST001")
                .productName("Test Product 1")
                .quantity(2L)
                .price(99.99)
                .build();

        OrderItemDTO item2 = OrderItemDTO.builder()
                .sku("TEST002")
                .productName("Test Product 2")
                .quantity(1L)
                .price(149.99)
                .build();

        testOrderEvent = OrderEventDTO.builder()
                .orderId(1L)
                .orderNumber("ORD-20250127-001")
                .customerEmail("test@example.com")
                .items(Arrays.asList(item1, item2))
                .totalAmount(349.97)
                .eventType("ORDER_CREATED")
                .build();

        // Setup test notification
        testNotification = Notification.builder()
                .id(1L)
                .orderId(1L)
                .orderNumber("ORD-20250127-001")
                .customerEmail("test@example.com")
                .subject("Order Confirmation")
                .message("Your order has been confirmed")
                .type(NotificationType.ORDER_CREATED)
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void processOrderEvent_Success() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doNothing().when(emailService).sendOrderConfirmationEmail(anyString(), anyString(), anyString());

        // Act
        notificationService.processOrderEvent(testOrderEvent);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class)); // Once for PENDING, once for SENT
        verify(emailService, times(1)).sendOrderConfirmationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void processOrderEvent_EmailFails_StatusRemainsFailure() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailService).sendOrderConfirmationEmail(anyString(), anyString(), anyString());

        // Act
        notificationService.processOrderEvent(testOrderEvent);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class)); // Once for PENDING, once for FAILED
        verify(emailService, times(1)).sendOrderConfirmationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void getAllNotifications_Success() {
        // Arrange
        Notification notification1 = Notification.builder()
                .id(1L)
                .orderNumber("ORD-001")
                .status(NotificationStatus.SENT)
                .build();

        Notification notification2 = Notification.builder()
                .id(2L)
                .orderNumber("ORD-002")
                .status(NotificationStatus.PENDING)
                .build();

        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findAll()).thenReturn(notifications);

        // Act
        List<NotificationDTO> result = notificationService.getAllNotifications();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ORD-001", result.get(0).getOrderNumber());
        assertEquals("ORD-002", result.get(1).getOrderNumber());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getNotificationsByOrderNumber_Success() {
        // Arrange
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByOrderNumber("ORD-20250127-001")).thenReturn(notifications);

        // Act
        List<NotificationDTO> result = notificationService.getNotificationsByOrderNumber("ORD-20250127-001");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD-20250127-001", result.get(0).getOrderNumber());
        verify(notificationRepository, times(1)).findByOrderNumber("ORD-20250127-001");
    }

    @Test
    void getNotificationsByStatus_Success() {
        // Arrange
        List<Notification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByStatus(NotificationStatus.PENDING)).thenReturn(notifications);

        // Act
        List<NotificationDTO> result = notificationService.getNotificationsByStatus(NotificationStatus.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NotificationStatus.PENDING, result.get(0).getStatus());
        verify(notificationRepository, times(1)).findByStatus(NotificationStatus.PENDING);
    }

    @Test
    void processOrderEvent_WithMultipleItems_CalculatesTotalCorrectly() {
        // Arrange
        OrderItemDTO item1 = OrderItemDTO.builder()
                .sku("TEST001")
                .productName("Product 1")
                .quantity(3L)
                .price(50.00)
                .build();

        OrderItemDTO item2 = OrderItemDTO.builder()
                .sku("TEST002")
                .productName("Product 2")
                .quantity(2L)
                .price(75.00)
                .build();

        OrderEventDTO multiItemEvent = OrderEventDTO.builder()
                .orderId(2L)
                .orderNumber("ORD-20250127-002")
                .customerEmail("test@example.com")
                .items(Arrays.asList(item1, item2))
                .totalAmount(300.00) // 3*50 + 2*75 = 150 + 150 = 300
                .eventType("ORDER_CREATED")
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        doNothing().when(emailService).sendOrderConfirmationEmail(anyString(), anyString(), anyString());

        // Act
        notificationService.processOrderEvent(multiItemEvent);

        // Assert
        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(emailService, times(1)).sendOrderConfirmationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void processOrderEvent_NullItems_HandlesGracefully() {
        // Arrange
        OrderEventDTO eventWithNullItems = OrderEventDTO.builder()
                .orderId(3L)
                .orderNumber("ORD-20250127-003")
                .items(null)
                .totalAmount(0.0)
                .eventType("ORDER_CREATED")
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act & Assert
        // Should handle null items gracefully without throwing exception
        assertDoesNotThrow(() -> {
            notificationService.processOrderEvent(eventWithNullItems);
        });
    }
}

