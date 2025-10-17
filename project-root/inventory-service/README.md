# Inventory Service

A Spring Boot microservice implementing Hexagonal Architecture (Ports & Adapters) for managing inventory with JSON:API compliance.

## Features

- **Hexagonal Architecture**: Clean separation between domain, application, and infrastructure layers
- **JSON:API Compliance**: All responses follow JSON:API specification
- **Resilient**: WebClient with timeouts and retry mechanisms for external service calls
- **Secure**: X-API-KEY authentication for all endpoints
- **PostgreSQL Integration**: JPA-based persistence with atomic operations
- **Comprehensive Error Handling**: JSON:API compliant error responses
- **Logging**: AOP-based logging for service and controller methods
- **API Documentation**: Swagger/OpenAPI documentation

## Architecture

### Domain Layer
- `Inventory` entity with business logic (increase, decrease, validation)
- `PurchaseResult` value object for purchase operations
- `ProductView` for external product information
- Domain exceptions for different error scenarios

### Application Layer
- `InventoryQueryService`: Handles inventory queries and updates
- `PurchaseService`: Manages purchase flow with atomic operations and **product validation via Products service API**
- Port interfaces for external dependencies

### Infrastructure Layer
- **Persistence**: JPA entities, repositories, and adapters
- **Web**: REST controllers with JSON:API DTOs
- **External Services**: WebClient for products service integration with **automatic product validation**
- **Security**: API key authentication filter
- **Configuration**: WebClient, Swagger, and application settings

## Product Validation

**Important**: The inventory service **does not** access the products database directly. Instead, it validates product existence by making HTTP calls to the Products service API using the `ProductCatalogPort` interface. This ensures proper service boundaries and maintains the microservices architecture.

The validation flow is:
1. Inventory service receives a request for a product
2. It calls the Products service API via WebClient with proper authentication
3. If the product exists, the operation continues
4. If the product doesn't exist, a 404 error is returned
5. If the Products service is unavailable, a 502 error is returned

## API Endpoints

### GET /inventory/{productId}
Returns current inventory for a product including product information. **Validates product existence by calling the Products service API**.

**Response (JSON:API)**:
```json
{
  "data": {
    "type": "inventory",
    "id": "1",
    "attributes": {
      "product_id": 1,
      "quantity": 100,
      "updated_at": "2024-01-01T10:00:00Z"
    }
  },
  "included": [
    {
      "type": "product",
      "id": "1",
      "attributes": {
        "name": "Product Name",
        "price": 29.99,
        "description": "Product description"
      }
    }
  ]
}
```

### PUT /inventory/{productId}
Updates inventory quantity for a product.

**Request Body**:
```json
{
  "quantity": 150
}
```

### POST /inventory/purchase
Processes a purchase request with atomic inventory decrement. **Validates product existence by calling the Products service API before processing the purchase**.

**Request Body (JSON:API)**:
```json
{
  "data": {
    "type": "purchase",
    "attributes": {
      "product_id": 1,
      "quantity": 2
    }
  }
}
```

**Response (JSON:API)**:
```json
{
  "data": {
    "type": "purchase",
    "id": "1",
    "attributes": {
      "product_id": 1,
      "requested_qty": 2,
      "remaining_qty": 98,
      "occurred_at": "2024-01-01T10:00:00Z"
    }
  },
  "included": [
    {
      "type": "product",
      "id": "1",
      "attributes": {
        "name": "Product Name",
        "price": 29.99,
        "description": "Product description"
      }
    }
  ]
}
```

## Error Responses

All errors follow JSON:API specification:

```json
{
  "errors": [
    {
      "status": "404",
      "title": "Product Not Found",
      "detail": "Product not found with ID: 999"
    }
  ]
}
```

### Error Codes
- `400`: Bad Request (validation errors)
- `401`: Unauthorized (missing/invalid API key)
- `404`: Not Found (product/inventory not found)
- `409`: Conflict (insufficient stock)
- `502`: Bad Gateway (products service unavailable)
- `500`: Internal Server Error

## Configuration

