package com.example.inventoryservice.infrastructure.web.client;

import com.example.inventoryservice.domain.exception.ExternalServiceException;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Product;
import com.example.inventoryservice.infrastructure.web.client.dto.ProductResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Catalog Client Tests")
class ProductCatalogClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductCatalogClient productCatalogClient;

    private ProductResponse testProductResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(productCatalogClient, "apiKey", "test-api-key");

        testProductResponse = new ProductResponse();
        testProductResponse.setId(1L);
        testProductResponse.setName("Test Product");
        testProductResponse.setPrice(99.99);
        testProductResponse.setDescription("Test Description");
    }

    @Test
    @DisplayName("Should get product by ID successfully")
    void shouldGetProductByIdSuccessfully() {
        // Given
        Long productId = 1L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.just(testProductResponse));

        // When
        Product result = productCatalogClient.getById(productId);

        // Then
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        assertEquals("Test Description", result.getDescription());

        verify(webClient, times(1)).get();
        verify(requestHeadersSpec, times(1)).header("X-API-KEY", "test-api-key");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void shouldThrowProductNotFoundExceptionWhenProductNotFound() {
        // Given
        Long productId = 999L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.error(WebClientResponseException.NotFound.create(
                        404, "Not Found", null, null, null)));

        // When & Then
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> productCatalogClient.getById(productId)
        );

        assertTrue(exception.getMessage().contains("Product not found"));
        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should throw ExternalServiceException when unauthorized")
    void shouldThrowExternalServiceExceptionWhenUnauthorized() {
        // Given
        Long productId = 1L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.error(WebClientResponseException.Unauthorized.create(
                        401, "Unauthorized", null, null, null)));

        // When & Then
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> productCatalogClient.getById(productId)
        );

        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should throw ExternalServiceException for other WebClient errors")
    void shouldThrowExternalServiceExceptionForOtherErrors() {
        // Given
        Long productId = 1L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.error(WebClientResponseException.InternalServerError.create(
                        500, "Internal Server Error", null, null, null)));

        // When & Then
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> productCatalogClient.getById(productId)
        );

        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should throw ExternalServiceException for unexpected errors")
    void shouldThrowExternalServiceExceptionForUnexpectedErrors() {
        // Given
        Long productId = 1L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // When & Then
        ExternalServiceException exception = assertThrows(
                ExternalServiceException.class,
                () -> productCatalogClient.getById(productId)
        );

        assertTrue(exception.getMessage().contains("Unexpected error"));
        verify(webClient, times(1)).get();
    }

    @Test
    @DisplayName("Should include API key in request header")
    void shouldIncludeApiKeyInRequestHeader() {
        // Given
        Long productId = 1L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.just(testProductResponse));

        // When
        productCatalogClient.getById(productId);

        // Then
        verify(requestHeadersSpec, times(1)).header("X-API-KEY", "test-api-key");
    }

    @Test
    @DisplayName("Should use correct URI with product ID")
    void shouldUseCorrectUriWithProductId() {
        // Given
        Long productId = 123L;

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.just(testProductResponse));

        // When
        productCatalogClient.getById(productId);

        // Then
        verify(requestHeadersUriSpec, times(1)).uri("/products/{id}", productId);
    }

    @Test
    @DisplayName("Should map ProductResponse to Product domain model correctly")
    void shouldMapProductResponseToProductDomainModelCorrectly() {
        // Given
        Long productId = 1L;
        ProductResponse response = new ProductResponse();
        response.setId(42L);
        response.setName("Mapped Product");
        response.setPrice(199.99);
        response.setDescription("Mapped Description");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.just(response));

        // When
        Product result = productCatalogClient.getById(productId);

        // Then
        assertNotNull(result);
        assertEquals(42L, result.getId());
        assertEquals("Mapped Product", result.getName());
        assertEquals(199.99, result.getPrice());
        assertEquals("Mapped Description", result.getDescription());
    }

    @Test
    @DisplayName("Should handle product with null description")
    void shouldHandleProductWithNullDescription() {
        // Given
        Long productId = 1L;
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Product Without Description");
        response.setPrice(49.99);
        response.setDescription(null);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductResponse.class))
                .thenReturn(Mono.just(response));

        // When
        Product result = productCatalogClient.getById(productId);

        // Then
        assertNotNull(result);
        assertNull(result.getDescription());
        assertEquals("Product Without Description", result.getName());
    }
}
