package com.example.inventoryservice.infrastructure.web.controller;

import com.example.inventoryservice.application.port.in.GetInventoryByProductIdUseCase;
import com.example.inventoryservice.application.port.in.SetInventoryQuantityUseCase;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Inventory;
import com.example.inventoryservice.domain.model.InventoryWithProduct;
import com.example.inventoryservice.domain.model.Product;
import com.example.inventoryservice.infrastructure.web.dto.InventoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
@DisplayName("Inventory Controller Tests")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GetInventoryByProductIdUseCase getInventoryByProductIdUseCase;

    @MockBean
    private SetInventoryQuantityUseCase setInventoryQuantityUseCase;

    private InventoryWithProduct testInventoryWithProduct;
    private Inventory testInventory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testInventory = Inventory.builder()
                .id(1L)
                .productId(1L)
                .quantity(100)
                .updatedAt(Instant.now())
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(99.99)
                .description("Test Description")
                .build();

        testInventoryWithProduct = new InventoryWithProduct(testInventory, testProduct);
    }

    @Test
    @DisplayName("GET /inventory/{productId} - Should return inventory successfully")
    void shouldGetInventorySuccessfully() throws Exception {
        // Given
        Long productId = 1L;
        when(getInventoryByProductIdUseCase.getInventoryByProductId(productId))
                .thenReturn(testInventoryWithProduct);

        // When & Then
        mockMvc.perform(get("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(100));

        verify(getInventoryByProductIdUseCase, times(1)).getInventoryByProductId(productId);
    }

    @Test
    @DisplayName("GET /inventory/{productId} - Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        Long productId = 999L;
        when(getInventoryByProductIdUseCase.getInventoryByProductId(productId))
                .thenThrow(new ProductNotFoundException("Product not found with ID: " + productId));

        // When & Then
        mockMvc.perform(get("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(getInventoryByProductIdUseCase, times(1)).getInventoryByProductId(productId);
    }

    @Test
    @DisplayName("GET /inventory/{productId} - Should handle invalid product ID")
    void shouldHandleInvalidProductId() throws Exception {
        // Given
        Long productId = -1L;
        when(getInventoryByProductIdUseCase.getInventoryByProductId(productId))
                .thenThrow(new IllegalArgumentException("Product ID must be a positive number"));

        // When & Then
        mockMvc.perform(get("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(getInventoryByProductIdUseCase, times(1)).getInventoryByProductId(productId);
    }

    @Test
    @DisplayName("PUT /inventory/{productId} - Should update inventory successfully")
    void shouldUpdateInventorySuccessfully() throws Exception {
        // Given
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(50);

        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(50)
                .updatedAt(Instant.now())
                .build();

        when(setInventoryQuantityUseCase.setInventoryQuantity(productId, 50))
                .thenReturn(updatedInventory);

        // When & Then
        mockMvc.perform(put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(50));

        verify(setInventoryQuantityUseCase, times(1)).setInventoryQuantity(productId, 50);
    }

    @Test
    @DisplayName("PUT /inventory/{productId} - Should return 400 for negative quantity")
    void shouldReturn400ForNegativeQuantity() throws Exception {
        // Given
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(-10);

        when(setInventoryQuantityUseCase.setInventoryQuantity(productId, -10))
                .thenThrow(new IllegalArgumentException("Quantity cannot be negative"));

        // When & Then
        mockMvc.perform(put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(setInventoryQuantityUseCase, times(1)).setInventoryQuantity(productId, -10);
    }

    @Test
    @DisplayName("PUT /inventory/{productId} - Should return 404 when product not found")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        // Given
        Long productId = 999L;
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(50);

        when(setInventoryQuantityUseCase.setInventoryQuantity(productId, 50))
                .thenThrow(new ProductNotFoundException("Product not found with ID: " + productId));

        // When & Then
        mockMvc.perform(put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(setInventoryQuantityUseCase, times(1)).setInventoryQuantity(productId, 50);
    }

    @Test
    @DisplayName("PUT /inventory/{productId} - Should accept zero quantity")
    void shouldAcceptZeroQuantity() throws Exception {
        // Given
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(0);

        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(0)
                .updatedAt(Instant.now())
                .build();

        when(setInventoryQuantityUseCase.setInventoryQuantity(productId, 0))
                .thenReturn(updatedInventory);

        // When & Then
        mockMvc.perform(put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(0));

        verify(setInventoryQuantityUseCase, times(1)).setInventoryQuantity(productId, 0);
    }

    @Test
    @DisplayName("PUT /inventory/{productId} - Should update inventory with large quantity")
    void shouldUpdateInventoryWithLargeQuantity() throws Exception {
        // Given
        Long productId = 1L;
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(10000);

        Inventory updatedInventory = Inventory.builder()
                .id(1L)
                .productId(productId)
                .quantity(10000)
                .updatedAt(Instant.now())
                .build();

        when(setInventoryQuantityUseCase.setInventoryQuantity(productId, 10000))
                .thenReturn(updatedInventory);

        // When & Then
        mockMvc.perform(put("/inventory/{productId}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10000));

        verify(setInventoryQuantityUseCase, times(1)).setInventoryQuantity(productId, 10000);
    }
}
