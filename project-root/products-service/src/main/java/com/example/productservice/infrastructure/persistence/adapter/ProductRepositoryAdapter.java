package com.example.productservice.infrastructure.persistence.adapter;

import com.example.productservice.domain.model.Product;
import com.example.productservice.domain.port.out.ProductRepositoryPort;
import com.example.productservice.infrastructure.persistence.entity.ProductEntity;
import com.example.productservice.infrastructure.persistence.mapper.ProductMapper;
import com.example.productservice.infrastructure.persistence.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository productJpaRepository;
    
    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        ProductEntity savedEntity = productJpaRepository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id)
                .map(ProductMapper::toDomain);
    }
    
    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll()
                .stream()
                .map(ProductMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }
}

