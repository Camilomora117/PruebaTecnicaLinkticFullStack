package com.example.inventoryservice.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Purchase Domain Model Tests")
class PurchaseTest {

    @Test
    @DisplayName("Should create purchase with builder")
    void shouldCreatePurchaseWithBuilder() {
        // Given
        Instant now = Instant.now();

        // When
        Purchase purchase = Purchase.builder()
                .id(1L)
                .productId(100L)
                .quantity(5)
                .createdAt(now)
                .build();

        // Then
        assertNotNull(purchase);
        assertEquals(1L, purchase.getId());
        assertEquals(100L, purchase.getProductId());
        assertEquals(5, purchase.getQuantity());
        assertEquals(now, purchase.getCreatedAt());
    }

    @Test
    @DisplayName("Should create purchase with constructor")
    void shouldCreatePurchaseWithConstructor() {
        // When
        Purchase purchase = new Purchase(100L, 5);

        // Then
        assertNotNull(purchase);
        assertEquals(100L, purchase.getProductId());
        assertEquals(5, purchase.getQuantity());
        assertNotNull(purchase.getCreatedAt());
    }

    @Test
    @DisplayName("Should create purchase with minimum quantity")
    void shouldCreatePurchaseWithMinimumQuantity() {
        // When
        Purchase purchase = new Purchase(100L, 1);

        // Then
        assertEquals(1, purchase.getQuantity());
    }

    @Test
    @DisplayName("Should create purchase with large quantity")
    void shouldCreatePurchaseWithLargeQuantity() {
        // When
        Purchase purchase = Purchase.builder()
                .productId(100L)
                .quantity(1000)
                .createdAt(Instant.now())
                .build();

        // Then
        assertEquals(1000, purchase.getQuantity());
    }

    @Test
    @DisplayName("Should have timestamp set automatically in constructor")
    void shouldHaveTimestampSetAutomaticallyInConstructor() {
        // Given
        Instant before = Instant.now();

        // When
        Purchase purchase = new Purchase(100L, 5);
        Instant after = Instant.now();

        // Then
        assertNotNull(purchase.getCreatedAt());
        assertTrue(purchase.getCreatedAt().isAfter(before.minusSeconds(1)));
        assertTrue(purchase.getCreatedAt().isBefore(after.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should support equality comparison")
    void shouldSupportEqualityComparison() {
        // Given
        Instant now = Instant.now();
        Purchase purchase1 = Purchase.builder()
                .id(1L)
                .productId(100L)
                .quantity(5)
                .createdAt(now)
                .build();

        Purchase purchase2 = Purchase.builder()
                .id(1L)
                .productId(100L)
                .quantity(5)
                .createdAt(now)
                .build();

        // Then
        assertEquals(purchase1, purchase2);
        assertEquals(purchase1.hashCode(), purchase2.hashCode());
    }

    @Test
    @DisplayName("Should create purchase without ID for new records")
    void shouldCreatePurchaseWithoutIdForNewRecords() {
        // When
        Purchase purchase = new Purchase(100L, 5);

        // Then
        assertNull(purchase.getId());
        assertEquals(100L, purchase.getProductId());
    }

    @Test
    @DisplayName("Should allow updating quantity")
    void shouldAllowUpdatingQuantity() {
        // Given
        Purchase purchase = Purchase.builder()
                .id(1L)
                .productId(100L)
                .quantity(5)
                .createdAt(Instant.now())
                .build();

        // When
        purchase.setQuantity(10);

        // Then
        assertEquals(10, purchase.getQuantity());
    }

    @Test
    @DisplayName("Should preserve timestamp immutability")
    void shouldPreserveTimestampImmutability() {
        // Given
        Instant originalTime = Instant.parse("2024-01-15T10:30:45Z");
        Purchase purchase = Purchase.builder()
                .id(1L)
                .productId(100L)
                .quantity(5)
                .createdAt(originalTime)
                .build();

        // When
        Instant retrievedTime = purchase.getCreatedAt();

        // Then
        assertEquals(originalTime, retrievedTime);
    }
}
