package com.example.inventoryservice.domain.port.out;

import com.example.inventoryservice.domain.model.Inventory;

import java.util.Optional;

public interface InventoryRepositoryPort {

    Optional<Inventory> findByProductId(Long productId);

    Inventory save(Inventory inventory);

    Inventory setQuantity(Long productId, int quantity);
}

