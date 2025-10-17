package com.example.inventoryservice.infrastructure.persistence.repository;

import com.example.inventoryservice.infrastructure.persistence.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseJpaRepository extends JpaRepository<PurchaseEntity, Long>  {
}
