package com.example.inventoryservice.infrastructure.persistence.mapper;

import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.infrastructure.persistence.entity.PurchaseEntity;
import org.springframework.stereotype.Component;

@Component
public class PurchaseMapper {

    public Purchase toDomain(PurchaseEntity entity) {
        if (entity == null) {
            return null;
        }

        return Purchase.builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public PurchaseEntity toEntity(Purchase domain) {
        if (domain == null) {
            return null;
        }

        return PurchaseEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .quantity(domain.getQuantity())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}