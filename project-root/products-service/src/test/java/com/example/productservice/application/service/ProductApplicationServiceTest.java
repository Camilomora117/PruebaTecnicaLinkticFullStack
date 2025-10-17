package com.example.productservice.application.service;

import com.example.productservice.domain.model.Product;
import com.example.productservice.domain.port.out.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProductApplicationServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(100.0)
                .description("Description")
                .build();
    }

    @Test
    @DisplayName("Crear producto válido")
    void createProduct() {
        when(productRepositoryPort.save(any(Product.class))).thenReturn(product);
        Product created = productApplicationService.create(product);
        assertNotNull(created);
        assertEquals("Test Product", created.getName());
    }

    @Test
    @DisplayName("Obtener producto por ID")
    void getById() {
        when(productRepositoryPort.findById(1L)).thenReturn(Optional.of(product));
        Optional<Product> found = productApplicationService.getById(1L);
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
    }

    @Test
    @DisplayName("Obtener todos los productos")
    void getAll() {
        when(productRepositoryPort.findAll()).thenReturn(List.of(product));
        List<Product> products = productApplicationService.getAll();
        assertFalse(products.isEmpty());
    }

    @Test
    @DisplayName("Actualizar producto existente")
    void updateProduct() {
        when(productRepositoryPort.findById(1L)).thenReturn(Optional.of(product));
        when(productRepositoryPort.save(any(Product.class))).thenReturn(product);
        Optional<Product> updated = productApplicationService.update(1L, product);
        assertTrue(updated.isPresent());
        assertEquals("Test Product", updated.get().getName());
    }

    @Test
    @DisplayName("Eliminar producto existente")
    void deleteProduct() {
        when(productRepositoryPort.existsById(1L)).thenReturn(true);
        doNothing().when(productRepositoryPort).deleteById(1L);
        boolean deleted = productApplicationService.delete(1L);
        assertTrue(deleted);
    }

    @Test
    @DisplayName("Eliminar producto inexistente")
    void deleteNonExistingProduct() {
        when(productRepositoryPort.existsById(2L)).thenReturn(false);
        boolean deleted = productApplicationService.delete(2L);
        assertFalse(deleted);
    }
}
