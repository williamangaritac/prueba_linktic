# Products Service - Microservicio de Productos

Este es el microservicio de productos desarrollado con Spring Boot 3.5.7 y Java 17 para el proyecto de prueba técnica de Linktic.

## Características

- **Framework**: Spring Boot 3.5.7
- **Java**: 17
- **Base de datos**: PostgreSQL
- **Documentación API**: OpenAPI/Swagger
- **Arquitectura**: Microservicios
- **Gestión de dependencias**: Maven

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/linktic_test/products_service/
│   │   ├── ProductsServiceApplication.java
│   │   ├── config/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── controllers/
│   │   │   └── ProductController.java
│   │   ├── model/
│   │   │   ├── entities/
│   │   │   │   └── Product.java
│   │   │   └── dtos/
│   │   │       ├── ProductRequest.java
│   │   │       └── ProductResponse.java
│   │   ├── repositories/
│   │   │   └── ProductRepository.java
│   │   └── services/
│   │       └── ProductService.java
│   └── resources/
│       └── application.yml
└── test/
    ├── java/com/linktic_test/products_service/
    │   ├── ProductsServiceApplicationTests.java
    │   └── controllers/
    │       └── ProductControllerTest.java
    └── resources/
        └── application-test.yml
```

## Configuración de Base de Datos

### 1. Crear la Base de Datos en PostgreSQL

Ejecuta el script SQL ubicado en `database/create_database.sql` en pgAdmin o psql:

```sql
-- El script creará:
-- - Base de datos: products_service
-- - Usuario: linktic (password: linktic)
-- - Tabla: product con todos los campos necesarios
-- - Datos de prueba
```

### 2. Credenciales de Base de Datos

- **Host**: localhost
- **Puerto**: 5432
- **Base de datos**: products_service
- **Usuario**: linktic
- **Contraseña**: linktic

## Endpoints de la API

El microservicio expone los siguientes endpoints:

### Productos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/products` | Crear un producto |
| GET | `/api/v1/products/{id}` | Obtener un producto por ID |
| PUT | `/api/v1/products/{id}` | Actualizar un producto por ID |
| DELETE | `/api/v1/products/{id}` | Eliminar un producto por ID |
| GET | `/api/v1/products` | Listar todos los productos con paginación |
| GET | `/api/v1/products/active` | Listar productos activos con paginación |

### Documentación

| Endpoint | Descripción |
|----------|-------------|
| `/api/v1/swagger-ui.html` | Interfaz de Swagger UI |
| `/api/v1/api-docs` | Documentación OpenAPI en JSON |

## Modelo de Datos

### Product Entity

```java
{
    "id": Long,
    "sku": String (único, requerido),
    "name": String (requerido),
    "description": String,
    "price": BigDecimal (requerido, > 0),
    "status": Boolean (requerido),
    "createdAt": LocalDateTime,
    "updatedAt": LocalDateTime
}
```

## Cómo Ejecutar

### Prerrequisitos

1. Java 17 instalado
2. Maven 3.6+ instalado
3. PostgreSQL 12+ instalado y ejecutándose
4. Base de datos configurada según las instrucciones anteriores

### Pasos para ejecutar

1. **Clonar y navegar al directorio del proyecto**:
   ```bash
   cd products_service
   ```

2. **Instalar dependencias**:
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verificar que la aplicación esté ejecutándose**:
   - La aplicación estará disponible en: `http://localhost:8081`
   - Swagger UI: `http://localhost:8081/api/v1/swagger-ui.html`

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con reporte de cobertura
mvn test jacoco:report
```

## Ejemplos de Uso

### Crear un Producto

```bash
curl -X POST http://localhost:8081/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAPTOP001",
    "name": "Laptop Gaming",
    "description": "Laptop para gaming de alta gama",
    "price": 1599.99,
    "status": true
  }'
```

### Obtener un Producto

```bash
curl -X GET http://localhost:8081/api/v1/products/1
```

### Listar Productos con Paginación

```bash
curl -X GET "http://localhost:8081/api/v1/products?page=0&size=10&sort=name,asc"
```

## Configuración

### Variables de Entorno

Puedes sobrescribir la configuración usando variables de entorno:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/products_service
export SPRING_DATASOURCE_USERNAME=linktic
export SPRING_DATASOURCE_PASSWORD=linktic
export SERVER_PORT=8081
```

### Perfiles de Spring

- `default`: Configuración para desarrollo
- `test`: Configuración para pruebas (usa H2 en memoria)

## Tecnologías Utilizadas

- **Spring Boot 3.5.7**: Framework principal
- **Spring Data JPA**: Persistencia de datos
- **Spring Web**: API REST
- **PostgreSQL**: Base de datos principal
- **H2**: Base de datos para pruebas
- **OpenAPI/Swagger**: Documentación de API
- **Lombok**: Reducción de código boilerplate
- **JUnit 5**: Framework de pruebas
- **Maven**: Gestión de dependencias

## Contacto

Para cualquier consulta sobre este microservicio, contacta al equipo de desarrollo de Linktic.
