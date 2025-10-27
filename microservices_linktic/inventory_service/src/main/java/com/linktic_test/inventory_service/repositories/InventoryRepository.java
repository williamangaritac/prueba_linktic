package com.linktic_test.inventory_service.repositories;

import com.linktic_test.inventory_service.model.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Inventory entity
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Find inventory by SKU
     * @param sku the product SKU
     * @return Optional containing the inventory if found
     */
    Optional<Inventory> findBySku(String sku);

    /**
     * Check if inventory exists by SKU
     * @param sku the product SKU
     * @return true if inventory exists, false otherwise
     */
    boolean existsBySku(String sku);

    /**
     * Update quantity for a specific SKU
     * @param sku the product SKU
     * @param quantity the new quantity
     * @return number of affected rows
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = :quantity WHERE i.sku = :sku")
    int updateQuantityBySku(@Param("sku") String sku, @Param("quantity") Long quantity);

    /**
     * Decrease quantity for a specific SKU
     * @param sku the product SKU
     * @param quantity the quantity to decrease
     * @return number of affected rows
     */
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :quantity WHERE i.sku = :sku AND i.quantity >= :quantity")
    int decreaseQuantityBySku(@Param("sku") String sku, @Param("quantity") Long quantity);
}
