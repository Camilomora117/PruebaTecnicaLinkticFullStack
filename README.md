# PruebaTecnicaLinktic

Sistema de microservicios para gestión de productos e inventario.

## 🏗️ Arquitectura

El proyecto está compuesto por:

- **PostgreSQL**: Base de datos relacional (Puerto 5432)
- **Products Service**: Microservicio de gestión de productos (Puerto 8080)
- **Inventory Service**: Microservicio de gestión de inventario (Puerto 8081)
- **Product Management Service**: Aplicación web Angular para gestión de productos (Puerto 4200)

### 🔷 Arquitectura Hexagonal (Ports & Adapters)

Ambos microservicios implementan **Arquitectura Hexagonal**, también conocida como **Ports and Adapters**, propuesta por Alistair Cockburn. Esta arquitectura separa la lógica de negocio del código de infraestructura.

#### ¿Por qué Arquitectura Hexagonal?

1. **Independencia de Frameworks**: La lógica de negocio no depende de Spring, JPA u otros frameworks
2. **Testabilidad**: Facilita las pruebas unitarias al aislar la lógica de negocio
3. **Flexibilidad**: Permite cambiar implementaciones (base de datos, APIs externas) sin afectar el dominio
4. **Mantenibilidad**: Código más limpio y organizado con responsabilidades bien definidas
5. **Escalabilidad**: Facilita la evolución y crecimiento del sistema

#### Estructura de Capas

```
📦 Microservicio
├── 🎯 Domain (Núcleo - Lógica de Negocio)
│   ├── model/          # Entidades del dominio
│   ├── port/           # Interfaces (Puertos)
│   │   ├── in/         # Casos de uso (entrada)
│   │   └── out/        # Repositorios (salida)
│   └── exception/      # Excepciones del dominio
│
├── 🔧 Application (Casos de Uso)
│   └── service/        # Implementación de casos de uso
│
└── 🔌 Infrastructure (Adaptadores)
    ├── persistence/    # Adaptadores de BD (JPA)
    ├── web/           # Adaptadores REST (Controllers)
    ├── security/      # Seguridad (API Keys)
    ├── client/        # Clientes HTTP externos
    └── aop/           # Aspectos (Logging)
```

**Flujo de Datos:**
```
Cliente HTTP → Controller (Adapter) → Use Case (Application) → Domain Logic → Repository Port → JPA Adapter → PostgreSQL
```

### 🎨 Patrones de Diseño Implementados

#### 1. **Repository Pattern**
- **Ubicación**: `domain/port/out/` y `infrastructure/persistence/adapter/`
- **Propósito**: Abstrae el acceso a datos, separando la lógica de persistencia del dominio
- **Beneficio**: Permite cambiar la implementación de persistencia sin afectar la lógica de negocio

```java
// Puerto (Interfaz en el dominio)
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
}

// Adaptador (Implementación en infraestructura)
@Component
public class ProductRepositoryAdapter implements ProductRepository {
    // Implementación con JPA
}
```

#### 2. **Dependency Injection (IoC)**
- **Framework**: Spring Boot
- **Propósito**: Inversión de control para gestionar dependencias
- **Beneficio**: Bajo acoplamiento y alta cohesión

#### 3. **DTO Pattern (Data Transfer Object)**
- **Ubicación**: `infrastructure/web/dto/`
- **Propósito**: Objetos para transferir datos entre capas
- **Beneficio**: Desacopla la representación externa de los modelos del dominio

#### 4. **Mapper Pattern**
- **Ubicación**: `infrastructure/web/mapper/` y `infrastructure/persistence/mapper/`
- **Propósito**: Convierte entre DTOs, entidades JPA y modelos del dominio
- **Beneficio**: Separación clara entre capas

#### 5. **Use Case Pattern**
- **Ubicación**: `domain/port/in/` y `application/service/`
- **Propósito**: Encapsula la lógica de negocio en casos de uso específicos
- **Beneficio**: Código más legible y mantenible

