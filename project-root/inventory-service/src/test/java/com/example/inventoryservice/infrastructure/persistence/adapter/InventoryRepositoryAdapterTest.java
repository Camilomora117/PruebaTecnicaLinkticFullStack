package com.example.inventoryservice.infrastructure.persistence.adapter;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.infrastructure.persistence.entity.InventoryEntity;
import com.example.inventoryservice.infrastructure.persistence.mapper.InventoryMapper;
import com.example.inventoryservice.infrastructure.persistence.repository.InventoryJpaRepository;
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
@DisplayName("Inventory Repository Adapter Tests")
class InventoryRepositoryAdapterTest {

    @Mock
    private InventoryJpaRepository inventoryJpaRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryRepositoryAdapter inventoryRepositoryAdapter;

    private Inventory testInventory;
    private InventoryEntity testInventoryEntity;

    @BeforeEach
    void setUp() {
        testInventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .updatedAt(Instant.now())
                .build();

        testInventoryEntity = new InventoryEntity();
        testInventoryEntity.setId(1L);
        testInventoryEntity.setProductId(1L);
        testInventoryEntity.setQuantity(100);
        testInventoryEntity.setUpdatedAt(Instant.now());
    }

    @Test
    @DisplayName("Should find inventory by product ID successfully")
    void shouldFindInventoryByProductIdSuccessfully() {
        // Given
        Long productId = 1L;
        when(inventoryJpaRepository.findByProductId(productId))
                .thenReturn(Optional.of(testInventoryEntity));
        when(inventoryMapper.toDomain(testInventoryEntity))
                .thenReturn(testInventory);

        // When
        Optional<Inventory> result = inventoryRepositoryAdapter.findByProductId(productId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(productId, result.get().getProductId());
        assertEquals(100, result.get().getQuantity());

        verify(inventoryJpaRepository, times(1)).findByProductId(productId);
        verify(inventoryMapper, times(1)).toDomain(testInventoryEntity);
    }

    @Test
    @DisplayName("Should return empty when inventory not found")
    void shouldReturnEmptyWhenInventoryNotFound() {
        // Given
        Long productId = 999L;
        when(inventoryJpaRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        // When
        Optional<Inventory> result = inventoryRepositoryAdapter.findByProductId(productId);

        // Then
        assertFalse(result.isPresent());
        verify(inventoryJpaRepository, times(1)).findByProductId(productId);
        verify(inventoryMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Should save inventory successfully")
    void shouldSaveInventorySuccessfully() {
        // Given
        when(inventoryMapper.toEntity(testInventory))
                .thenReturn(testInventoryEntity);
        when(inventoryJpaRepository.save(testInventoryEntity))
                .thenReturn(testInventoryEntity);
        when(inventoryMapper.toDomain(testInventoryEntity))
                .thenReturn(testInventory);

        // When
        Inventory result = inventoryRepositoryAdapter.save(testInventory);

        // Then
        assertNotNull(result);
        assertEquals(testInventory.getProductId(), result.getProductId());
        assertEquals(testInventory.getQuantity(), result.getQuantity());

        verify(inventoryMapper, times(1)).toEntity(testInventory);
        verify(inventoryJpaRepository, times(1)).save(testInventoryEntity);
        verify(inventoryMapper, times(1)).toDomain(testInventoryEntity);
    }

    @Test
    @DisplayName("Should set quantity successfully")
    void shouldSetQuantitySuccessfully() {
        // Given
        Long productId = 1L;
        int newQuantity = 50;

        InventoryEntity updatedEntity = new InventoryEntity();
        updatedEntity.setId(1L);
        updatedEntity.setProductId(productId);
        updatedEntity.setQuantity(newQuantity);
        updatedEntity.setUpdatedAt(Instant.now());

        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(newQuantity)
                .updatedAt(Instant.now())
                .build();

        doNothing().when(inventoryJpaRepository).setQuantity(productId, newQuantity);
        when(inventoryJpaRepository.findByProductId(productId))
                .thenReturn(Optional.of(updatedEntity));
        when(inventoryMapper.toDomain(updatedEntity))
                .thenReturn(updatedInventory);

        // When
        Inventory result = inventoryRepositoryAdapter.setQuantity(productId, newQuantity);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(newQuantity, result.getQuantity());

        verify(inventoryJpaRepository, times(1)).setQuantity(productId, newQuantity);
        verify(inventoryJpaRepository, times(1)).findByProductId(productId);
        verify(inventoryMapper, times(1)).toDomain(updatedEntity);
    }

    @Test
    @DisplayName("Should throw exception when set quantity fails")
    void shouldThrowExceptionWhenSetQuantityFails() {
        // Given
        Long productId = 1L;
        int newQuantity = 50;

        doNothing().when(inventoryJpaRepository).setQuantity(productId, newQuantity);
        when(inventoryJpaRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> inventoryRepositoryAdapter.setQuantity(productId, newQuantity)
        );

        assertTrue(exception.getMessage().contains("Failed to set quantity"));
        verify(inventoryJpaRepository, times(1)).setQuantity(productId, newQuantity);
        verify(inventoryJpaRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("Should handle null inventory when saving")
    void shouldHandleNullInventoryWhenSaving() {
        // Given
        when(inventoryMapper.toEntity(null))
                .thenThrow(new NullPointerException("Inventory cannot be null"));

        // When & Then
        assertThrows(
                NullPointerException.class,
                () -> inventoryRepositoryAdapter.save(null)
        );

        verify(inventoryMapper, times(1)).toEntity(null);
        verify(inventoryJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should set quantity to zero")
    void shouldSetQuantityToZero() {
        // Given
        Long productId = 1L;
        int zeroQuantity = 0;

        InventoryEntity updatedEntity = new InventoryEntity();
        updatedEntity.setId(1L);
        updatedEntity.setProductId(productId);
        updatedEntity.setQuantity(zeroQuantity);
        updatedEntity.setUpdatedAt(Instant.now());

        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(zeroQuantity)
                .updatedAt(Instant.now())
                .build();

        doNothing().when(inventoryJpaRepository).setQuantity(productId, zeroQuantity);
        when(inventoryJpaRepository.findByProductId(productId))
                .thenReturn(Optional.of(updatedEntity));
        when(inventoryMapper.toDomain(updatedEntity))
                .thenReturn(updatedInventory);

        // When
        Inventory result = inventoryRepositoryAdapter.setQuantity(productId, zeroQuantity);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getQuantity());
        verify(inventoryJpaRepository, times(1)).setQuantity(productId, zeroQuantity);
    }

    @Test
    @DisplayName("Should save new inventory with null ID")
    void shouldSaveNewInventoryWithNullId() {
        // Given
        Inventory newInventory = Inventory.builder()
                .productId(2L)
                .quantity(50)
                .updatedAt(Instant.now())
                .build();

        InventoryEntity newEntity = new InventoryEntity();
        newEntity.setProductId(2L);
        newEntity.setQuantity(50);
        newEntity.setUpdatedAt(Instant.now());

        InventoryEntity savedEntity = new InventoryEntity();
        savedEntity.setId(2L);
        savedEntity.setProductId(2L);
        savedEntity.setQuantity(50);
        savedEntity.setUpdatedAt(Instant.now());

        Inventory savedInventory = Inventory.builder()
                .id(2L)
                .productId(2L)
                .quantity(50)
                .updatedAt(Instant.now())
                .build();

        when(inventoryMapper.toEntity(newInventory)).thenReturn(newEntity);
        when(inventoryJpaRepository.save(newEntity)).thenReturn(savedEntity);
        when(inventoryMapper.toDomain(savedEntity)).thenReturn(savedInventory);

        // When
        Inventory result = inventoryRepositoryAdapter.save(newInventory);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(2L, result.getId());
        verify(inventoryJpaRepository, times(1)).save(newEntity);
    }
}
