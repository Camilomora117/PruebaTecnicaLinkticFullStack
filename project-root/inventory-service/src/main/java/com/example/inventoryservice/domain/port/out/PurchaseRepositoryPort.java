package com.example.inventoryservice.domain.port.out;


import com.example.inventoryservice.domain.model.Purchase;

public interface PurchaseRepositoryPort {

    Purchase save(Purchase inventory);
}
