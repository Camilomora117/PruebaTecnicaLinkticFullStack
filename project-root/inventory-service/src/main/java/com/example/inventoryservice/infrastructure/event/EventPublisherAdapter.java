package com.example.inventoryservice.infrastructure.event;

import com.example.inventoryservice.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisherAdapter implements EventPublisherPort {
    
    @Override
    public void publishInventoryChanged(Long productId, int newQuantity) {
        // For now, just log the event. In a real implementation, this would publish to a message broker
        log.info("Inventory changed event: productId={}, newQuantity={}", productId, newQuantity);
        // TODO: Implement actual event publishing to message
    }
}

