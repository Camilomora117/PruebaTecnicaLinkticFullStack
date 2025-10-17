package com.example.inventoryservice.application.service;

import com.example.inventoryservice.application.port.in.GetInventoryByProductIdUseCase;
import com.example.inventoryservice.application.port.in.SetInventoryQuantityUseCase;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.InventoryWithProduct;
import com.example.inventoryservice.domain.model.Product;
import com.example.inventoryservice.domain.port.out.InventoryRepositoryPort;
import com.example.inventoryservice.domain.port.out.ProductCatalogPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryApplicationService implements
        GetInventoryByProductIdUseCase,
        SetInventoryQuantityUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final ProductCatalogPort productCatalogPort;

    @Override
    public InventoryWithProduct getInventoryByProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }

        Inventory inventory = inventoryRepositoryPort.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .quantity(0)
                        .build());

        // Get product information
        Product product;
        try {
            product = productCatalogPort.getById(productId);
        } catch (Exception e) {
            log.error("Failed to fetch product with ID: {}", productId, e);
            throw new ProductNotFoundException("Failed to fetch product information for ID: " + productId, e);
        }

        return new InventoryWithProduct(inventory, product);
    }

    @Override
    @Transactional
    public Inventory setInventoryQuantity(Long productId, int quantity) {
        validateProductExists(productId);

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        return inventoryRepositoryPort.setQuantity(productId, quantity);
    }
    
    private void validateProductExists(Long productId) {
        try {
            productCatalogPort.getById(productId);
        } catch (Exception e) {
            throw new ProductNotFoundException("Product not found with ID: " + productId, e);
        }
    }
}