#### 6. **Adapter Pattern**
- **Ubicación**: `infrastructure/persistence/adapter/`, `infrastructure/web/client/`
- **Propósito**: Adapta interfaces externas a las interfaces del dominio
- **Beneficio**: Flexibilidad para cambiar implementaciones

#### 7. **Aspect-Oriented Programming (AOP)**
- **Ubicación**: `infrastructure/aop/LoggingAspect.java`
- **Propósito**: Logging transversal sin contaminar la lógica de negocio
- **Beneficio**: Separación de concerns (preocupaciones cruzadas)

```java
@Aspect
@Component
public class LoggingAspect {
    @Around("execution(* com.example..application.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) {
        // Logging automático
    }
}
```

#### 8. **Strategy Pattern**
- **Ubicación**: Validaciones y reglas de negocio
- **Propósito**: Permite diferentes algoritmos de validación
- **Beneficio**: Extensibilidad sin modificar código existente

#### 9. **Builder Pattern**
- **Uso**: Con Lombok `@Builder` en DTOs y entidades
- **Propósito**: Construcción fluida de objetos complejos
- **Beneficio**: Código más legible y mantenible

#### 10. **Filter Pattern**
- **Ubicación**: `infrastructure/security/ApiKeyAuthenticationFilter.java`
- **Propósito**: Intercepta requests para validar API Keys
- **Beneficio**: Seguridad centralizada

### 🐘 ¿Por qué PostgreSQL?

Elegimos **PostgreSQL** como base de datos por las siguientes razones:

#### Ventajas Técnicas

1. **ACID Compliance**: Garantiza transacciones confiables y consistencia de datos
2. **Relaciones Complejas**: Soporte robusto para relaciones entre productos, inventario y compras
3. **Integridad Referencial**: Foreign keys y constraints para mantener la integridad de datos
4. **Rendimiento**: Excelente rendimiento en operaciones de lectura y escritura
5. **Escalabilidad**: Soporta grandes volúmenes de datos y concurrencia

#### Características Específicas

- **JSON Support**: Permite almacenar datos semi-estructurados si es necesario
- **Índices Avanzados**: B-tree, Hash, GiST, GIN para optimizar consultas
- **Transacciones**: Esencial para operaciones de compra que afectan inventario
- **Open Source**: Sin costos de licenciamiento
- **Comunidad Activa**: Amplia documentación y soporte

#### Casos de Uso en el Proyecto

```sql
-- Ejemplo: Transacción atómica en compras
BEGIN;
  UPDATE inventory SET quantity = quantity - 5 WHERE product_id = 1;
  INSERT INTO purchase (product_id, quantity, purchase_date) VALUES (1, 5, NOW());
COMMIT;
```

- **Products Service**: Gestión CRUD de productos con validaciones
- **Inventory Service**: Control de stock con transacciones atómicas
- **Purchase Service**: Registro de compras con integridad referencial

### 🔐 Principios SOLID Aplicados

- **S** - Single Responsibility: Cada clase tiene una única responsabilidad
- **O** - Open/Closed: Abierto para extensión, cerrado para modificación
- **L** - Liskov Substitution: Los adaptadores pueden sustituirse sin romper el sistema
- **I** - Interface Segregation: Interfaces específicas (ports) en lugar de genéricas
- **D** - Dependency Inversion: Dependemos de abstracciones (ports), no de implementaciones

## 🚀 Inicio Rápido

### Prerrequisitos

- Docker Desktop instalado y en ejecución
- Docker Compose v3.9 o superior

### Ejecutar el Sistema Completo

1. **Clonar el repositorio** (si aún no lo has hecho):
```bash
git clone https://github.com/Camilomora117/PruebaTecnicaLinktic
cd PruebaTecnicaLinktic
```

2. **Iniciar todos los servicios**:
```bash
docker-compose up -d
```

