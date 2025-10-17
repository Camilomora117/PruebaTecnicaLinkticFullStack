package com.example.productservice.application.port.in;

import com.example.productservice.domain.model.Product;

public interface CreateProductUseCase {
    Product create(Product product);
}


