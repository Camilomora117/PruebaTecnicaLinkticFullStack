package com.example.productservice.infrastructure.persistence.mapper;

import com.example.productservice.domain.model.Product;
import com.example.productservice.infrastructure.persistence.entity.ProductEntity;

public class ProductMapper {
    
    public static ProductEntity toEntity(Product product) {
        if (product == null) {
            return null;
        }
        
        return ProductEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
    
    public static Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .build();
    }
}

