package com.linktic_test.products_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linktic_test.products_service.model.dtos.ProductRequest;
import com.linktic_test.products_service.model.entities.Product;
import com.linktic_test.products_service.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        productRepository.deleteAll();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setSku("TEST001");
        request.setName("Test Product");
        request.setDescription("Test Description");
        request.setPrice(new BigDecimal("99.99"));
        request.setStatus(true);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("TEST001"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void getProductById_ShouldReturnProduct() throws Exception {
        Product product = new Product();
        product.setSku("TEST002");
        product.setName("Test Product 2");
        product.setDescription("Test Description 2");
        product.setPrice(new BigDecimal("149.99"));
        product.setStatus(true);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.sku").value("TEST002"))
                .andExpect(jsonPath("$.name").value("Test Product 2"));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() throws Exception {
        Product product = new Product();
        product.setSku("TEST003");
        product.setName("Test Product 3");
        product.setDescription("Test Description 3");
        product.setPrice(new BigDecimal("199.99"));
        product.setStatus(true);
        Product savedProduct = productRepository.save(product);

        ProductRequest updateRequest = new ProductRequest();
        updateRequest.setSku("TEST003_UPDATED");
        updateRequest.setName("Updated Test Product");
        updateRequest.setDescription("Updated Description");
        updateRequest.setPrice(new BigDecimal("299.99"));
        updateRequest.setStatus(false);

        mockMvc.perform(put("/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("TEST003_UPDATED"))
                .andExpect(jsonPath("$.name").value("Updated Test Product"))
                .andExpect(jsonPath("$.price").value(299.99))
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent() throws Exception {
        Product product = new Product();
        product.setSku("TEST004");
        product.setName("Test Product 4");
        product.setDescription("Test Description 4");
        product.setPrice(new BigDecimal("99.99"));
        product.setStatus(true);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(delete("/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllProducts_ShouldReturnPagedProducts() throws Exception {
        // Create test products
        for (int i = 1; i <= 5; i++) {
            Product product = new Product();
            product.setSku("TEST00" + i);
            product.setName("Test Product " + i);
            product.setDescription("Test Description " + i);
            product.setPrice(new BigDecimal("99.99"));
            product.setStatus(true);
            productRepository.save(product);
        }

        mockMvc.perform(get("/products")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
}
