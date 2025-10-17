# Environments

Archivos de variables de entorno para `product-service`.

- `dev.env`: Variables para entorno de desarrollo
- `prod.env`: Variables para entorno productivo

## Uso

El `application.yml` carga automáticamente el archivo de environment basado en el perfil activo:

```bash
# Para desarrollo (por defecto)
java -jar product-service.jar

# Para producción
java -jar product-service.jar --spring.profiles.active=prod
```

El archivo se carga desde: `./environments/${SPRING_PROFILES_ACTIVE}.env`
