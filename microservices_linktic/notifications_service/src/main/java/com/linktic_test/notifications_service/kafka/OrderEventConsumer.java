package com.linktic_test.notifications_service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.notifications_service.model.dto.OrderEventDTO;
import com.linktic_test.notifications_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer de Kafka para escuchar eventos de órdenes
 */
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    /**
     * Escucha eventos del topic order-events
     */
    @KafkaListener(
        topics = "${app.kafka.topic.order-events}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeOrderEvent(String message) {
        log.info("Received order event from Kafka: {}", message);

        try {
            // Parsear el mensaje JSON a OrderEventDTO
            OrderEventDTO orderEvent = objectMapper.readValue(message, OrderEventDTO.class);
            
            log.debug("Parsed order event: {}", orderEvent);
            
            // Procesar el evento y enviar notificación
            notificationService.processOrderEvent(orderEvent);
            
            log.info("Successfully processed order event for order: {}", orderEvent.getOrderNumber());
            
        } catch (Exception e) {
            log.error("Error processing order event from Kafka: {}", message, e);
            // En producción, aquí podrías enviar el mensaje a un Dead Letter Queue (DLQ)
        }
    }
}

