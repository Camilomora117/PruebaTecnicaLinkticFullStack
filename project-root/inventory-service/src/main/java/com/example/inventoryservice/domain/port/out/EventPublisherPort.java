package com.example.inventoryservice.domain.port.out;

public interface EventPublisherPort {
    
    void publishInventoryChanged(Long productId, int newQuantity);
}

