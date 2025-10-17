package com.example.inventoryservice.domain.port.out;

import com.example.inventoryservice.domain.model.Product;

public interface ProductCatalogPort {
    
    Product getById(Long productId);
}

