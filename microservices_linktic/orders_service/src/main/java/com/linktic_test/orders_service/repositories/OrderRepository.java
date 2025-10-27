package com.linktic_test.orders_service.repositories;

import com.linktic_test.orders_service.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Order
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Busca una orden por su número de orden
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Verifica si existe una orden con el número especificado
     */
    boolean existsByOrderNumber(String orderNumber);
}
