package com.example.inventoryservice.infrastructure.persistence.repository;

import com.example.inventoryservice.infrastructure.persistence.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE InventoryEntity i SET i.quantity = :quantity, i.updatedAt = CURRENT_TIMESTAMP WHERE i.productId = :productId")
    void setQuantity(@Param("productId") Long productId, @Param("quantity") int quantity);
}

