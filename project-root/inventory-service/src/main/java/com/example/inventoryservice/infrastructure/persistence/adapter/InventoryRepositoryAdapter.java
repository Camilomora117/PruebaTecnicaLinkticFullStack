package com.example.inventoryservice.infrastructure.persistence.adapter;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.port.out.InventoryRepositoryPort;
import com.example.inventoryservice.infrastructure.persistence.entity.InventoryEntity;
import com.example.inventoryservice.infrastructure.persistence.mapper.InventoryMapper;
import com.example.inventoryservice.infrastructure.persistence.repository.InventoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryAdapter implements InventoryRepositoryPort {
    
    private final InventoryJpaRepository inventoryJpaRepository;
    private final InventoryMapper inventoryMapper;
    
    @Override
    public Optional<Inventory> findByProductId(Long productId) {
        return inventoryJpaRepository.findByProductId(productId)
                .map(inventoryMapper::toDomain);
    }
    
    @Override
    public Inventory save(Inventory inventory) {
        InventoryEntity entity = inventoryMapper.toEntity(inventory);
        InventoryEntity savedEntity = inventoryJpaRepository.save(entity);
        return inventoryMapper.toDomain(savedEntity);
    }
    
    @Override
    public Inventory setQuantity(Long productId, int quantity) {
        inventoryJpaRepository.setQuantity(productId, quantity);

        return inventoryJpaRepository.findByProductId(productId)
                .map(inventoryMapper::toDomain)
                .orElseThrow(() -> new IllegalStateException("Failed to set quantity for product: " + productId));
    }
}
