package com.example.inventoryservice.application.service;

import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.InventoryWithProduct;
import com.example.inventoryservice.domain.model.Product;
import com.example.inventoryservice.domain.port.out.InventoryRepositoryPort;
import com.example.inventoryservice.domain.port.out.ProductCatalogPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Inventory Application Service Tests")
class InventoryApplicationServiceTest {

    @Mock
    private InventoryRepositoryPort inventoryRepositoryPort;

    @Mock
    private ProductCatalogPort productCatalogPort;

    @InjectMocks
    private InventoryApplicationService inventoryApplicationService;

    private Inventory testInventory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testInventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .updatedAt(Instant.now())
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(99.99)
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("Should get inventory by product ID successfully")
    void shouldGetInventoryByProductIdSuccessfully() {
        // Given
        Long productId = 1L;
        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(productCatalogPort.getById(productId))
                .thenReturn(testProduct);

        // When
        InventoryWithProduct result = inventoryApplicationService.getInventoryByProductId(productId);

        // Then
        assertNotNull(result);
        assertNotNull(result.getInventory());
        assertNotNull(result.getProduct());
        assertEquals(productId, result.getInventory().getProductId());
        assertEquals(100, result.getInventory().getQuantity());
        assertEquals("Test Product", result.getProduct().getName());

        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(productCatalogPort, times(1)).getById(productId);
    }

    @Test
    @DisplayName("Should return zero inventory when inventory not found")
    void shouldReturnZeroInventoryWhenNotFound() {
        // Given
        Long productId = 1L;
        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.empty());
        when(productCatalogPort.getById(productId))
                .thenReturn(testProduct);

        // When
        InventoryWithProduct result = inventoryApplicationService.getInventoryByProductId(productId);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getInventory().getQuantity());
        assertEquals(productId, result.getInventory().getProductId());
        assertNotNull(result.getProduct());

        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(productCatalogPort, times(1)).getById(productId);
    }

    @Test
    @DisplayName("Should throw exception when product ID is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryApplicationService.getInventoryByProductId(null)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
        verify(inventoryRepositoryPort, never()).findByProductId(anyLong());
        verify(productCatalogPort, never()).getById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when product ID is zero")
    void shouldThrowExceptionWhenProductIdIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryApplicationService.getInventoryByProductId(0L)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when product ID is negative")
    void shouldThrowExceptionWhenProductIdIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryApplicationService.getInventoryByProductId(-1L)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product catalog fails")
    void shouldThrowProductNotFoundExceptionWhenProductCatalogFails() {
        // Given
        Long productId = 1L;
        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(productCatalogPort.getById(productId))
                .thenThrow(new RuntimeException("Product service unavailable"));

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> inventoryApplicationService.getInventoryByProductId(productId)
        );

        assertTrue(exception.getMessage().contains("Failed to fetch product information"));
        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(productCatalogPort, times(1)).getById(productId);
    }

    @Test
    @DisplayName("Should set inventory quantity successfully")
    void shouldSetInventoryQuantitySuccessfully() {
        // Given
        Long productId = 1L;
        int newQuantity = 50;
        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(newQuantity)
                .updatedAt(Instant.now())
                .build();

        when(productCatalogPort.getById(productId)).thenReturn(testProduct);
        when(inventoryRepositoryPort.setQuantity(productId, newQuantity))
                .thenReturn(updatedInventory);

        // When
        Inventory result = inventoryApplicationService.setInventoryQuantity(productId, newQuantity);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(newQuantity, result.getQuantity());

        verify(productCatalogPort, times(1)).getById(productId);
        verify(inventoryRepositoryPort, times(1)).setQuantity(productId, newQuantity);
    }

    @Test
    @DisplayName("Should throw exception when setting negative quantity")
    void shouldThrowExceptionWhenSettingNegativeQuantity() {
        // Given
        Long productId = 1L;
        int negativeQuantity = -10;
        when(productCatalogPort.getById(productId)).thenReturn(testProduct);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryApplicationService.setInventoryQuantity(productId, negativeQuantity)
        );

        assertEquals("Quantity cannot be negative", exception.getMessage());
        verify(productCatalogPort, times(1)).getById(productId);
        verify(inventoryRepositoryPort, never()).setQuantity(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should throw exception when product does not exist")
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // Given
        Long productId = 999L;
        int quantity = 50;
        when(productCatalogPort.getById(productId))
                .thenThrow(new RuntimeException("Product not found"));

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> inventoryApplicationService.setInventoryQuantity(productId, quantity)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(productCatalogPort, times(1)).getById(productId);
        verify(inventoryRepositoryPort, never()).setQuantity(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should set inventory quantity to zero")
    void shouldSetInventoryQuantityToZero() {
        // Given
        Long productId = 1L;
        int zeroQuantity = 0;
        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(zeroQuantity)
                .updatedAt(Instant.now())
                .build();

        when(productCatalogPort.getById(productId)).thenReturn(testProduct);
        when(inventoryRepositoryPort.setQuantity(productId, zeroQuantity))
                .thenReturn(updatedInventory);

        // When
        Inventory result = inventoryApplicationService.setInventoryQuantity(productId, zeroQuantity);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        verify(inventoryRepositoryPort, times(1)).setQuantity(productId, zeroQuantity);
    }
}
