package com.example.inventoryservice.infrastructure.web.controller;

import com.example.inventoryservice.application.port.in.ProcessPurchaseUseCase;
import com.example.inventoryservice.domain.exception.InsufficientStockException;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Purchase;
import com.example.inventoryservice.infrastructure.web.dto.PurchaseRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
@DisplayName("Purchase Controller Tests")
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessPurchaseUseCase processPurchaseUseCase;

    private Purchase testPurchase;

    @BeforeEach
    void setUp() {
        testPurchase = Purchase.builder()
                .id(1L)
                .productId(1L)
                .quantity(5)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("POST /purchase - Should process purchase successfully")
    void shouldProcessPurchaseSuccessfully() throws Exception {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        when(processPurchaseUseCase.processPurchase(1L, 5))
                .thenReturn(testPurchase);

        // When & Then
        mockMvc.perform(post("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Purchase createsd successfully"));

        verify(processPurchaseUseCase, times(1)).processPurchase(1L, 5);
    }

    @Test
    @DisplayName("POST /purchase - Should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(999L);
        request.setQuantity(5);

        when(processPurchaseUseCase.processPurchase(999L, 5))
                .thenThrow(new ProductNotFoundException("Product not found with ID: 999"));

        // When & Then
        mockMvc.perform(post("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(processPurchaseUseCase, times(1)).processPurchase(999L, 5);
    }

    @Test
    @DisplayName("POST /purchase - Should return 409 when insufficient stock")
    void shouldReturn409WhenInsufficientStock() throws Exception {
        // Given
        PurchaseRequest request = new PurchaseRequest();
        request.setProductId(1L);
        request.setQuantity(1000);

        when(processPurchaseUseCase.processPurchase(1L, 1000))
                .thenThrow(new InsufficientStockException("Product without inventory"));

        // When & Then
        mockMvc.perform(post("/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(processPurchaseUseCase, times(1)).processPurchase(1L, 1000);
    }


}
