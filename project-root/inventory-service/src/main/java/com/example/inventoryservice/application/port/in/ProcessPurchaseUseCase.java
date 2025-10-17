package com.example.inventoryservice.application.port.in;

import com.example.inventoryservice.domain.model.Purchase;

public interface ProcessPurchaseUseCase {
    Purchase processPurchase(Long productId, int requestedQuantity);
}
