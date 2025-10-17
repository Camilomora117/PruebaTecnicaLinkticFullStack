package com.example.inventoryservice.infrastructure.web.controller;

import com.example.inventoryservice.application.port.in.ProcessPurchaseUseCase;
import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.infrastructure.web.dto.PurchaseRequest;
import com.example.inventoryservice.infrastructure.web.dto.PurchaseResponse;
import com.example.inventoryservice.infrastructure.web.mapper.PurchaseWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controlador REST para operaciones de compra de productos.
 * <p>
 * <b>Requiere header:</b> X-API-KEY
 * </p>
 */
@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final ProcessPurchaseUseCase processPurchaseUseCase;

    /**
     * Procesa una compra de productos, actualizando el inventario correspondiente.
     *
     * @param request Objeto con los datos de la compra (productId, quantity)
     * @return ResponseEntity con los datos de la compra procesada
     * @header X-API-KEY Clave de API requerida en el header
     */
    @PostMapping
    public ResponseEntity<PurchaseResponse> processPurchase(
            @Valid @RequestBody PurchaseRequest request) {

        Purchase result = processPurchaseUseCase.processPurchase(
                request.getProductId(),
                request.getQuantity()
        );
        return ResponseEntity.ok(PurchaseWebMapper.toResponse(result, "Purchase createsd successfully"));
    }

}
