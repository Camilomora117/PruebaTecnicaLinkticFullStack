package com.example.productservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    @DisplayName("Validación exitosa para producto válido")
    void validateSuccess() {
        Product product = Product.builder()
                .name("Valid Name")
                .price(10.0)
                .description("desc")
                .build();
        assertDoesNotThrow(product::validate);
    }

    @Test
    @DisplayName("Lanza excepción si el nombre es nulo o vacío")
    void validateNameRequired() {
        Product product = Product.builder().price(10.0).description("desc").build();
        Exception ex = assertThrows(IllegalArgumentException.class, product::validate);
        assertTrue(ex.getMessage().contains("name"));
    }

    @Test
    @DisplayName("Lanza excepción si el precio es nulo o <= 0")
    void validatePriceRequired() {
        Product product = Product.builder().name("Name").description("desc").build();
        Exception ex = assertThrows(IllegalArgumentException.class, product::validate);
        assertTrue(ex.getMessage().contains("price"));
    }

    @Test
    @DisplayName("Lanza excepción si el nombre es demasiado largo")
    void validateNameTooLong() {
        String longName = "a".repeat(101);
        Product product = Product.builder().name(longName).price(10.0).description("desc").build();
        Exception ex = assertThrows(IllegalArgumentException.class, product::validate);
        assertTrue(ex.getMessage().contains("exceed 100"));
    }

    @Test
    @DisplayName("Lanza excepción si la descripción es demasiado larga")
    void validateDescriptionTooLong() {
        String longDesc = "a".repeat(501);
        Product product = Product.builder().name("Name").price(10.0).description(longDesc).build();
        Exception ex = assertThrows(IllegalArgumentException.class, product::validate);
        assertTrue(ex.getMessage().contains("exceed 500"));
    }
}
