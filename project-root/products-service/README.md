# Product Service

A Spring Boot microservice implementing **true hexagonal architecture** (ports and adapters) for managing products.

## Features

- **True Hexagonal Architecture**: Clean separation between domain, application, and infrastructure layers
- **Ports and Adapters**: Domain-driven design with proper abstraction layers
- **REST API**: Full CRUD operations for products
- **PostgreSQL Integration**: Database persistence with JPA
- **AOP Logging**: Method execution logging using Aspect-Oriented Programming
- **Builder Pattern**: Flexible product creation in domain layer
- **Exception Handling**: Centralized error management
- **Unit Tests**: Comprehensive test coverage with JUnit 5 and Mockito

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Spring AOP**
- **JUnit 5**
- **Mockito**

## Hexagonal Architecture Structure

```
src/main/java/com/example/productservice/
├── domain/                    # DOMAIN LAYER (Core Business Logic)
│   ├── model/                # Domain entities
│   │   └── Product.java
│   ├── port/                 # Ports (interfaces)
│   │   └── ProductRepositoryPort.java
│   ├── usecase/              # Use cases (business logic)
│   │   ├── CreateProductUseCase.java
│   │   ├── GetProductByIdUseCase.java
│   │   ├── GetAllProductsUseCase.java
│   │   ├── UpdateProductUseCase.java
│   │   └── DeleteProductUseCase.java
│   ├── builder/              # Builder pattern
│   │   └── ProductBuilder.java
│   └── exception/            # Domain exceptions
│       ├── ProductNotFoundException.java
│       └── ValidationException.java
├── application/              # APPLICATION LAYER (Use Case Orchestration)
│   ├── dto/                  # Data transfer objects
│   │   └── ProductDTO.java
│   ├── mapper/               # Application mappers
│   │   └── ProductApplicationMapper.java
│   ├── service/              # Application services
│   │   └── ProductApplicationService.java
│   └── controller/           # Input adapters (REST controllers)
│       └── ProductController.java
├── infrastructure/           # INFRASTRUCTURE LAYER (External Concerns)
│   ├── persistence/          # Output adapters (Database)
│   │   ├── entity/           # JPA entities
│   │   │   └── ProductEntity.java
│   │   ├── repository/       # JPA repositories
│   │   │   └── ProductJpaRepository.java
│   │   ├── mapper/           # Infrastructure mappers
│   │   │   └── ProductMapper.java
│   │   └── adapter/          # Repository adapters
│   │       └── ProductRepositoryAdapter.java
│   ├── aop/                  # Cross-cutting concerns
│   │   └── LoggingAspect.java
│   ├── exception/            # Infrastructure exceptions
│   │   ├── ErrorResponse.java
│   │   └── GlobalExceptionHandler.java
│   └── config/               # Configuration
│       └── ProductConfig.java
└── ProductServiceApplication.java
```

## API Endpoints

### Create Product
```
POST /products
Content-Type: application/json

{
    "name": "Product Name",
    "price": 99.99,
    "description": "Product Description"
}
```

### Get Product by ID
```
GET /products/{id}
```

### Get All Products
```
GET /products
```

### Update Product
```
PUT /products/{id}
Content-Type: application/json

{
    "name": "Updated Product Name",
    "price": 149.99,
    "description": "Updated Description"
}
```

### Delete Product
```
DELETE /products/{id}
```

## Database Configuration

The application is configured to connect to PostgreSQL with the following settings:

- **Host**: localhost
- **Port**: 5432
- **Database**: testdb
- **Username**: testuser
- **Password**: testpassword

## Running the Application

1. **Prerequisites**:
   - Java 17
   - Maven 3.6+
   - PostgreSQL running on localhost:5432

2. **Database Setup**:
   ```sql
   CREATE DATABASE testdb;
   CREATE USER testuser WITH PASSWORD 'testpassword';
   GRANT ALL PRIVILEGES ON DATABASE testdb TO testuser;
   ```

