package com.example.inventoryservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Instant createdAt;
    
    public Purchase(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = Instant.now();
    }
}
