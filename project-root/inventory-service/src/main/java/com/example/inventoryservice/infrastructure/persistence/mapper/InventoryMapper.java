package com.example.inventoryservice.infrastructure.persistence.mapper;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Inventory domain model and InventoryEntity persistence entity.
 */
@Component
public class InventoryMapper {

    public Inventory toDomain(InventoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return Inventory.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public InventoryEntity toEntity(Inventory domain) {
        if (domain == null) {
            return null;
        }

        return InventoryEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .quantity(domain.getQuantity())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
