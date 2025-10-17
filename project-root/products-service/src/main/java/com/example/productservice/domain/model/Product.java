package com.example.productservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    private Long id;
    private String name;
    private Double price;
    private String description;
    
    public Product(String name, Double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
    
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name cannot exceed 100 characters");
        }
        if (description != null && description.length() > 500) {
            throw new IllegalArgumentException("Product description cannot exceed 500 characters");
        }
    }
}

