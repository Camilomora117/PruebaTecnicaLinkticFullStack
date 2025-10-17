package com.example.productservice.domain.builder;

import com.example.productservice.domain.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductBuilderTest {

    @Test
    void build_ShouldReturnProduct_WhenValidData() {
        // Given
        String name = "Test Product";
        Double price = 99.99;
        String description = "Test Description";

        // When
        Product product = ProductBuilder.builder()
                .name(name)
                .price(price)
                .description(description)
                .build();

        // Then
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertEquals(description, product.getDescription());
    }

    @Test
    void build_ShouldThrowException_WhenNameIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> ProductBuilder.builder()
                        .name(null)
                        .price(99.99)
                        .build());
    }

    @Test
    void build_ShouldThrowException_WhenNameIsEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> ProductBuilder.builder()
                        .name("")
                        .price(99.99)
                        .build());
    }

    @Test
    void build_ShouldThrowException_WhenPriceIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> ProductBuilder.builder()
                        .name("Test Product")
                        .price(null)
                        .build());
    }

    @Test
    void build_ShouldThrowException_WhenPriceIsZero() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> ProductBuilder.builder()
                        .name("Test Product")
                        .price(0.0)
                        .build());
    }

    @Test
    void build_ShouldThrowException_WhenPriceIsNegative() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> ProductBuilder.builder()
                        .name("Test Product")
                        .price(-1.0)
                        .build());
    }

    @Test
    void build_ShouldReturnProduct_WhenDescriptionIsNull() {
        // Given
        String name = "Test Product";
        Double price = 99.99;

        // When
        Product product = ProductBuilder.builder()
                .name(name)
                .price(price)
                .description(null)
                .build();

        // Then
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(price, product.getPrice());
        assertNull(product.getDescription());
    }
}


