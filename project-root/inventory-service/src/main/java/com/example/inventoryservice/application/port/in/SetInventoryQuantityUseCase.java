package com.example.inventoryservice.application.port.in;

import com.example.inventoryservice.domain.model.Inventory;

public interface SetInventoryQuantityUseCase {
    Inventory setInventoryQuantity(Long productId, int quantity);
}
