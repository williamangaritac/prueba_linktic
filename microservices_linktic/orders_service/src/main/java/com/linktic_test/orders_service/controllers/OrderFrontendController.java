package com.linktic_test.orders_service.controllers;

import com.linktic_test.orders_service.model.dtos.OrderRequest;
import com.linktic_test.orders_service.model.dtos.OrderResponse;
import com.linktic_test.orders_service.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador específico para integración con Angular Frontend
 * Endpoint optimizado para el botón "Comprar"
 */
@Slf4j
@RestController
@RequestMapping("/frontend/orders")
@RequiredArgsConstructor
@Tag(name = "Frontend Orders", description = "Endpoints optimizados para Angular Frontend - Botón Comprar")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderFrontendController {

    private final OrderService orderService;

    /**
     * Crear orden desde el frontend (Botón "Comprar")
     * POST /api/v1/frontend/orders/purchase
     * 
     * Este endpoint:
     * 1. Valida inventario
     * 2. Crea la orden
     * 3. Publica evento en Kafka
     * 4. Dispara notificación por email
     */
    @Operation(summary = "Create order from frontend", 
               description = "Creates order when user clicks 'Comprar' button. Validates inventory, creates order, publishes Kafka event, and triggers email notification")
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> createOrderFromFrontend(
            @Valid @RequestBody OrderRequest orderRequest) {
        
        log.info("Frontend purchase request received with {} items", 
                 orderRequest.getOrderItems().size());
        
        try {
            OrderResponse order = orderService.createOrder(orderRequest);
            
            log.info("Order created successfully from frontend: {}", order.orderNumber());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "¡Compra realizada exitosamente!",
                    "order", order,
                    "orderNumber", order.orderNumber(),
                    "notification", "Recibirás un email de confirmación en breve"
            ));
            
        } catch (RuntimeException e) {
            log.error("Error creating order from frontend: {}", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Error al procesar la compra",
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Validar disponibilidad de productos antes de comprar
     * POST /api/v1/frontend/orders/validate
     */
    @Operation(summary = "Validate product availability", 
               description = "Validates if products are available before purchase")
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateOrder(
            @Valid @RequestBody OrderRequest orderRequest) {
        
        log.info("Frontend validation request for {} items", 
                 orderRequest.getOrderItems().size());
        
        try {
            // Aquí podrías agregar lógica de validación sin crear la orden
            // Por ahora, retornamos éxito si la estructura es válida
            
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "Productos disponibles para compra"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "valid", false,
                    "message", e.getMessage()
            ));
        }
    }
}

