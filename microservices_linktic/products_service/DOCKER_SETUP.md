# Docker Compose Setup - Products Service

Este documento describe cómo usar Docker Compose para levantar la aplicación Products Service con PostgreSQL.

## Requisitos Previos

- Docker instalado (versión 20.10+)
- Docker Compose instalado (versión 1.29+)
- Git (para clonar el repositorio)

## Estructura de Archivos

```
.
├── docker-compose.yml           # Configuración principal de Docker Compose
├── docker-compose.override.yml  # Configuración para desarrollo local
├── Dockerfile                   # Dockerfile para la aplicación Spring Boot
├── init-db.sql                  # Script de inicialización de la base de datos
├── .dockerignore                # Archivos a ignorar en la construcción Docker
├── .env                         # Variables de entorno
└── DOCKER_SETUP.md             # Este archivo
```

## Inicio Rápido

### 1. Levantar los servicios

```bash
docker-compose up -d
```

Este comando:
- Construye la imagen de la aplicación Spring Boot
- Levanta el contenedor de PostgreSQL
- Levanta el contenedor de la aplicación
- Inicializa la base de datos con datos de ejemplo

### 2. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver dos contenedores corriendo:
- `linktic_postgres` - Base de datos PostgreSQL
- `linktic_products_service` - Aplicación Spring Boot

### 3. Acceder a la aplicación

- **Swagger UI**: http://localhost:8081/api/v1/swagger-ui.html
- **API Health Check**: http://localhost:8081/api/v1/products/status
- **API Docs**: http://localhost:8081/api/v1/api-docs

### 4. Acceder a la base de datos

```bash
docker-compose exec postgres psql -U postgres -d linktic_products
```

## Comandos Útiles

### Ver logs de los servicios

```bash
# Logs de todos los servicios
docker-compose logs -f

# Logs de un servicio específico
docker-compose logs -f postgres
docker-compose logs -f products-service
```

### Detener los servicios

```bash
docker-compose stop
```

### Reiniciar los servicios

```bash
docker-compose restart
```

### Eliminar los servicios y volúmenes

```bash
# Solo detiene los contenedores
docker-compose down

# Detiene y elimina volúmenes (CUIDADO: elimina datos de la BD)
docker-compose down -v
```

### Reconstruir la imagen de la aplicación

```bash
docker-compose build --no-cache products-service
```

### Ejecutar comandos dentro de un contenedor

```bash
# Acceder a la shell del contenedor de PostgreSQL
docker-compose exec postgres bash

# Acceder a la shell del contenedor de la aplicación
docker-compose exec products-service sh
```

## Variables de Entorno

Las variables de entorno se definen en el archivo `.env`:

```env
POSTGRES_DB=linktic_products
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5432
APP_PORT=8081
```

Para cambiar estas variables, edita el archivo `.env` y reinicia los servicios:

```bash
docker-compose down
docker-compose up -d
```

## Datos de Ejemplo

El script `init-db.sql` crea automáticamente:
- Tabla `products` con estructura completa
- Índices para optimización
- 10 productos de ejemplo (5 activos, 5 inactivos)
- Trigger para actualizar `updated_at` automáticamente

## Solución de Problemas

### La aplicación no puede conectarse a PostgreSQL

1. Verifica que PostgreSQL esté corriendo:
   ```bash
   docker-compose ps
   ```

2. Verifica los logs:
   ```bash
   docker-compose logs postgres
   docker-compose logs products-service
   ```

3. Asegúrate de que el contenedor de PostgreSQL esté saludable:
   ```bash
   docker-compose exec postgres pg_isready -U postgres
   ```

### Puerto 5432 o 8081 ya está en uso

Edita `docker-compose.yml` y cambia los puertos:

```yaml
postgres:
  ports:
    - "5433:5432"  # Cambiar puerto externo

products-service:
  ports:
    - "8082:8081"  # Cambiar puerto externo
```

### Eliminar datos y empezar de nuevo

```bash
docker-compose down -v
docker-compose up -d
```

## Desarrollo Local

Para desarrollo local, el archivo `docker-compose.override.yml` se carga automáticamente y proporciona:
- Logging detallado
- Reconstrucción automática de imágenes
- Acceso directo a la base de datos

## Producción

Para un entorno de producción, considera:

1. Usar variables de entorno seguras (no en `.env`)
2. Cambiar contraseñas por defecto
3. Usar volúmenes persistentes para datos
4. Configurar backups de la base de datos
5. Usar redes privadas
6. Implementar límites de recursos

Ejemplo para producción:

```bash
docker-compose -f docker-compose.yml up -d
```

(Sin el archivo `docker-compose.override.yml`)

## Monitoreo

### Ver estadísticas de los contenedores

```bash
docker stats
```

### Ver eventos de Docker

```bash
docker events
```

## Limpieza

Para limpiar completamente:

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Eliminar imágenes
docker rmi linktic_products_service postgres:15-alpine
```

## Soporte

Para más información sobre Docker Compose, consulta:
- [Documentación oficial de Docker Compose](https://docs.docker.com/compose/)
- [Referencia de docker-compose.yml](https://docs.docker.com/compose/compose-file/)

