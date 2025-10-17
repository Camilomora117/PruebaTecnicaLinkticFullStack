package com.example.inventoryservice.infrastructure.persistence.adapter;

import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.domain.port.out.PurchaseRepositoryPort;
import com.example.inventoryservice.infrastructure.persistence.entity.PurchaseEntity;
import com.example.inventoryservice.infrastructure.persistence.mapper.PurchaseMapper;
import com.example.inventoryservice.infrastructure.persistence.repository.PurchaseJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchasePersistenceAdapter implements PurchaseRepositoryPort {

    private final PurchaseJpaRepository purchaseJpaRepository;
    private final PurchaseMapper purchaseMapper;

    @Override
    public Purchase save(Purchase purchase) {
        PurchaseEntity entity = purchaseMapper.toEntity(purchase);
        PurchaseEntity savedEntity = purchaseJpaRepository.save(entity);
        return purchaseMapper.toDomain(savedEntity);
    }
}
