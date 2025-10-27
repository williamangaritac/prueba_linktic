package com.linktic_test.products_service.services;

import com.linktic_test.products_service.model.dtos.ProductRequest;
import com.linktic_test.products_service.model.dtos.ProductResponse;
import com.linktic_test.products_service.model.entities.Product;
import com.linktic_test.products_service.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Product operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product
     * @param productRequest the product data
     * @return ProductResponse with created product data
     * @throws RuntimeException if SKU already exists
     */
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating product with SKU: {}", productRequest.getSku());
        
        // Check if SKU already exists
        if (productRepository.existsBySku(productRequest.getSku())) {
            throw new RuntimeException("Product with SKU " + productRequest.getSku() + " already exists");
        }

        Product product = mapToEntity(productRequest);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getId());
        return mapToResponse(savedProduct);
    }

    /**
     * Get product by ID
     * @param id the product ID
     * @return ProductResponse with product data
     * @throws RuntimeException if product not found
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        return mapToResponse(product);
    }

    /**
     * Get product by SKU
     * @param sku the product SKU
     * @return ProductResponse with product data
     * @throws RuntimeException if product not found
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        log.info("Fetching product with SKU: {}", sku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));

        return mapToResponse(product);
    }

    /**
     * Update product by ID
     * @param id the product ID
     * @param productRequest the updated product data
     * @return ProductResponse with updated product data
     * @throws RuntimeException if product not found or SKU conflict
     */
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Check if SKU is being changed and if new SKU already exists
        if (!existingProduct.getSku().equals(productRequest.getSku()) && 
            productRepository.existsBySku(productRequest.getSku())) {
            throw new RuntimeException("Product with SKU " + productRequest.getSku() + " already exists");
        }

        // Update product fields
        existingProduct.setSku(productRequest.getSku());
        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStatus(productRequest.getStatus());

        Product updatedProduct = productRepository.save(existingProduct);
        
        log.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return mapToResponse(updatedProduct);
    }

    /**
     * Delete product by ID
     * @param id the product ID
     * @throws RuntimeException if product not found
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }

    /**
     * Get all products with pagination
     * @param pageable pagination information
     * @return Page of ProductResponse
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        log.info("Fetching all products with pagination: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get active products with pagination
     * @param pageable pagination information
     * @return Page of ProductResponse for active products
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        log.info("Fetching active products with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> products = productRepository.findActiveProducts(pageable);
        return products.map(this::mapToResponse);
    }

    /**
     * Get all products without pagination
     * @return List of ProductResponse for all products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductsWithoutPagination() {
        log.info("Fetching all products without pagination");

        List<Product> products = productRepository.findAllProducts();
        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get all active products without pagination
     * @return List of ProductResponse for all active products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProductsWithoutPagination() {
        log.info("Fetching all active products without pagination");

        List<Product> products = productRepository.findAllActiveProducts();
        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Map ProductRequest to Product entity
     */
    private Product mapToEntity(ProductRequest productRequest) {
        Product product = new Product();
        product.setSku(productRequest.getSku());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStatus(productRequest.getStatus());
        return product;
    }

    /**
     * Map Product entity to ProductResponse
     */
    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStatus(product.getStatus());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
