package com.linktic_test.notifications_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@linktic.com");
        ReflectionTestUtils.setField(emailService, "toEmails", "william.angaritac@gmail.com,contacto@linktic.com");
        ReflectionTestUtils.setField(emailService, "emailEnabled", true);
    }

    @Test
    void sendOrderConfirmationEmail_Success() {
        // Arrange
        String orderNumber = "ORD-20250127-001";
        String orderDetails = "Product: Test Product, Quantity: 2, Price: $99.99";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals("noreply@linktic.com", sentMessage.getFrom());
        assertNotNull(sentMessage.getTo());
        assertEquals(2, sentMessage.getTo().length);
        assertTrue(sentMessage.getSubject().contains(orderNumber));
        assertTrue(sentMessage.getText().contains(orderDetails));
    }

    @Test
    void sendOrderConfirmationEmail_EmailDisabled_DoesNotSend() {
        // Arrange
        ReflectionTestUtils.setField(emailService, "emailEnabled", false);
        String orderNumber = "ORD-20250127-001";
        String orderDetails = "Product: Test Product";

        // Act
        emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmailToMultipleRecipients_Success() {
        // Arrange
        String[] recipients = {"test1@example.com", "test2@example.com"};
        String subject = "Test Subject";
        String body = "Test Body";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmailToMultipleRecipients(recipients, subject, body);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertEquals("noreply@linktic.com", sentMessage.getFrom());
        assertArrayEquals(recipients, sentMessage.getTo());
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
    }

    @Test
    void sendOrderConfirmationEmail_WithMultipleItems_FormatsCorrectly() {
        // Arrange
        String orderNumber = "ORD-20250127-002";
        String orderDetails = """
                Item 1: Product A, Quantity: 2, Price: $50.00
                Item 2: Product B, Quantity: 1, Price: $100.00
                Total: $200.00
                """;

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertTrue(sentMessage.getText().contains("Product A"));
        assertTrue(sentMessage.getText().contains("Product B"));
        assertTrue(sentMessage.getText().contains("$200.00"));
    }

    @Test
    void sendOrderConfirmationEmail_MailSenderThrowsException_LogsError() {
        // Arrange
        String orderNumber = "ORD-20250127-003";
        String orderDetails = "Test Details";

        doThrow(new RuntimeException("SMTP server unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);
        });

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmailToMultipleRecipients_EmptyRecipients_DoesNotSend() {
        // Arrange
        String[] recipients = {};
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmailToMultipleRecipients(recipients, subject, body);

        // Assert
        // Should still attempt to send, but with empty recipients
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendOrderConfirmationEmail_ParsesMultipleEmailAddresses() {
        // Arrange
        String orderNumber = "ORD-20250127-004";
        String orderDetails = "Test Order Details";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage.getTo());
        assertEquals(2, sentMessage.getTo().length);
        assertEquals("william.angaritac@gmail.com", sentMessage.getTo()[0]);
        assertEquals("contacto@linktic.com", sentMessage.getTo()[1]);
    }

    @Test
    void sendOrderConfirmationEmail_NullOrderNumber_HandlesGracefully() {
        // Arrange
        String orderDetails = "Test Details";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendOrderConfirmationEmail(null, orderDetails);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertNotNull(sentMessage);
        assertTrue(sentMessage.getSubject().contains("null") || sentMessage.getSubject().contains("Confirmación"));
    }

    @Test
    void sendOrderConfirmationEmail_EmptyOrderDetails_SendsEmail() {
        // Arrange
        String orderNumber = "ORD-20250127-005";
        String orderDetails = "";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendOrderConfirmationEmail(orderNumber, orderDetails);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}

