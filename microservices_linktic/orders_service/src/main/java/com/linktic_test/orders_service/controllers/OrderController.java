package com.linktic_test.orders_service.controllers;

import com.linktic_test.orders_service.model.dtos.BaseResponse;
import com.linktic_test.orders_service.model.dtos.OrderRequest;
import com.linktic_test.orders_service.model.dtos.OrderResponse;
import com.linktic_test.orders_service.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de órdenes
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Orders", description = "API para gestión de órdenes")
public class OrderController {

    private final OrderService orderService;

    /**
     * Health check del servicio
     */
    @GetMapping("/status")
    @Operation(summary = "Health check", description = "Verificar el estado del servicio de órdenes")
    @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "orders-service");
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        log.debug("Health check requested");
        return ResponseEntity.ok(response);
    }

    /**
     * Crear una nueva orden
     */
    @PostMapping
    @Operation(summary = "Crear orden", description = "Crear una nueva orden en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

        log.info("Creating order");

        try {
            OrderResponse orderResponse = orderService.createOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(new String[]{e.getMessage()}));
        }
    }

    /**
     * Obtener orden por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID", description = "Obtener los detalles de una orden específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "ID de la orden") @PathVariable Long id) {
        
        log.debug("Getting order by ID: {}", id);

        return orderService.getOrderById(id)
                .map(order -> ResponseEntity.ok().body(order))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener orden por número de orden
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Obtener orden por número", description = "Obtener los detalles de una orden por su número")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    public ResponseEntity<?> getOrderByOrderNumber(
            @Parameter(description = "Número de la orden") @PathVariable String orderNumber) {
        
        log.debug("Getting order by order number: {}", orderNumber);

        return orderService.getOrderByOrderNumber(orderNumber)
                .map(order -> ResponseEntity.ok().body(order))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener todas las órdenes
     */
    @GetMapping
    @Operation(summary = "Listar órdenes", description = "Obtener lista de todas las órdenes")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes obtenida exitosamente")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        log.debug("Getting all orders");

        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
