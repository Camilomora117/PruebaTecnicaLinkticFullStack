package com.example.inventoryservice.application.port.in;

import com.example.inventoryservice.domain.model.InventoryWithProduct;

public interface  GetInventoryByProductIdUseCase {
    InventoryWithProduct getInventoryByProductId(Long productId);
}
