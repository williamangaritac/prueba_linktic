package com.linktic_test.notifications_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para enviar correos electrónicos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Envía un email de forma asíncrona
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent email to: {}, subject: {}", to, subject);
            log.debug("Email body: {}", body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Error sending email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Envía un email a múltiples destinatarios de forma asíncrona
     */
    @Async
    public void sendEmailToMultipleRecipients(String[] recipients, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email sending is disabled. Would have sent email to: {}, subject: {}",
                    String.join(", ", recipients), subject);
            log.debug("Email body: {}", body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(recipients);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", String.join(", ", recipients));
        } catch (Exception e) {
            log.error("Error sending email to: {}", String.join(", ", recipients), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Envía un email de confirmación de orden
     */
    @Async
    public void sendOrderConfirmationEmail(String to, String orderNumber, String orderDetails) {
        String subject = "Confirmación de Orden - " + orderNumber;
        String body = buildOrderConfirmationBody(orderNumber, orderDetails);

        // Si hay múltiples destinatarios separados por coma
        if (to.contains(",")) {
            String[] recipients = to.split(",");
            // Limpiar espacios en blanco
            for (int i = 0; i < recipients.length; i++) {
                recipients[i] = recipients[i].trim();
            }
            sendEmailToMultipleRecipients(recipients, subject, body);
        } else {
            sendEmail(to, subject, body);
        }
    }

    /**
     * Construye el cuerpo del email de confirmación de orden
     */
    private String buildOrderConfirmationBody(String orderNumber, String orderDetails) {
        return String.format("""
                Estimado cliente,
                
                Su orden ha sido creada exitosamente.
                
                Número de Orden: %s
                
                Detalles de la Orden:
                %s
                
                Gracias por su compra.
                
                Saludos,
                Equipo de LINKTIC
                
                ---
                Este es un correo automático, por favor no responder.
                """, orderNumber, orderDetails);
    }
}

