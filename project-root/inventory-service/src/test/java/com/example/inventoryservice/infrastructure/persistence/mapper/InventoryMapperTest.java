package com.example.inventoryservice.infrastructure.persistence.mapper;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.infrastructure.persistence.entity.InventoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Mapper Tests")
class InventoryMapperTest {

    private InventoryMapper inventoryMapper;

    @BeforeEach
    void setUp() {
        inventoryMapper = new InventoryMapper();
    }

    @Test
    @DisplayName("Should map domain to entity correctly")
    void shouldMapDomainToEntityCorrectly() {
        // Given
        Instant now = Instant.now();
        Inventory domain = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        // When
        InventoryEntity entity = inventoryMapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(100L, entity.getProductId());
        assertEquals(50, entity.getQuantity());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map entity to domain correctly")
    void shouldMapEntityToDomainCorrectly() {
        // Given
        Instant now = Instant.now();
        InventoryEntity entity = new InventoryEntity();
        entity.setId(1L);
        entity.setProductId(100L);
        entity.setQuantity(50);
        entity.setUpdatedAt(now);

        // When
        Inventory domain = inventoryMapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertEquals(1L, domain.getId());
        assertEquals(100L, domain.getProductId());
        assertEquals(50, domain.getQuantity());
        assertEquals(now, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map domain with null ID to entity")
    void shouldMapDomainWithNullIdToEntity() {
        // Given
        Inventory domain = Inventory.builder()
                .productId(100L)
                .quantity(50)
                .updatedAt(Instant.now())
                .build();

        // When
        InventoryEntity entity = inventoryMapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals(100L, entity.getProductId());
        assertEquals(50, entity.getQuantity());
    }

    @Test
    @DisplayName("Should map entity with null ID to domain")
    void shouldMapEntityWithNullIdToDomain() {
        // Given
        InventoryEntity entity = new InventoryEntity();
        entity.setProductId(100L);
        entity.setQuantity(50);
        entity.setUpdatedAt(Instant.now());

        // When
        Inventory domain = inventoryMapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertNull(domain.getId());
        assertEquals(100L, domain.getProductId());
    }

    @Test
    @DisplayName("Should map domain with zero quantity")
    void shouldMapDomainWithZeroQuantity() {
        // Given
        Inventory domain = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(0)
                .updatedAt(Instant.now())
                .build();

        // When
        InventoryEntity entity = inventoryMapper.toEntity(domain);

        // Then
        assertNotNull(entity);
        assertEquals(0, entity.getQuantity());
    }

    @Test
    @DisplayName("Should map entity with large quantity")
    void shouldMapEntityWithLargeQuantity() {
        // Given
        InventoryEntity entity = new InventoryEntity();
        entity.setId(1L);
        entity.setProductId(100L);
        entity.setQuantity(999999);
        entity.setUpdatedAt(Instant.now());

        // When
        Inventory domain = inventoryMapper.toDomain(entity);

        // Then
        assertNotNull(domain);
        assertEquals(999999, domain.getQuantity());
    }

    @Test
    @DisplayName("Should preserve timestamp precision when mapping")
    void shouldPreserveTimestampPrecisionWhenMapping() {
        // Given
        Instant preciseTime = Instant.parse("2024-01-15T10:30:45.123456789Z");
        Inventory domain = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(preciseTime)
                .build();

        // When
        InventoryEntity entity = inventoryMapper.toEntity(domain);
        Inventory mappedBack = inventoryMapper.toDomain(entity);

        // Then
        assertEquals(preciseTime, entity.getUpdatedAt());
        assertEquals(preciseTime, mappedBack.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle bidirectional mapping correctly")
    void shouldHandleBidirectionalMappingCorrectly() {
        // Given
        Instant now = Instant.now();
        Inventory originalDomain = Inventory.builder()
                .id(1L)
                .productId(100L)
                .quantity(50)
                .updatedAt(now)
                .build();

        // When
        InventoryEntity entity = inventoryMapper.toEntity(originalDomain);
        Inventory mappedBackDomain = inventoryMapper.toDomain(entity);

        // Then
        assertEquals(originalDomain.getId(), mappedBackDomain.getId());
        assertEquals(originalDomain.getProductId(), mappedBackDomain.getProductId());
        assertEquals(originalDomain.getQuantity(), mappedBackDomain.getQuantity());
        assertEquals(originalDomain.getUpdatedAt(), mappedBackDomain.getUpdatedAt());
    }
}
