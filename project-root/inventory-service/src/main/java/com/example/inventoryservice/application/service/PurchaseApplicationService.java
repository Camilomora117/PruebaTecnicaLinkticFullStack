package com.example.inventoryservice.application.service;

import com.example.inventoryservice.application.port.in.ProcessPurchaseUseCase;
import com.example.inventoryservice.domain.exception.InsufficientStockException;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.domain.port.out.EventPublisherPort;
import com.example.inventoryservice.domain.port.out.InventoryRepositoryPort;
import com.example.inventoryservice.domain.port.out.PurchaseRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseApplicationService implements ProcessPurchaseUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final PurchaseRepositoryPort purchaseRepositoryPort;
    private final EventPublisherPort eventPublisherPort;

    @Override
    @Transactional
    public Purchase processPurchase(Long productId, int requestedQuantity) {
        log.info("Processing purchase for product ID: {} with quantity: {}", productId, requestedQuantity);

        validateRequest(productId, requestedQuantity);

        // Check inventory
        Inventory inventory = inventoryRepositoryPort.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        // Update inventory - reduce stock
        int newQuantity = inventory.getQuantity() - requestedQuantity;
        if (newQuantity < 0) {
            throw new InsufficientStockException("Product without inventory");
        }
        inventory.setQuantity(newQuantity);
        inventoryRepositoryPort.save(inventory);
        log.info("Updated inventory for product ID: {} - New quantity: {}", productId, newQuantity);

        // Create and save purchase record
        Purchase purchase = new Purchase(productId, requestedQuantity);
        Purchase newPurchase = purchaseRepositoryPort.save(purchase);

        // Publish inventory changed event
        publishInventoryChanged(productId, newQuantity);

        return newPurchase;
    }

    private void validateRequest(Long productId, int requestedQuantity) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be greater than 0");
        }
    }

    private void publishInventoryChanged(Long productId, int newQuantity) {
        try {
            eventPublisherPort.publishInventoryChanged(productId, newQuantity);
            log.info("Published inventory changed event for product ID: {}", productId);
        } catch (Exception e) {
            log.error("Failed to publish inventory changed event for product ID: {}", productId, e);
        }
    }
}