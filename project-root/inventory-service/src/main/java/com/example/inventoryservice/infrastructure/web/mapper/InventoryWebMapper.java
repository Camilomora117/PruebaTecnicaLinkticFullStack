package com.example.inventoryservice.infrastructure.web.mapper;

import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.InventoryWithProduct;
import com.example.inventoryservice.infrastructure.web.dto.InventoryRequest;
import com.example.inventoryservice.infrastructure.web.dto.InventoryResponse;
import com.example.inventoryservice.infrastructure.web.dto.InventoryResponseBasic;

/**
 * Mapper for converting between domain models and DTOs for inventory operations.
 */
public class InventoryWebMapper {

    public static Inventory toDomain(InventoryRequest request, Long productId) {
        if (request == null) {
            return null;
        }
        return new Inventory(productId, request.getQuantity());
    }

    public static InventoryResponse toResponse(InventoryWithProduct inventory) {
        if (inventory == null) {
            return null;
        }
        return new InventoryResponse(
            inventory.getProduct().getId(), 
            inventory.getInventory().getQuantity(), 
            inventory.getProduct().getName(), 
            inventory.getProduct().getPrice(), 
            inventory.getProduct().getDescription()
        );
    }

    public static InventoryResponseBasic toResponse(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return new InventoryResponseBasic(
            inventory.getProductId(), 
            inventory.getQuantity()
        );
    }
}

