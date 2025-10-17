package com.example.inventoryservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryWithProduct {
    private final Inventory inventory;
    private final Product product;
}
