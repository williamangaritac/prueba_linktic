package com.linktic_test.products_service.services;

import com.linktic_test.products_service.model.dtos.ProductRequest;
import com.linktic_test.products_service.model.dtos.ProductResponse;
import com.linktic_test.products_service.model.entities.Product;
import com.linktic_test.products_service.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequest testProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setSku("TEST001");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStatus(true);

        testProductRequest = new ProductRequest();
        testProductRequest.setSku("TEST001");
        testProductRequest.setName("Test Product");
        testProductRequest.setDescription("Test Description");
        testProductRequest.setPrice(new BigDecimal("99.99"));
        testProductRequest.setStatus(true);
    }

    @Test
    void createProduct_Success() {
        // Arrange
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponse response = productService.createProduct(testProductRequest);

        // Assert
        assertNotNull(response);
        assertEquals("TEST001", response.getSku());
        assertEquals("Test Product", response.getName());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        verify(productRepository, times(1)).existsBySku("TEST001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_SkuAlreadyExists_ThrowsException() {
        // Arrange
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(testProductRequest);
        });
        
        assertTrue(exception.getMessage().contains("already exists"));
        verify(productRepository, times(1)).existsBySku("TEST001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TEST001", response.getSku());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(1L);
        });
        
        assertTrue(exception.getMessage().contains("not found"));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductBySku_Success() {
        // Arrange
        when(productRepository.findBySku("TEST001")).thenReturn(Optional.of(testProduct));

        // Act
        ProductResponse response = productService.getProductBySku("TEST001");

        // Assert
        assertNotNull(response);
        assertEquals("TEST001", response.getSku());
        verify(productRepository, times(1)).findBySku("TEST001");
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setSku("TEST001");
        updateRequest.setName("Updated Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("149.99"));
        updateRequest.setStatus(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductResponse response = productService.updateProduct(1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void getAllProducts_WithPagination_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> response = productService.getAllProducts(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("TEST001", response.getContent().get(0).getSku());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllProductsWithoutPagination_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllProducts()).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getAllProductsWithoutPagination();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("TEST001", response.get(0).getSku());
        verify(productRepository, times(1)).findAllProducts();
    }

    @Test
    void getAllActiveProductsWithoutPagination_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllActiveProducts()).thenReturn(products);

        // Act
        List<ProductResponse> response = productService.getAllActiveProductsWithoutPagination();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).getStatus());
        verify(productRepository, times(1)).findAllActiveProducts();
    }

    @Test
    void getActiveProducts_WithPagination_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products);
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findByStatusTrue(pageable)).thenReturn(productPage);

        // Act
        Page<ProductResponse> response = productService.getActiveProducts(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertTrue(response.getContent().get(0).getStatus());
        verify(productRepository, times(1)).findByStatusTrue(pageable);
    }
}

