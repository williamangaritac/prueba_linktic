# 🌐 API Gateway - Punto de Entrada Único

Gateway centralizado para todos los microservicios de Linktic Store. Proporciona enrutamiento, balanceo de carga, circuit breaker, CORS y seguridad.

## 📋 Descripción

El API Gateway actúa como punto de entrada único para todas las peticiones del frontend y clientes externos. Utiliza Spring Cloud Gateway para enrutar peticiones a los microservicios correspondientes mediante Service Discovery (Eureka).

## 🚀 Tecnologías

- **Spring Boot**: 3.5.7
- **Spring Cloud Gateway**: Enrutamiento reactivo
- **Spring Cloud**: 2024.0.0
- **Resilience4j**: Circuit Breaker pattern
- **Java**: 17
- **Eureka Client**: Service Discovery

## 🔌 Configuración

### Puerto
- **8080** - Puerto del API Gateway

### Rutas Configuradas

| Ruta | Servicio Destino | Puerto Destino |
|------|------------------|----------------|
| `/api/v1/products/**` | PRODUCTS-SERVICE | 8081 |
| `/api/v1/inventory/**` | INVENTORY-SERVICE | 8082 |
| `/api/v1/orders/**` | ORDERS-SERVICE | 8083 |
| `/api/v1/frontend/orders/**` | ORDERS-SERVICE | 8083 |
| `/api/v1/notifications/**` | NOTIFICATIONS-SERVICE | 8084 |

### Endpoints del Gateway

| Endpoint | Descripción |
|----------|-------------|
| `http://localhost:8080/actuator/health` | Health check |
| `http://localhost:8080/actuator/gateway/routes` | Lista de rutas configuradas |
| `http://localhost:8080/actuator/circuitbreakers` | Estado de circuit breakers |
| `http://localhost:8080/fallback/*` | Endpoints de fallback |

## 📦 Dependencias Principales

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-reactor-resilience4j</artifactId>
</dependency>
```

## ⚙️ Características

### 1. Service Discovery
- Descubrimiento automático de servicios vía Eureka
- Balanceo de carga con `lb://SERVICE-NAME`
- No requiere configuración manual de IPs/puertos

### 2. Circuit Breaker (Resilience4j)
- **Sliding Window**: 10 peticiones
- **Failure Rate Threshold**: 50%
- **Wait Duration**: 10 segundos en estado abierto
- **Timeout**: 5 segundos por petición

### 3. CORS
- Permite peticiones desde `http://localhost:4200` (Angular)
- Métodos: GET, POST, PUT, DELETE, OPTIONS, PATCH
- Headers: Todos permitidos
- Credentials: Habilitado

### 4. Fallback Endpoints
Respuestas alternativas cuando los servicios no están disponibles:
- `/fallback/products`
- `/fallback/inventory`
- `/fallback/orders`
- `/fallback/notifications`

## 🏃 Ejecución

### Desarrollo Local

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

### Con Maven Wrapper

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Con Docker

```bash
# Construir imagen
docker build -t linktic/api-gateway:latest .

# Ejecutar contenedor
docker run -p 8080:8080 linktic/api-gateway:latest
```

## 🔍 Verificación

### 1. Health Check
```bash
curl http://localhost:8080/actuator/health
```

### 2. Ver Rutas Configuradas
```bash
curl http://localhost:8080/actuator/gateway/routes | jq
```

### 3. Probar Enrutamiento
```bash
# Productos
curl http://localhost:8080/api/v1/products

# Inventario
curl http://localhost:8080/api/v1/inventory/check/SKU123

# Órdenes
curl http://localhost:8080/api/v1/orders
```

### 4. Estado de Circuit Breakers
```bash
curl http://localhost:8080/actuator/circuitbreakers
```

## 📊 Ejemplo de Configuración de Ruta

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: products-service
          uri: lb://PRODUCTS-SERVICE
          predicates:
            - Path=/api/v1/products/**
          filters:
            - name: CircuitBreaker
              args:
                name: productsCircuitBreaker
                fallbackUri: forward:/fallback/products
            - StripPrefix=0
```

## 🔒 Seguridad

### Modo Desarrollo (Actual)
- CSRF: Deshabilitado
- Autenticación: Deshabilitada
- Todos los endpoints: Públicos

### Modo Producción (TODO)
- Implementar OAuth2 + JWT
- Validación de tokens
- Rate limiting
- API Keys

## 🐛 Troubleshooting

### Error: Puerto 8080 en uso
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### Circuit Breaker se abre constantemente
1. Verificar que los microservicios estén corriendo
2. Revisar logs del servicio destino
3. Aumentar `failure-rate-threshold` temporalmente
4. Verificar conectividad de red

### CORS Errors
1. Verificar que el frontend esté en `http://localhost:4200`
2. Revisar configuración en `CorsConfig.java`
3. Verificar headers en la petición

### Servicio no se encuentra
1. Verificar que Eureka Server esté corriendo
2. Verificar que el servicio esté registrado en Eureka
3. Revisar nombre del servicio en la configuración
4. Esperar 30 segundos para que se registre

## 📝 Notas Importantes

- **Debe iniciar después de Eureka Server**
- **Debe iniciar antes que el Frontend**
- Los nombres de servicios en `uri: lb://SERVICE-NAME` deben coincidir con los registrados en Eureka
- El Circuit Breaker se activa automáticamente ante fallos
- Los fallbacks devuelven HTTP 503 (Service Unavailable)

## 📚 Referencias

- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Resilience4j](https://resilience4j.readme.io/)
- [Circuit Breaker Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)