### Environment Variables
- `INVENTORY_API_KEY`: API key for incoming requests
- `PRODUCTS_SERVICE_URL`: Base URL of products service
- `PRODUCTS_SERVICE_API_KEY`: API key for products service calls
- `PRODUCTS_SERVICE_CONNECT_TIMEOUT`: Connection timeout (ms)
- `PRODUCTS_SERVICE_READ_TIMEOUT`: Read timeout (ms)
- `PRODUCTS_SERVICE_RETRY_MAX_ATTEMPTS`: Maximum retry attempts
- `PRODUCTS_SERVICE_RETRY_BACKOFF_DELAY`: Backoff delay (ms)

### Database Configuration
- Host: `localhost`
- Port: `5432`
- Database: `testdb`
- Username: `testuser`
- Password: `testpassword`

## Security

All endpoints require the `X-API-KEY` header with a valid API key. The service also sends the API key when calling the products service.

## Resilience

The service includes resilience patterns for external service calls:
- Connection and read timeouts (configurable)
- Retry mechanism with exponential backoff
- Circuit breaker pattern (can be extended)

## Running the Service

1. Ensure PostgreSQL is running with the inventory database
2. Ensure the products service is running and accessible
3. Set environment variables or use the provided environment files
4. Run: `mvn spring-boot:run`

## API Documentation

Swagger UI is available at: `http://localhost:8081/swagger-ui/`

## Domain Entities

### Inventory (Domain Model)
```java
public class Inventory {
    private Long id;              // Identificador único
    private Long productId;       // ID del producto (FK)
    private Integer quantity;     // Cantidad disponible en stock
    private Instant updatedAt;    // Última actualización
}
```

**Reglas de Negocio:**
- La cantidad no puede ser negativa
- Cada producto tiene un único registro de inventario
- Las actualizaciones son atómicas para evitar race conditions

### Purchase (Domain Model)
```java
public class Purchase {
    private Long id;              // Identificador único de la compra
    private Long productId;       // ID del producto comprado
    private Integer quantity;     // Cantidad comprada
    private Instant createdAt;    // Fecha y hora de la compra
}
```

**Reglas de Negocio:**
- La cantidad debe ser mayor a 0
- Debe haber stock suficiente antes de procesar
- El producto debe existir (validado vía Products Service API)
- La compra y actualización de inventario son transaccionales

### Product (Value Object)
```java
public class Product {
    private Long id;
    private String name;
    private Double price;
    private String description;
}
```
**Nota:** Este es un Value Object que representa la información del producto obtenida del Products Service. No se persiste en la base de datos del Inventory Service.

### InventoryWithProduct (Aggregate)
Objeto compuesto que combina información de inventario con datos del producto:
```java
public class InventoryWithProduct {
    private Inventory inventory;
    private Product product;
}
```

## Database Schema

### Tabla: inventory
```sql
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_product_id ON inventory(product_id);
```

### Tabla: purchase
```sql
CREATE TABLE purchase (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_purchase_product_id ON purchase(product_id);
CREATE INDEX idx_purchase_created_at ON purchase(created_at);
```

## Architecture Structure

