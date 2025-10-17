package com.example.inventoryservice.infrastructure.web.controller;

import com.example.inventoryservice.application.port.in.GetInventoryByProductIdUseCase;
import com.example.inventoryservice.application.port.in.SetInventoryQuantityUseCase;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.InventoryWithProduct;
import com.example.inventoryservice.infrastructure.web.dto.InventoryRequest;
import com.example.inventoryservice.infrastructure.web.dto.InventoryResponse;
import com.example.inventoryservice.infrastructure.web.dto.InventoryResponseBasic;
import com.example.inventoryservice.infrastructure.web.mapper.InventoryWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de inventario.
 * <p>
 * <b>Requiere header:</b> X-API-KEY
 * </p>
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final SetInventoryQuantityUseCase setInventoryQuantityUseCase;
    private final GetInventoryByProductIdUseCase getInventoryByProductIdUseCase;

    /**
     * Obtiene la información de inventario para un producto específico.
     *
     * @param productId ID del producto (en la URL)
     * @return ResponseEntity con los datos del inventario del producto
     * @header X-API-KEY Clave de API requerida en el header
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(@PathVariable Long productId) {
        InventoryWithProduct inventoryWithProduct = getInventoryByProductIdUseCase.getInventoryByProductId(productId);
        var response = InventoryWebMapper.toResponse(inventoryWithProduct);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la cantidad de inventario para un producto específico.
     *
     * @param productId ID del producto (en la URL)
     * @param request   Objeto con la nueva cantidad de inventario
     * @return ResponseEntity con los datos actualizados del inventario
     * @header X-API-KEY Clave de API requerida en el header
     */
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponseBasic> updateInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryRequest request) {

        Inventory updatedInventory = setInventoryQuantityUseCase.setInventoryQuantity(
                productId,
                request.getQuantity()
        );

        return ResponseEntity.ok(InventoryWebMapper.toResponse(updatedInventory));
    }

}

