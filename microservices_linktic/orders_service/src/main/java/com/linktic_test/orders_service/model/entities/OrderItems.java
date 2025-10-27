package com.linktic_test.orders_service.model.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa los items de una orden
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;

    @Column(name = "product_name")
    private String productName;

    private Double price;

    private Long quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal subtotal;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
