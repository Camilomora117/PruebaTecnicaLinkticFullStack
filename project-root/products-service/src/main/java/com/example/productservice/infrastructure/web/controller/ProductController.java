package com.example.productservice.infrastructure.web.controller;

import com.example.productservice.application.service.ProductApplicationService;
import com.example.productservice.domain.model.Product;
import com.example.productservice.infrastructure.web.dto.ProductRequest;
import com.example.productservice.infrastructure.web.dto.ProductResponse;
import com.example.productservice.infrastructure.web.mapper.ProductWebMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para operaciones CRUD de productos.
 * <p>
 * <b>Requiere header:</b> X-API-KEY
 * </p>
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductApplicationService productApplicationService;

    /**
     * Crea un nuevo producto.
     *
     * @param productRequest Datos del producto a crear
     * @return ResponseEntity con el producto creado
     * @header X-API-KEY Clave de API requerida en el header
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest.getName());
        Product created = productApplicationService.create(ProductWebMapper.toDomain(productRequest));
        return new ResponseEntity<>(ProductWebMapper.toResponse(created), HttpStatus.CREATED);
    }

    /**
     * Obtiene un producto por su ID.
     *
     * @param id ID del producto (en la URL)
     * @return ResponseEntity con el producto, o 404 si no existe
     * @header X-API-KEY Clave de API requerida en el header
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.info("Fetching product with ID: {}", id);
        Optional<Product> product = productApplicationService.getById(id);
        return product.map(p -> ResponseEntity.ok(ProductWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Lista todos los productos registrados.
     *
     * @return ResponseEntity con la lista de productos
     * @header X-API-KEY Clave de API requerida en el header
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductResponse> products = productApplicationService.getAll().stream()
                .map(ProductWebMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param id ID del producto a actualizar (en la URL)
     * @param productRequest Nuevos datos del producto
     * @return ResponseEntity con el producto actualizado, o 404 si no existe
     * @header X-API-KEY Clave de API requerida en el header
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest productRequest) {
        log.info("Updating product with ID: {}", id);
        Optional<Product> updated =
                productApplicationService.update(id, ProductWebMapper.toDomain(productRequest));
        return updated.map(p -> ResponseEntity.ok(ProductWebMapper.toResponse(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id ID del producto a eliminar (en la URL)
     * @return ResponseEntity 204 si se eliminó, 404 si no existe
     * @header X-API-KEY Clave de API requerida en el header
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with ID: {}", id);
        boolean deleted = productApplicationService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}


