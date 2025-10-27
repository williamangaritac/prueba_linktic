package com.linktic_test.products_service.repositories;

import com.linktic_test.products_service.model.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by SKU
     * @param sku the product SKU
     * @return Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Check if a product exists by SKU
     * @param sku the product SKU
     * @return true if product exists, false otherwise
     */
    boolean existsBySku(String sku);

    /**
     * Find products by status with pagination
     * @param status the product status
     * @param pageable pagination information
     * @return Page of products
     */
    Page<Product> findByStatus(Boolean status, Pageable pageable);

    /**
     * Find products by name containing (case insensitive) with pagination
     * @param name the product name to search
     * @param pageable pagination information
     * @return Page of products
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Find active products (status = true) with pagination
     * @param pageable pagination information
     * @return Page of active products
     */
    @Query("SELECT p FROM Product p WHERE p.status = true")
    Page<Product> findActiveProducts(Pageable pageable);

    /**
     * Find all products without pagination
     * @return List of all products
     */
    @Query("SELECT p FROM Product p ORDER BY p.id")
    List<Product> findAllProducts();

    /**
     * Find all active products without pagination
     * @return List of all active products
     */
    @Query("SELECT p FROM Product p WHERE p.status = true ORDER BY p.id")
    List<Product> findAllActiveProducts();
}
