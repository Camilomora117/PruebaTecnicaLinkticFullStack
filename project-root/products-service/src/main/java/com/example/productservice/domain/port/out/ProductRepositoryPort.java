package com.example.productservice.domain.port.out;

import com.example.productservice.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    
    Product save(Product product);
    
    Optional<Product> findById(Long id);
    
    List<Product> findAll();
    
    void deleteById(Long id);
    
    boolean existsById(Long id);
}