Este comando:
- Construirá las imágenes Docker de los microservicios
- Iniciará PostgreSQL con los scripts de inicialización
- Desplegará ambos microservicios
- Configurará la red interna entre servicios

3. **Verificar el estado de los servicios**:
```bash
docker-compose ps
```

4. **Ver los logs**:
```bash
# Todos los servicios
docker-compose logs -f

# Solo un servicio específico
docker-compose logs -f products-service
docker-compose logs -f inventory-service
docker-compose logs -f postgres
```

## 📡 Endpoints Disponibles

### Product Management Service (Puerto 4200)
- **URL**: `http://localhost:4200`
- **Descripción**: Interfaz web Angular para gestionar productos e inventario
- **Características**:
  - Interfaz de usuario moderna con Angular Material
  - Gestión completa de productos (CRUD)
  - Visualización y actualización de inventario
  - Procesamiento de compras
  - Comunicación con los microservicios backend

### Products Service (Puerto 8080)
- **API Base**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

#### Ejemplos de Curl - Products Service

**1. Crear un Producto (POST)**
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-API-Key: products-api-key-123" \
  -d '{
    "name": "Laptop Dell XPS 15",
    "price": 1299.99,
    "description": "Laptop de alto rendimiento con procesador Intel i7"
  }'
```

**2. Obtener Todos los Productos (GET)**
```bash
curl -X GET http://localhost:8080/products \
  -H "X-API-Key: products-api-key-123"
```

**3. Obtener un Producto por ID (GET)**
```bash
curl -X GET http://localhost:8080/products/1 \
  -H "X-API-Key: products-api-key-123"
```

**4. Actualizar un Producto (PUT)**
```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: products-api-key-123" \
  -d '{
    "name": "Laptop Dell XPS 15 (Actualizado)",
    "price": 1199.99,
    "description": "Laptop de alto rendimiento con descuento especial"
  }'
```

**5. Eliminar un Producto (DELETE)**
```bash
curl -X DELETE http://localhost:8080/products/1 \
  -H "X-API-Key: products-api-key-123"
```

---

### Inventory Service (Puerto 8081)
- **API Base**: `http://localhost:8081`
- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **API Docs**: `http://localhost:8081/v3/api-docs`

#### Ejemplos de Curl - Inventory Service

**1. Obtener Inventario de un Producto (GET)**
```bash
curl -X GET http://localhost:8081/inventory/1 \
  -H "X-API-Key: inventory-api-key-123"
```

**2. Actualizar Cantidad de Inventario (PUT)**
```bash
curl -X PUT http://localhost:8081/inventory/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: inventory-api-key-123" \
  -d '{
    "quantity": 50
  }'
```

**3. Procesar una Compra (POST)**
```bash
curl -X POST http://localhost:8081/purchase \
  -H "Content-Type: application/json" \
  -H "X-API-Key: inventory-api-key-123" \
  -d '{
    "productId": 1,
    "quantity": 5
  }'
```

---

### Base de Datos PostgreSQL
- **Host**: `localhost`
- **Puerto**: `5432`
- **Database**: `testdb`
- **Usuario**: `testuser`
- **Contraseña**: `testpassword`

**Conectar a PostgreSQL con psql:**
```bash
docker-compose exec postgres psql -U testuser -d testdb
```

## 🔑 API Keys

Los servicios están protegidos con API Keys:

- **Products Service**: `products-api-key-123`
- **Inventory Service**: `inventory-api-key-123`

Incluye el header en tus peticiones:
```
X-API-Key: products-api-key-123
```

## 🎯 Flujo de Trabajo Completo (Ejemplo)

Aquí hay un ejemplo de flujo completo para crear un producto, gestionar su inventario y procesar una compra:

