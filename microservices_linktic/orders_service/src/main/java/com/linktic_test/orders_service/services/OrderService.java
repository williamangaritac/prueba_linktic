package com.linktic_test.orders_service.services;

import com.linktic_test.orders_service.events.OrderEvent;
import com.linktic_test.orders_service.model.dtos.*;
import com.linktic_test.orders_service.model.entities.Order;
import com.linktic_test.orders_service.model.entities.OrderItems;
import com.linktic_test.orders_service.model.enums.OrderStatus;
import com.linktic_test.orders_service.repositories.OrderRepository;
import com.linktic_test.orders_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de órdenes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Qualifier("inventoryWebClient")
    private final WebClient inventoryWebClient;

    @Value("${app.kafka.topic.order-events}")
    private String orderEventsTopic;

    /**
     * Crear una nueva orden
     */
    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating new order with {} items", orderRequest.getOrderItems().size());

        // 1. Actualizar inventario ANTES de crear la orden
        try {
            updateInventoryAfterPurchase(orderRequest.getOrderItems());
            log.info("Inventory updated successfully for all items");
        } catch (Exception e) {
            log.error("Failed to update inventory: {}", e.getMessage());
            throw new RuntimeException("No se pudo actualizar el inventario: " + e.getMessage());
        }

        // 2. Calcular el monto total
        double totalAmountDouble = orderRequest.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        java.math.BigDecimal totalAmount = java.math.BigDecimal.valueOf(totalAmountDouble);

        // 3. Crear la orden
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .totalAmount(totalAmount)
                .status("PENDING")
                .customerEmail(orderRequest.getCustomerEmail())
                .build();

        // 4. Crear los items de la orden
        List<OrderItems> orderItems = orderRequest.getOrderItems().stream()
                .map(itemRequest -> {
                    // Calcular subtotal = precio * cantidad
                    java.math.BigDecimal subtotal = java.math.BigDecimal.valueOf(itemRequest.getPrice())
                            .multiply(java.math.BigDecimal.valueOf(itemRequest.getQuantity()));

                    return OrderItems.builder()
                            .sku(itemRequest.getSku())
                            .price(itemRequest.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .subtotal(subtotal)
                            .order(order)
                            .build();
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // 5. Guardar la orden
        Order savedOrder = orderRepository.save(order);

        // 6. Emitir evento
        emitOrderEvent(savedOrder);

        log.info("Order created successfully with number: {}", savedOrder.getOrderNumber());
        return mapToOrderResponse(savedOrder);
    }

    /**
     * Obtener orden por ID
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long id) {
        log.debug("Getting order by ID: {}", id);
        return orderRepository.findById(id)
                .map(this::mapToOrderResponse);
    }

    /**
     * Obtener orden por número de orden
     */
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByOrderNumber(String orderNumber) {
        log.debug("Getting order by order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::mapToOrderResponse);
    }

    /**
     * Obtener todas las órdenes
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Getting all orders");

        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }



    /**
     * Generar número de orden único
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uuid;
    }



    /**
     * Emitir evento de orden
     */
    private void emitOrderEvent(Order order) {
        try {
            // Construir lista de items del evento
            List<OrderEvent.OrderEventItem> eventItems = order.getOrderItems().stream()
                    .map(item -> new OrderEvent.OrderEventItem(
                            item.getSku(),
                            null, // productName - se puede obtener del Products Service si es necesario
                            item.getQuantity().intValue(), // Convertir Long a Integer
                            item.getPrice()
                    ))
                    .collect(Collectors.toList());

            // Calcular monto total
            double totalAmount = order.getOrderItems().stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();

            // Crear evento completo
            OrderEvent event = new OrderEvent(
                    order.getId(),
                    order.getOrderNumber(),
                    eventItems,
                    totalAmount,
                    "ORDER_CREATED",
                    OrderStatus.PLACED
            );

            String eventJson = JsonUtils.toJson(event);

            kafkaTemplate.send(orderEventsTopic, order.getOrderNumber(), eventJson);

            log.info("Order event emitted to Kafka topic '{}': {}", orderEventsTopic, eventJson);
        } catch (Exception e) {
            log.error("Error emitting order event: {}", e.getMessage(), e);
        }
    }

    /**
     * Mapear entidad Order a OrderResponse
     */
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemsResponse> itemsResponse = order.getOrderItems().stream()
                .map(item -> new OrderItemsResponse(
                        item.getId(),
                        item.getSku(),
                        item.getPrice(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                itemsResponse
        );
    }

    /**
     * Actualizar inventario después de una compra
     * Llama al Inventory Service para decrementar el stock
     */
    private void updateInventoryAfterPurchase(List<OrderItemRequest> orderItems) {
        log.info("Calling Inventory Service to update stock for {} items", orderItems.size());

        try {
            // Llamar al endpoint POST /inventory/purchase del Inventory Service
            inventoryWebClient.post()
                    .uri("/inventory/purchase")
                    .bodyValue(orderItems)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block(); // Bloquear hasta que se complete la llamada

            log.info("Inventory Service updated successfully");
        } catch (Exception e) {
            log.error("Error calling Inventory Service: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar el inventario: " + e.getMessage());
        }
    }
}
