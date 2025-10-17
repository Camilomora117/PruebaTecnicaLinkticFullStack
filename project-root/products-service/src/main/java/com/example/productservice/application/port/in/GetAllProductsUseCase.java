package com.example.productservice.application.port.in;

import com.example.productservice.domain.model.Product;

import java.util.List;

public interface GetAllProductsUseCase {
    List<Product> getAll();
}


