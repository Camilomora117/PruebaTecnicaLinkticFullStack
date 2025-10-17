package com.example.productservice.infrastructure.web.mapper;

import com.example.productservice.domain.model.Product;
import com.example.productservice.infrastructure.web.dto.ProductRequest;
import com.example.productservice.infrastructure.web.dto.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductWebMapperTest {
    @Test
    @DisplayName("Mapea ProductRequest a Product")
    void toDomain() {
        ProductRequest request = ProductRequest.builder()
                .name("Test Product")
                .price(50.0)
                .description("Description")
                .build();
        Product product = ProductWebMapper.toDomain(request);
        assertNotNull(product);
        assertEquals("Test Product", product.getName());
        assertEquals(50.0, product.getPrice());
        assertEquals("Description", product.getDescription());
    }

    @Test
    @DisplayName("Mapea Product a ProductResponse")
    void toResponse() {
        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(50.0)
                .description("Description")
                .build();
        ProductResponse response = ProductWebMapper.toResponse(product);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals(50.0, response.getPrice());
        assertEquals("Description", response.getDescription());
    }
}
