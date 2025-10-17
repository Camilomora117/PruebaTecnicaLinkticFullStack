package com.example.inventoryservice.infrastructure.web.mapper;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.infrastructure.web.dto.InventoryResponseBasic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Web Mapper Tests")
class InventoryWebMapperTest {

    @Test
    @DisplayName("Should map inventory to response correctly")
    void shouldMapInventoryToResponseCorrectly() {
        // Given
        Instant now = Instant.now();
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        // When
        InventoryResponseBasic response = InventoryWebMapper.toResponse(inventory);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getProductId());
        assertEquals(50, response.getQuantity());
    }

    @Test
    @DisplayName("Should map inventory with zero quantity")
    void shouldMapInventoryWithZeroQuantity() {
        // Given
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(0)
                .updatedAt(Instant.now())
                .build();

        // When
        InventoryResponseBasic response = InventoryWebMapper.toResponse(inventory);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getQuantity());
    }

    @Test
    @DisplayName("Should map inventory with large quantity")
    void shouldMapInventoryWithLargeQuantity() {
        // Given
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(999999)
                .updatedAt(Instant.now())
                .build();

        // When
        InventoryResponseBasic response = InventoryWebMapper.toResponse(inventory);

        // Then
        assertNotNull(response);
        assertEquals(999999, response.getQuantity());
    }

    @Test
    @DisplayName("Should preserve timestamp when mapping")
    void shouldPreserveTimestampWhenMapping() {
        // Given
        Instant specificTime = Instant.parse("2024-01-15T10:30:45Z");
        Inventory inventory = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(specificTime)
                .build();

        // When
        InventoryResponseBasic response = InventoryWebMapper.toResponse(inventory);

    }
}
