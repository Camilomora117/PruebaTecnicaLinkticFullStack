package com.example.productservice.application.port.in;

import com.example.productservice.domain.model.Product;

import java.util.Optional;

public interface GetProductByIdUseCase {
    Optional<Product> getById(Long id);
}


