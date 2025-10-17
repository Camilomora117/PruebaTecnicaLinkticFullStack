package com.example.inventoryservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Domain Model Tests")
class InventoryTest {

    @Test
    @DisplayName("Should create inventory with builder")
    void shouldCreateInventoryWithBuilder() {
        // Given
        Instant now = Instant.now();

        // When
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        // Then
        assertNotNull(inventory);
        assertEquals(1L, inventory.getId());
        assertEquals(100L, inventory.getProductId());
        assertEquals(50, inventory.getQuantity());
        assertEquals(now, inventory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create inventory with constructor")
    void shouldCreateInventoryWithConstructor() {
        // When
        Inventory inventory = new Inventory(100L, 50);

        // Then
        assertNotNull(inventory);
        assertEquals(100L, inventory.getProductId());
        assertEquals(50, inventory.getQuantity());
        assertNotNull(inventory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create inventory with zero quantity")
    void shouldCreateInventoryWithZeroQuantity() {
        // When
        Inventory inventory = Inventory.builder()
                .productId(100L)
                .quantity(0)
                .updatedAt(Instant.now())
                .build();

        // Then
        assertEquals(0, inventory.getQuantity());
    }

    @Test
    @DisplayName("Should update inventory quantity")
    void shouldUpdateInventoryQuantity() {
        // Given
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(Instant.now())
                .build();

        // When
        inventory.setQuantity(75);

        // Then
        assertEquals(75, inventory.getQuantity());
    }

    @Test
    @DisplayName("Should update timestamp")
    void shouldUpdateTimestamp() {
        // Given
        Instant oldTime = Instant.now().minusSeconds(3600);
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(oldTime)
                .build();

        // When
        Instant newTime = Instant.now();
        inventory.setUpdatedAt(newTime);

        // Then
        assertEquals(newTime, inventory.getUpdatedAt());
        assertNotEquals(oldTime, inventory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should support equality comparison")
    void shouldSupportEqualityComparison() {
        // Given
        Instant now = Instant.now();
        Inventory inventory1 = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        Inventory inventory2 = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        // Then
        assertEquals(inventory1, inventory2);
        assertEquals(inventory1.hashCode(), inventory2.hashCode());
    }

    @Test
    @DisplayName("Should handle large quantities")
    void shouldHandleLargeQuantities() {
        // When
        Inventory inventory = Inventory.builder()
                .productId(100L)
                .quantity(Integer.MAX_VALUE)
                .updatedAt(Instant.now())
                .build();

        // Then
        assertEquals(Integer.MAX_VALUE, inventory.getQuantity());
    }

    @Test
    @DisplayName("Should create inventory without ID for new records")
    void shouldCreateInventoryWithoutIdForNewRecords() {
        // When
        Inventory inventory = new Inventory(100L, 50);

        // Then
        assertNull(inventory.getId());
        assertEquals(100L, inventory.getProductId());
    }
}