```bash
# 1. Crear un producto
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "X-API-Key: products-api-key-123" \
  -d '{
    "name": "Mouse Gaming RGB",
    "price": 49.99,
    "description": "Mouse gaming con iluminación RGB y 7 botones programables"
  }'

# Respuesta esperada: {"id":1,"name":"Mouse Gaming RGB","price":49.99,"description":"..."}

# 2. Actualizar el inventario del producto (ID 1)
curl -X PUT http://localhost:8081/inventory/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: inventory-api-key-123" \
  -d '{
    "quantity": 100
  }'

# 3. Consultar el inventario actual
curl -X GET http://localhost:8081/inventory/1 \
  -H "X-API-Key: inventory-api-key-123"

# 4. Procesar una compra de 3 unidades
curl -X POST http://localhost:8081/purchase \
  -H "Content-Type: application/json" \
  -H "X-API-Key: inventory-api-key-123" \
  -d '{
    "productId": 1,
    "quantity": 3
  }'

# 5. Verificar el inventario actualizado (debería ser 97)
curl -X GET http://localhost:8081/inventory/1 \
  -H "X-API-Key: inventory-api-key-123"
```

## 🛠️ Comandos Útiles

### Detener todos los servicios
```bash
docker-compose down
```

### Detener y eliminar volúmenes (limpieza completa)
```bash
docker-compose down -v
```

### Reconstruir las imágenes
```bash
docker-compose build --no-cache
```

### Reiniciar un servicio específico
```bash
docker-compose restart products-service
```

### Ejecutar comandos dentro de un contenedor
```bash
# Acceder a la base de datos
docker-compose exec postgres psql -U testuser -d testdb

# Ver logs en tiempo real de un servicio
docker-compose logs -f inventory-service
```

## 📁 Estructura del Proyecto

```
PruebaTecnicaLinktic/
├── db/
│   └── init/                          # Scripts SQL de inicialización
│       ├── script_sql_products.sql
│       ├── script_sql_inventory.sql
│       └── script_sql_purchase.sql
├── project-root/
│   ├── products-service/              # Microservicio de productos
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── .dockerignore
│   │   └── pom.xml
│   ├── inventory-service/             # Microservicio de inventario
│   │   ├── src/
│   │   ├── Dockerfile
│   │   ├── .dockerignore
│   │   └── pom.xml
│   └── product-management-service/    # Aplicación web Angular
│       ├── src/
│       ├── Dockerfile
│       ├── .dockerignore
│       ├── nginx.conf
│       ├── package.json
│       └── angular.json
├── docker-compose.yml                 # Orquestación de servicios
└── README.md
```

## 🔧 Desarrollo Local

Si deseas ejecutar los servicios localmente sin Docker:

1. **Iniciar PostgreSQL**:
```bash
docker-compose up -d postgres
```

2. **Ejecutar Products Service**:
```bash
cd project-root/products-service
mvn spring-boot:run
```

3. **Ejecutar Inventory Service**:
```bash
cd project-root/inventory-service
mvn spring-boot:run
```

## 🐛 Troubleshooting

### Los servicios no inician
- Verifica que Docker Desktop esté en ejecución
- Asegúrate de que los puertos 5432, 8080, 8081 y 4200 no estén en uso
- Revisa los logs: `docker-compose logs`

### Error de conexión a la base de datos
- Espera unos segundos más, PostgreSQL tarda en inicializar
- Verifica el health check: `docker-compose ps`

### Reconstruir desde cero
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
```

## 📝 Notas

- Los microservicios esperan a que PostgreSQL esté completamente iniciado gracias al `healthcheck`
- El `inventory-service` depende del `products-service` y esperará a que este inicie
- El `product-management-service` (frontend Angular) depende de ambos servicios backend y esperará a que inicien
- Los datos de la base de datos persisten en un volumen Docker
- Los Dockerfiles usan multi-stage builds para optimizar el tamaño de las imágenes
- El frontend Angular se sirve a través de nginx en el puerto 4200
- Nginx está configurado como proxy inverso para las APIs de los microservicios