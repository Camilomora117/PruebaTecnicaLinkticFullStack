# Product Management Service

Aplicación web Angular 17 standalone para la gestión de productos e inventario. Interfaz de usuario moderna que consume los microservicios backend de productos e inventario.

## 📋 Descripción del Proyecto

Este servicio es el **frontend** del sistema de gestión de productos, desarrollado con Angular 17 en modo standalone (sin módulos tradicionales). Proporciona una interfaz de usuario moderna y responsiva para:

- **Gestión de Productos**: Visualización, creación, edición y eliminación de productos
- **Control de Inventario**: Consulta y actualización de stock
- **Procesamiento de Compras**: Registro de compras que afectan el inventario
- **Integración con Backend**: Comunicación con los microservicios de productos e inventario

### Arquitectura

```
┌─────────────────────────────────────┐
│   Product Management Service        │
│   (Angular 17 + Angular Material)   │
│   Puerto: 4200                      │
└──────────┬──────────────────────────┘
           │
           ├─── HTTP ───► Products Service (Puerto 8080)
           │
           └─── HTTP ───► Inventory Service (Puerto 8081)
```

## 📋 Requisitos Previos

### Para Desarrollo Local

- **Node.js**: v18.13.0 o superior (requerido por Angular 17)
- **npm**: v8 o superior
- **Angular CLI**: v17 (opcional, se puede usar npx)

### Para Docker

- **Docker**: 20.10 o superior
- **Docker Compose**: v3.9 o superior

### Verificar Versiones

```bash
node --version    # Debe ser >= 18.13.0
npm --version     # Debe ser >= 8.0.0
```

## 🚀 Instalación y Ejecución

### Opción 1: Desarrollo Local

1. **Instalar dependencias**:
```bash
cd project-root/product-management-service
npm install
```

2. **Iniciar servidor de desarrollo**:
```bash
npm start
```

O usando Angular CLI:
```bash
ng serve
```

3. **Acceder a la aplicación**:
```
http://localhost:4200
```

La aplicación se recargará automáticamente al modificar archivos.

### Opción 2: Docker (Recomendado)

Desde la raíz del proyecto:

```bash
docker-compose up -d product-management-service
```

O para iniciar todo el sistema:

```bash
docker-compose up -d
```

La aplicación estará disponible en `http://localhost:4200`

## 🏗️ Estructura del Proyecto

```
src/
├── app/
│   ├── components/
│   │   ├── header/                    # Cabecera fija con navegación
│   │   ├── products/                  # Lista de productos (CRUD)
│   │   └── product-detail/            # Detalle y compra de producto
│   ├── models/
│   │   ├── product.model.ts           # Interface de Producto
│   │   ├── inventory.model.ts         # Interface de Inventario
│   │   └── purchase.model.ts          # Interface de Compra
│   ├── services/
│   │   ├── product.service.ts         # Servicio HTTP para productos
│   │   ├── inventory.service.ts       # Servicio HTTP para inventario
│   │   └── purchase.service.ts        # Servicio HTTP para compras
│   ├── app.component.*                # Componente raíz
│   ├── app.routes.ts                  # Configuración de rutas
│   └── app.config.ts                  # Configuración de providers
├── assets/                            # Recursos estáticos
├── index.html                         # Página principal
├── main.ts                            # Punto de entrada
└── styles.scss                        # Estilos globales
```

## 🎯 Funcionalidades

### 🏠 Lista de Productos (`/products`)
- Grid responsivo con tarjetas Material
- Visualización de nombre, precio y stock
- Indicadores de stock con colores (verde/naranja/rojo)
- Botón "Ver detalles" para cada producto
- Formato de precios en pesos colombianos (COP)
- Estado de carga con spinner
- Manejo de errores con mensajes informativos

### 📦 Detalle de Producto (`/products/:id`)
- Información completa del producto
- Visualización del inventario actual
- Botón "Comprar Producto" con validación de stock
- Notificaciones con MatSnackBar (éxito/error)
- Actualización reactiva del inventario
- Botón "Volver" a la lista
- Manejo de productos no encontrados

### 🔄 Servicios HTTP

**ProductService**
- `getAllProducts()`: Observable con lista de productos
- `getProductById(id)`: Observable con producto específico
- Headers con API Key automático
- Timeout de 10 segundos
- Manejo de errores HTTP

**InventoryService**
- `getInventoryByProductId(id)`: Observable con inventario
- Integración con API de inventario
- Headers con API Key

**PurchaseService**
- `processPurchase(productId, quantity)`: Observable con resultado
- Validación de stock
- Actualización automática de inventario

## 🎨 Tecnologías Utilizadas

