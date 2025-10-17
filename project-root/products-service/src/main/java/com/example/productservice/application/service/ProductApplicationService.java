package com.example.productservice.application.service;
import com.example.productservice.application.port.in.*;
import com.example.productservice.domain.model.Product;
import com.example.productservice.domain.port.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductApplicationService implements
        CreateProductUseCase,
        GetProductByIdUseCase,
        GetAllProductsUseCase,
        UpdateProductUseCase,
        DeleteProductUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    @Override
    public Product create(Product product) {
        product.validate();
        return productRepositoryPort.save(product);
    }

    @Override
    public Optional<Product> getById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }
        return productRepositoryPort.findById(id);
    }

    @Override
    public List<Product> getAll() {
        return productRepositoryPort.findAll();
    }

    @Override
    public Optional<Product> update(Long id, Product product) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }
        return productRepositoryPort.findById(id)
                .map(existing -> {
                    product.validate();
                    Product updatedProduct = Product.builder()
                            .id(id)
                            .name(product.getName())
                            .price(product.getPrice())
                            .description(product.getDescription())
                            .build();
                    return productRepositoryPort.save(updatedProduct);
                });
    }

    @Override
    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID must be a positive number");
        }
        if (!productRepositoryPort.existsById(id)) {
            return false;
        }
        productRepositoryPort.deleteById(id);
        return true;
    }
}