3. **Build and Run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the API**:
   - Base URL: http://localhost:8080
   - API Documentation: http://localhost:8080/products

## Domain Entities

### Product (Domain Model)
```java
public class Product {
    private Long id;              // Identificador único
    private String name;          // Nombre del producto (máx 100 caracteres)
    private Double price;         // Precio (debe ser > 0)
    private String description;   // Descripción (máx 500 caracteres)
    
    // Métodos de validación incluidos en la entidad
    public void validate() { ... }
}
```

**Reglas de Negocio:**
- El nombre es obligatorio y no puede exceder 100 caracteres
- El precio debe ser mayor a 0
- La descripción es opcional pero no puede exceder 500 caracteres
- Validación en el dominio, no en la infraestructura

### ProductEntity (JPA Entity)
Entidad de persistencia mapeada a la tabla `products` en PostgreSQL:
```sql
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description VARCHAR(500)
);
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
mvn test -Dtest=ProductApplicationServiceTest
```

### Tipos de Tests Incluidos

#### 1. **Unit Tests** (Capa de Servicio)
- `ProductApplicationServiceTest`: Tests de lógica de negocio
- Mockeo de dependencias con Mockito
- Validación de casos de uso

#### 2. **Controller Tests** (Capa Web)
- `ProductControllerTest`: Tests de endpoints REST
- Validación de requests/responses
- Manejo de errores HTTP

#### 3. **Builder Tests**
- Validación del patrón Builder
- Construcción de objetos del dominio

#### 4. **Integration Tests**
- Tests de integración con base de datos H2
- Validación de flujo completo

### Estructura de Tests
```
src/test/java/com/example/productservice/
├── application/
│   └── service/
│       └── ProductApplicationServiceTest.java
├── infrastructure/
│   └── web/
│       └── controller/
│           └── ProductControllerTest.java
└── domain/
    └── builder/
        └── ProductBuilderTest.java
```

### Ejemplo de Ejecución
```bash
# Limpiar, compilar y ejecutar tests
mvn clean test

# Ver resultados
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
```

## Hexagonal Architecture Layers

### 1. **Domain Layer** (Core Business Logic)
- **Entities**: Pure business objects with validation logic
- **Ports**: Interfaces that define contracts for external interactions
- **Use Cases**: Business logic implementation
- **Domain Exceptions**: Business-specific exceptions

### 2. **Application Layer** (Use Case Orchestration)
- **DTOs**: Data transfer objects for API communication
- **Application Services**: Orchestrate use cases
- **Mappers**: Convert between domain and application objects
- **Input Adapters**: REST controllers

### 3. **Infrastructure Layer** (External Concerns)
- **Output Adapters**: Database repositories, external services
- **JPA Entities**: Database mapping objects
- **Cross-cutting Concerns**: Logging, exception handling
- **Configuration**: Dependency injection setup

## Design Patterns Implemented

1. **Hexagonal Architecture**: True ports and adapters pattern
2. **Ports and Adapters**: Clean separation of concerns
3. **Use Case Pattern**: Business logic encapsulation
4. **Builder Pattern**: Flexible and safe product creation
5. **DTO Pattern**: Data transfer without exposing internal entities
6. **AOP**: Cross-cutting concerns like logging
7. **Repository Pattern**: Data access abstraction
8. **Dependency Injection**: Loose coupling between components
9. **Mapper Pattern**: Object transformation between layers

## Logging

The application uses AOP to log:
- Method entry and exit
- Input parameters
- Return values
- Execution time
- Exceptions

Logs are configured to show DEBUG level for the application packages.

## Error Handling

Centralized exception handling with:
- `ProductNotFoundException`: When product is not found
- `ValidationException`: For business rule violations
- `GlobalExceptionHandler`: Centralized error response formatting
- Proper HTTP status codes and error messages
