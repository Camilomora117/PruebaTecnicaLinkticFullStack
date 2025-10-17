package com.example.inventoryservice.infrastructure.web.client;

import com.example.inventoryservice.domain.exception.ExternalServiceException;
import com.example.inventoryservice.domain.exception.ProductNotFoundException;
import com.example.inventoryservice.domain.model.Product;
import com.example.inventoryservice.domain.port.out.ProductCatalogPort;
import com.example.inventoryservice.infrastructure.web.client.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCatalogClient implements ProductCatalogPort {
    
    private final WebClient webClient;
    
    @Value("${inventory.products-service.api-key}")
    private String apiKey;
    
    @Override
    public Product getById(Long productId) {
        try {
            log.info("Calling products service to validate product ID: {}", productId);
            
            ProductResponse response = webClient
                    .get()
                    .uri("/products/{id}", productId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(1000))
                            .filter(throwable -> !(throwable instanceof WebClientResponseException.NotFound)))
                    .timeout(Duration.ofSeconds(2))
                    .block();
            
            if (response == null) {
                log.error("Products service returned null response for product ID: {}", productId);
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
            
            log.info("Successfully validated product ID: {} - Name: {}", productId, response.getName());
            
            return Product.builder()
                    .id(response.getId())
                    .name(response.getName())
                    .price(response.getPrice())
                    .description(response.getDescription())
                    .build();
                    
        } catch (WebClientResponseException.NotFound e) {
            log.error("Product not found with ID: {} - HTTP 404 from products service", productId);
            throw new ProductNotFoundException("Product not found with ID: " + productId, e);
        } catch (WebClientResponseException.Unauthorized e) {
            log.error("Unauthorized access to products service for product ID: {} - HTTP 401", productId);
            throw new ExternalServiceException("Unauthorized access to products service", e);
        } catch (WebClientResponseException e) {
            log.error("Error calling products service for product ID: {}, status: {}", productId, e.getStatusCode());
            throw new ExternalServiceException("Error calling products service: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling products service for product ID: {}", productId, e);
            throw new ExternalServiceException("Unexpected error calling products service", e);
        }
    }
}