```
src/main/java/com/example/inventoryservice/
├── domain/                           # DOMAIN LAYER
│   ├── model/                        # Entidades del dominio
│   │   ├── Inventory.java
│   │   ├── Purchase.java
│   │   ├── Product.java             # Value Object
│   │   └── InventoryWithProduct.java
│   ├── port/
│   │   ├── in/                      # Puertos de entrada (Use Cases)
│   │   │   ├── GetInventoryByProductIdUseCase.java
│   │   │   ├── SetInventoryQuantityUseCase.java
│   │   │   └── ProcessPurchaseUseCase.java
│   │   └── out/                     # Puertos de salida
│   │       ├── InventoryRepositoryPort.java
│   │       ├── PurchaseRepositoryPort.java
│   │       ├── ProductCatalogPort.java
│   │       └── EventPublisherPort.java
│   └── exception/                   # Excepciones del dominio
│       ├── ProductNotFoundException.java
│       ├── InsufficientStockException.java
│       ├── InventoryNotFoundException.java
│       ├── ExternalServiceException.java
│       └── ValidationException.java
├── application/                     # APPLICATION LAYER
│   └── service/                     # Implementación de casos de uso
│       ├── InventoryApplicationService.java
│       └── PurchaseApplicationService.java
└── infrastructure/                  # INFRASTRUCTURE LAYER
    ├── persistence/                 # Adaptadores de persistencia
    │   ├── entity/                  # Entidades JPA
    │   │   ├── InventoryEntity.java
    │   │   └── PurchaseEntity.java
    │   ├── repository/              # Repositorios JPA
    │   │   ├── InventoryJpaRepository.java
    │   │   └── PurchaseJpaRepository.java
    │   ├── mapper/                  # Mappers de persistencia
    │   │   ├── InventoryMapper.java
    │   │   └── PurchaseMapper.java
    │   └── adapter/                 # Adaptadores de repositorio
    │       ├── InventoryRepositoryAdapter.java
    │       └── PurchasePersistenceAdapter.java
    ├── web/                         # Adaptadores REST
    │   ├── controller/
    │   │   ├── InventoryController.java
    │   │   └── PurchaseController.java
    │   ├── dto/                     # DTOs para API
    │   │   ├── InventoryRequest.java
    │   │   ├── InventoryResponse.java
    │   │   ├── PurchaseRequest.java
    │   │   └── PurchaseResponse.java
    │   └── mapper/                  # Mappers web
    │       ├── InventoryWebMapper.java
    │       └── PurchaseWebMapper.java
    ├── client/                      # Cliente HTTP externo
    │   └── ProductCatalogClient.java
    ├── security/                    # Seguridad
    │   └── ApiKeyAuthenticationFilter.java
    ├── aop/                         # Aspectos
    │   └── LoggingAspect.java
    ├── event/                       # Publicación de eventos
    │   └── EventPublisherAdapter.java
    └── config/                      # Configuración
        ├── SecurityConfig.java
        ├── WebClientConfig.java
        └── SwaggerConfig.java
```

## Testing

### Ejecutar Todos los Tests
```bash
mvn test
```

### Ejecutar Tests con Reporte de Cobertura
```bash
mvn clean test jacoco:report
```
El reporte se genera en: `target/site/jacoco/index.html`

### Ejecutar un Test Específico
```bash
mvn test -Dtest=InventoryApplicationServiceTest
```

### Tipos de Tests Incluidos

#### 1. **Unit Tests** (Capa de Aplicación)
- `InventoryApplicationServiceTest`: Tests de lógica de inventario
- `PurchaseApplicationServiceTest`: Tests de lógica de compras
- Mockeo de puertos (repositories, external services)
- Validación de casos de uso

#### 2. **Controller Tests** (Capa Web)
- `InventoryControllerTest`: Tests de endpoints de inventario
- `PurchaseControllerTest`: Tests de endpoints de compras
- Validación de requests/responses
- Manejo de errores HTTP

#### 3. **Integration Tests**
- Tests de integración con base de datos H2
- Tests de integración con WebClient (MockWebServer)
- Validación de flujo completo end-to-end

#### 4. **Adapter Tests**
- Tests de adaptadores de persistencia
- Tests de cliente HTTP externo
- Validación de mappers

### Estructura de Tests
```
src/test/java/com/example/inventoryservice/
├── application/
│   └── service/
│       ├── InventoryApplicationServiceTest.java
│       └── PurchaseApplicationServiceTest.java
├── infrastructure/
│   ├── web/
│   │   └── controller/
│   │       ├── InventoryControllerTest.java
│   │       └── PurchaseControllerTest.java
│   ├── persistence/
│   │   └── adapter/
│   │       └── InventoryRepositoryAdapterTest.java
│   └── client/
│       └── ProductCatalogClientTest.java
└── integration/
    └── InventoryServiceIntegrationTest.java
```

### Ejemplo de Ejecución
```bash
# Limpiar, compilar y ejecutar tests
mvn clean test

# Ver resultados
[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0

# Ejecutar solo tests de integración
mvn test -Dtest=*IntegrationTest

# Ejecutar tests con logs detallados
mvn test -X
```

### Cobertura de Tests
El proyecto incluye JaCoCo para medir cobertura de código:
- **Objetivo**: >80% de cobertura
- **Áreas cubiertas**: Servicios, controladores, adaptadores
- **Reporte**: `target/site/jacoco/index.html`
