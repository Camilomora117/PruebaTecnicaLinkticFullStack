# Environment Configuration

This directory contains environment-specific configuration files for the inventory service.

## Files

- `dev.env` - Development environment configuration
- `prod.env` - Production environment configuration

## Usage

The service will automatically load the appropriate environment file based on the `SPRING_PROFILES_ACTIVE` environment variable.

### Development
```bash
export SPRING_PROFILES_ACTIVE=dev
java -jar inventory-service.jar
```

### Production
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar inventory-service.jar
```

## Configuration Variables

### Database
- `SPRING_DATASOURCE_URL` - PostgreSQL connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

### API Keys
- `INVENTORY_API_KEY` - API key for incoming requests
- `PRODUCTS_SERVICE_API_KEY` - API key for outgoing requests to products service

### Products Service Integration
- `PRODUCTS_SERVICE_URL` - Base URL of the products service
- `PRODUCTS_SERVICE_CONNECT_TIMEOUT` - Connection timeout in milliseconds
- `PRODUCTS_SERVICE_READ_TIMEOUT` - Read timeout in milliseconds
- `PRODUCTS_SERVICE_RETRY_MAX_ATTEMPTS` - Maximum retry attempts
- `PRODUCTS_SERVICE_RETRY_BACKOFF_DELAY` - Backoff delay in milliseconds

