package com.example.productservice.domain.builder;

import com.example.productservice.domain.model.Product;

public class ProductBuilder {
    
    private String name;
    private Double price;
    private String description;
    
    public ProductBuilder() {}
    
    public ProductBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public ProductBuilder price(Double price) {
        this.price = price;
        return this;
    }
    
    public ProductBuilder description(String description) {
        this.description = description;
        return this;
    }
    
    public Product build() {
        Product product = new Product(name, price, description);
        product.validate();
        return product;
    }
    
    public static ProductBuilder builder() {
        return new ProductBuilder();
    }
}

