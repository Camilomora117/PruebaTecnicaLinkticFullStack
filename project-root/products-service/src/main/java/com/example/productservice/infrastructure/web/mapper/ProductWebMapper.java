package com.example.productservice.infrastructure.web.mapper;

import com.example.productservice.domain.model.Product;
import com.example.productservice.infrastructure.web.dto.ProductRequest;
import com.example.productservice.infrastructure.web.dto.ProductResponse;

public class ProductWebMapper {

    public static Product toDomain(ProductRequest request) {
        if (request == null) {
            return null;
        }
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
    }

    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
}


