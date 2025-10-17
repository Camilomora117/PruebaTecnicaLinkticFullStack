package com.example.inventoryservice.application.service;

import com.example.inventoryservice.domain.exception.InsufficientStockException;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.domain.port.out.EventPublisherPort;
import com.example.inventoryservice.domain.port.out.InventoryRepositoryPort;
import com.example.inventoryservice.domain.port.out.PurchaseRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Purchase Application Service Tests")
class PurchaseApplicationServiceTest {

    @Mock
    private InventoryRepositoryPort inventoryRepositoryPort;

    @Mock
    private PurchaseRepositoryPort purchaseRepositoryPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @InjectMocks
    private PurchaseApplicationService purchaseApplicationService;

    private Inventory testInventory;
    private Purchase testPurchase;

    @BeforeEach
    void setUp() {
        testInventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .updatedAt(Instant.now())
                .build();

        testPurchase = Purchase.builder()
                .id(1L)
                .productId(1L)
                .quantity(5)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should process purchase successfully")
    void shouldProcessPurchaseSuccessfully() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 5;

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(purchaseRepositoryPort.save(any(Purchase.class)))
                .thenReturn(testPurchase);
        doNothing().when(eventPublisherPort).publishInventoryChanged(anyLong(), anyInt());

        // When
        Purchase result = purchaseApplicationService.processPurchase(productId, requestedQuantity);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(requestedQuantity, result.getQuantity());

        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(purchaseRepositoryPort, times(1)).save(any(Purchase.class));
        verify(eventPublisherPort, times(1)).publishInventoryChanged(productId, 95);
    }

    @Test
    @DisplayName("Should throw exception when product ID is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseApplicationService.processPurchase(null, 5)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
        verify(inventoryRepositoryPort, never()).findByProductId(anyLong());
        verify(purchaseRepositoryPort, never()).save(any(Purchase.class));
    }

    @Test
    @DisplayName("Should throw exception when product ID is zero")
    void shouldThrowExceptionWhenProductIdIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseApplicationService.processPurchase(0L, 5)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when product ID is negative")
    void shouldThrowExceptionWhenProductIdIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseApplicationService.processPurchase(-1L, 5)
        );

        assertEquals("Product ID must be a positive number", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when requested quantity is zero")
    void shouldThrowExceptionWhenRequestedQuantityIsZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseApplicationService.processPurchase(1L, 0)
        );

        assertEquals("Requested quantity must be greater than 0", exception.getMessage());
        verify(inventoryRepositoryPort, never()).findByProductId(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when requested quantity is negative")
    void shouldThrowExceptionWhenRequestedQuantityIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> purchaseApplicationService.processPurchase(1L, -5)
        );

        assertEquals("Requested quantity must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when inventory not found")
    void shouldThrowProductNotFoundExceptionWhenInventoryNotFound() {
        // Given
        Long productId = 999L;
        int requestedQuantity = 5;

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.empty());

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> purchaseApplicationService.processPurchase(productId, requestedQuantity)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(purchaseRepositoryPort, never()).save(any(Purchase.class));
        verify(eventPublisherPort, never()).publishInventoryChanged(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is insufficient")
    void shouldThrowInsufficientStockExceptionWhenStockInsufficient() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 150; // More than available (100)

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> purchaseApplicationService.processPurchase(productId, requestedQuantity)
        );

        assertEquals("Product without inventory", exception.getMessage());
        verify(inventoryRepositoryPort, times(1)).findByProductId(productId);
        verify(purchaseRepositoryPort, never()).save(any(Purchase.class));
        verify(eventPublisherPort, never()).publishInventoryChanged(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should process purchase with minimum valid quantity")
    void shouldProcessPurchaseWithMinimumValidQuantity() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 1;

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(purchaseRepositoryPort.save(any(Purchase.class)))
                .thenReturn(testPurchase);
        doNothing().when(eventPublisherPort).publishInventoryChanged(anyLong(), anyInt());

        // When
        Purchase result = purchaseApplicationService.processPurchase(productId, requestedQuantity);

        // Then
        assertNotNull(result);
        verify(purchaseRepositoryPort, times(1)).save(any(Purchase.class));
        verify(eventPublisherPort, times(1)).publishInventoryChanged(productId, 99);
    }

    @Test
    @DisplayName("Should process purchase with maximum valid quantity")
    void shouldProcessPurchaseWithMaximumValidQuantity() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 99; // Leaves 1 in stock

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(purchaseRepositoryPort.save(any(Purchase.class)))
                .thenReturn(testPurchase);
        doNothing().when(eventPublisherPort).publishInventoryChanged(anyLong(), anyInt());

        // When
        Purchase result = purchaseApplicationService.processPurchase(productId, requestedQuantity);

        // Then
        assertNotNull(result);
        verify(purchaseRepositoryPort, times(1)).save(any(Purchase.class));
        verify(eventPublisherPort, times(1)).publishInventoryChanged(productId, 1);
    }

    @Test
    @DisplayName("Should handle event publisher failure gracefully")
    void shouldHandleEventPublisherFailureGracefully() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 5;

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(purchaseRepositoryPort.save(any(Purchase.class)))
                .thenReturn(testPurchase);
        doThrow(new RuntimeException("Event publisher error"))
                .when(eventPublisherPort).publishInventoryChanged(anyLong(), anyInt());

        // When
        Purchase result = purchaseApplicationService.processPurchase(productId, requestedQuantity);

        // Then
        assertNotNull(result);
        verify(eventPublisherPort, times(1)).publishInventoryChanged(productId, 95);
        // Should not throw exception, just log it
    }

    @Test
    @DisplayName("Should create purchase with correct timestamp")
    void shouldCreatePurchaseWithCorrectTimestamp() {
        // Given
        Long productId = 1L;
        int requestedQuantity = 5;
        Instant beforePurchase = Instant.now();

        when(inventoryRepositoryPort.findByProductId(productId))
                .thenReturn(Optional.of(testInventory));
        when(purchaseRepositoryPort.save(any(Purchase.class)))
                .thenAnswer(invocation -> {
                    Purchase purchase = invocation.getArgument(0);
                    assertNotNull(purchase.getCreatedAt());
                    assertTrue(purchase.getCreatedAt().isAfter(beforePurchase.minusSeconds(1)));
                    return testPurchase;
                });

        // When
        purchaseApplicationService.processPurchase(productId, requestedQuantity);

        // Then
        verify(purchaseRepositoryPort, times(1)).save(any(Purchase.class));
    }
}
