package com.example.inventoryservice.infrastructure.web.mapper;

import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.infrastructure.web.dto.PurchaseRequest;
import com.example.inventoryservice.infrastructure.web.dto.PurchaseResponse;

/**
 * Mapper for converting between domain models and DTOs for purchase operations.
 */
public class PurchaseWebMapper {

    public static Purchase toDomain(PurchaseRequest request) {
        if (request == null) {
            return null;
        }
        return new Purchase(request.getProductId(), request.getQuantity());
    }

    public static PurchaseResponse toResponse(Purchase purchase,String message) {
        if (purchase == null) {
            return null;
        }
        return new PurchaseResponse(message, purchase.getQuantity());
    }
}

