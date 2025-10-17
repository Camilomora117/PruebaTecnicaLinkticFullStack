package com.example.productservice.infrastructure.web.controller;

import com.example.productservice.application.service.ProductApplicationService;
import com.example.productservice.domain.model.Product;
import com.example.productservice.infrastructure.web.dto.ProductRequest;
import com.example.productservice.infrastructure.web.dto.ProductResponse;
import com.example.productservice.infrastructure.web.mapper.ProductWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductApplicationService productApplicationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .description("Description")
                .build();
        productRequest = ProductRequest.builder()
                .name("Test Product")
                .price(100.0)
                .description("Description")
                .build();
        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .description("Description")
                .build();
    }

    @Test
    @DisplayName("POST /products - Crear producto")
    void createProduct() throws Exception {
        when(productApplicationService.create(any(Product.class))).thenReturn(product);
        mockMvc.perform(post("/products")
                        .header("X-API-KEY", "products-api-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)));
    }

    @Test
    @DisplayName("GET /products/{id} - Obtener producto por id")
    void getProductById() throws Exception {
        when(productApplicationService.getById(eq(1L))).thenReturn(Optional.of(product));
        mockMvc.perform(get("/products/1")
                        .header("X-API-KEY", "products-api-key-123"));
    }

    @Test
    @DisplayName("GET /products - Listar productos")
    void getAllProducts() throws Exception {
        when(productApplicationService.getAll()).thenReturn(List.of(product));
        mockMvc.perform(get("/products")
                        .header("X-API-KEY", "products-api-key-123"));
    }

    @Test
    @DisplayName("PUT /products/{id} - Actualizar producto")
    void updateProduct() throws Exception {
        when(productApplicationService.update(eq(1L), any(Product.class))).thenReturn(Optional.of(product));
        mockMvc.perform(put("/products/1")
                        .header("X-API-KEY", "products-api-key-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)));
    }

    @Test
    @DisplayName("DELETE /products/{id} - Eliminar producto")
    void deleteProduct() throws Exception {
        when(productApplicationService.delete(eq(1L))).thenReturn(true);
        mockMvc.perform(delete("/products/1")
                        .header("X-API-KEY", "products-api-key-123"));
    }
}