- **Angular 17**: Framework principal (standalone components)
- **Angular Material**: Componentes UI (Card, Button, Icon, Toolbar, Chip, Snackbar)
- **RxJS**: Programación reactiva y manejo de observables
- **TypeScript**: Lenguaje de programación tipado
- **SCSS**: Preprocesador CSS para estilos
- **HttpClient**: Cliente HTTP para consumir APIs
- **Router**: Sistema de navegación de Angular

## 🧪 Testing

Este proyecto utiliza **Jest** como framework de pruebas unitarias.

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
npm test

# Ejecutar en modo watch (desarrollo)
npm run test:watch

# Ejecutar con reporte de cobertura
npm run test:coverage
```

### Ver Reporte de Cobertura

Después de ejecutar `npm run test:coverage`, abre:
```
coverage/lcov-report/index.html
```

### Cobertura de Pruebas

✅ **ProductService**
- Obtener todos los productos
- Obtener producto por ID
- Manejo de errores y timeouts
- Validación de headers con API Key

✅ **InventoryService**
- Obtener inventario por producto
- Manejo de errores
- Headers correctos

✅ **PurchaseService**
- Procesar compra exitosa
- Validación de stock insuficiente
- Manejo de errores del servidor
- Cantidad por defecto

✅ **ProductsComponent**
- Carga de productos
- Estado de carga
- Manejo de errores
- Navegación a detalles
- Formato de precios

✅ **ProductDetailComponent**
- Carga de inventario
- Compra de producto
- Validación de stock
- Recarga de inventario
- Navegación

### Objetivos de Cobertura
- **Statements**: > 80%
- **Branches**: > 75%
- **Functions**: > 80%
- **Lines**: > 80%

## 📦 Build

### Build de Desarrollo
```bash
ng build
```

### Build de Producción
```bash
ng build --configuration production
```

Los artefactos se almacenan en `dist/product-management-service/`.

### Build con Docker
El Dockerfile usa multi-stage build:
1. **Stage 1**: Compilación con Node.js 18
2. **Stage 2**: Servir con Nginx Alpine

```bash
docker build -t product-management-service .
```

## 🔧 Configuración

### Variables de Entorno (Desarrollo)

Edita `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost',
  productsPort: 8080,
  inventoryPort: 8081,
  apiKeys: {
    products: 'products-api-key-123',
    inventory: 'inventory-api-key-123'
  }
};
```

### Nginx (Producción)

El archivo `nginx.conf` configura:
- Servidor en puerto 80
- Proxy inverso para `/api/products` → `products-service:8080`
- Proxy inverso para `/api/inventory` → `inventory-service:8081`
- Compresión gzip
- Fallback a index.html para SPA

## 🐛 Troubleshooting

### Error: Node.js version incompatible
```bash
# Actualizar Node.js a v18 o superior
# Descargar desde: https://nodejs.org/
node --version  # Verificar versión
```

### Puerto 4200 ocupado
```bash
ng serve --port 4201
```

### Problemas con dependencias
```bash
rm -rf node_modules package-lock.json
npm install
```

### Error de conexión con backend
- Verifica que los servicios backend estén corriendo
- Revisa las URLs en los servicios
- Confirma que las API Keys sean correctas

### Tests muy lentos
```bash
npm test -- --maxWorkers=50%
```

### Error: Cannot find module 'jest-preset-angular'
```bash
npm install
```

## 📝 Notas Importantes

- **Componentes Standalone**: No usa módulos tradicionales de Angular
- **API Keys**: Configuradas en los servicios HTTP
- **Formato de Precios**: Pesos colombianos (COP)
- **Timeout HTTP**: 10 segundos por defecto
- **Proxy Nginx**: Configurado para evitar CORS en producción
- **Estado Reactivo**: Uso de RxJS Observables
- **Responsive Design**: Se adapta a móviles, tablets y desktop

## 🔗 Integración con Backend

### Products Service (Puerto 8080)
```
GET    /products           - Listar productos
GET    /products/{id}      - Obtener producto
POST   /products           - Crear producto
PUT    /products/{id}      - Actualizar producto
DELETE /products/{id}      - Eliminar producto

Header: X-API-Key: products-api-key-123
```

### Inventory Service (Puerto 8081)
```
GET    /inventory/{productId}  - Obtener inventario
PUT    /inventory/{productId}  - Actualizar inventario
POST   /purchase               - Procesar compra

Header: X-API-Key: inventory-api-key-123
```

## 📚 Recursos

- [Angular 17 Documentation](https://angular.io/docs)
- [Angular Material](https://material.angular.io/)
- [RxJS Documentation](https://rxjs.dev/)
- [Jest Documentation](https://jestjs.io/)
- [Testing Library Angular](https://testing-library.com/docs/angular-testing-library/intro/)

## 👨‍💻 Autor

Desarrollado como parte de la prueba técnica LinkTic - Sistema de Microservicios
