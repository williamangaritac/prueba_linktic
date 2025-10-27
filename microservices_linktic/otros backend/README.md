# 🚀 LINKTIC Microservices Ecosystem

> **Arquitectura de Microservicios Enterprise-Grade con Spring Boot 3.5.7, Spring Cloud 2024.0.0, Java 17 y Angular 18**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.0.0-blue.svg)](https://spring.io/projects/spring-cloud)
[![Angular](https://img.shields.io/badge/Angular-18-red.svg)](https://angular.io/)

---

## 📋 Tabla de Contenidos

- [Visión General](#-visión-general)
- [Arquitectura](#-arquitectura)
- [Tecnologías](#-tecnologías)
- [Inicio Rápido](#-inicio-rápido)
- [Servicios](#-servicios)
- [Documentación](#-documentación)
- [Integración con Angular](#-integración-con-angular)

---

## 🎯 Visión General

Sistema completo de microservicios que implementa:

✅ **Service Discovery** con Eureka Server  
✅ **API Gateway** con Spring Cloud Gateway  
✅ **Circuit Breaker** con Resilience4j  
✅ **Event-Driven Architecture** con Apache Kafka  
✅ **Seguridad** con OAuth2 + JWT (preparado)  
✅ **CORS** configurado para Angular  
✅ **Paginación** optimizada (6 productos por página)  
✅ **Notificaciones** por email vía SMTP  

---

## 🏛️ Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│              Angular Frontend (4200)                         │
└────────────────────┬────────────────────────────────────────┘
                     │ HTTP/REST
                     ▼
┌─────────────────────────────────────────────────────────────┐
│           API Gateway (8080)                                 │
│  • CORS  • Circuit Breaker  • Load Balancing  • Security    │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│           Eureka Server (8761)                               │
│           Service Registry & Discovery                       │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┬────────────┐
        ▼            ▼            ▼            ▼
┌──────────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
│  Products    │ │Inventory │ │ Orders   │ │Notifications │
│  (8081)      │ │ (8082)   │ │ (8083)   │ │  (8084)      │
│ PostgreSQL   │ │PostgreSQL│ │  MySQL   │ │ PostgreSQL   │
└──────────────┘ └──────────┘ └────┬─────┘ └──────▲───────┘
                                    │              │
                                    └──── Kafka ───┘
```

---

## 🛠️ Tecnologías

### Backend
- **Spring Boot** 3.5.7
- **Spring Cloud** 2024.0.0
- **Java** 17 (Eclipse Adoptium Temurin)
- **Maven** 3.9+
- **PostgreSQL** 15+ (products, inventory, notifications)
- **MySQL** 8+ (orders)
- **Apache Kafka** 3.9+ (event streaming)
- **Resilience4j** (circuit breaker)
- **Swagger/OpenAPI** 3.0 (documentación)

### Frontend
- **Angular** 18
- **TypeScript** 5+
- **RxJS** 7+

---

## 🚀 Inicio Rápido

### Prerrequisitos

```bash
# Verificar Java 17
java -version

# Verificar Maven
mvn -version

# Verificar PostgreSQL
psql --version

# Verificar MySQL
mysql --version
```

### Opción 1: Inicio Automático (Recomendado)

```powershell
# Clonar el repositorio
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

# Iniciar todos los servicios
.\start-all-services.ps1

# Si ya están compilados:
.\start-all-services.ps1 -SkipBuild
```

### Opción 2: Inicio Manual

```powershell
# 1. Eureka Server (Service Discovery)
cd eureka-server
.\mvnw.cmd clean package -DskipTests
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar

# 2. API Gateway (esperar 20 segundos)
cd api-gateway
.\mvnw.cmd clean package -DskipTests
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar

# 3. Microservicios (esperar 20 segundos)
cd products_service
java -jar target/products_service-0.0.1-SNAPSHOT.jar

cd inventory_service
java -jar target/inventory_service-0.0.1-SNAPSHOT.jar

cd orders_service
java -jar target/orders_service-0.0.1-SNAPSHOT.jar

cd notifications_service
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

### Verificación

```powershell
# Verificar puertos activos
netstat -an | findstr "8761 8080 8081 8082 8083 8084"
```

Deberías ver:
```
TCP    0.0.0.0:8761    LISTENING  # Eureka
TCP    0.0.0.0:8080    LISTENING  # API Gateway
TCP    0.0.0.0:8081    LISTENING  # Products
TCP    0.0.0.0:8082    LISTENING  # Inventory
TCP    0.0.0.0:8083    LISTENING  # Orders
TCP    0.0.0.0:8084    LISTENING  # Notifications
```

---

## 🔧 Servicios

### 1. Eureka Server (8761)
**Service Discovery & Registry**

- Dashboard: http://localhost:8761
- Registra y descubre todos los microservicios
- Health checks automáticos

### 2. API Gateway (8080)
**Punto de Entrada Único**

- Enrutamiento dinámico
- Circuit Breaker con Resilience4j
- CORS configurado para Angular
- Load Balancing automático
- Actuator: http://localhost:8080/actuator/health

### 3. Products Service (8081)
**Gestión de Catálogo de Productos**

- Swagger: http://localhost:8081/api/v1/swagger-ui.html
- Base de Datos: PostgreSQL (`linktic_products`)
- 15 productos con SKUs UUID
- Paginación: 6 productos por página (frontend)

**Endpoints Clave:**
```
GET  /api/v1/frontend/products?page=0
GET  /api/v1/frontend/products/sku/{sku}
GET  /api/v1/frontend/products/active?page=0
```

### 4. Inventory Service (8082)
**Gestión de Inventario**

- Swagger: http://localhost:8082/api/v1/swagger-ui.html
- Base de Datos: PostgreSQL (`linktic_inventory`)
- Validación de stock en tiempo real

### 5. Orders Service (8083)
**Gestión de Órdenes de Compra**

- Swagger: http://localhost:8083/api/v1/swagger-ui.html
- Base de Datos: MySQL (`linktic_orders`)
- Publica eventos en Kafka

**Endpoints Clave:**
```
POST /api/v1/frontend/orders/purchase  # Botón "Comprar"
POST /api/v1/frontend/orders/validate
```

**Flujo de Compra:**
1. Valida inventario → inventory-service
2. Obtiene nombres de productos → products-service
3. Crea orden en MySQL
4. Publica evento en Kafka (`order-events`)
5. Retorna confirmación

### 6. Notifications Service (8084)
**Notificaciones por Email**

- Swagger: http://localhost:8084/api/v1/swagger-ui.html
- Base de Datos: PostgreSQL (`linktic_notifications`)
- Consume eventos de Kafka
- Envía emails vía SMTP (Gmail)

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [ARQUITECTURA_MICROSERVICIOS.md](ARQUITECTURA_MICROSERVICIOS.md) | Arquitectura completa del sistema |
| [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md) | Guía de integración con Angular |
| [GUIA_COMPLETA_KAFKA_SMTP.md](GUIA_COMPLETA_KAFKA_SMTP.md) | Configuración de Kafka y SMTP |
| [KAFKA_SETUP_WINDOWS.md](KAFKA_SETUP_WINDOWS.md) | Instalación de Kafka en Windows |

---

## 🎨 Integración con Angular

### Configuración Base

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### Ejemplo: Listar Productos (6 por página)

```typescript
// product.service.ts
getProducts(page: number): Observable<Page<Product>> {
  return this.http.get<Page<Product>>(
    `${this.apiUrl}/frontend/products?page=${page}`
  );
}
```

**Respuesta:**
```json
{
  "content": [...],      // 6 productos
  "totalPages": 3,       // 15 productos / 6 = 3 páginas
  "totalElements": 15,
  "number": 0,
  "size": 6
}
```

### Ejemplo: Botón "Comprar"

```typescript
// order.service.ts
purchase(orderRequest: OrderRequest): Observable<PurchaseResponse> {
  return this.http.post<PurchaseResponse>(
    `${this.apiUrl}/frontend/orders/purchase`,
    orderRequest
  );
}
```

**Request:**
```json
{
  "orderItems": [
    {
      "sku": "29444ed7a8f8495587365a6b61458735",
      "price": 2805.00,
      "quantity": 1
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "¡Compra realizada exitosamente!",
  "order": { ... },
  "orderNumber": "ORD-20251025-XXXXXXXX",
  "notification": "Recibirás un email de confirmación en breve"
}
```

Ver [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md) para ejemplos completos.

---

## 🔍 Monitoreo

### Eureka Dashboard
http://localhost:8761

Muestra todos los servicios registrados con su estado.

### Actuator Endpoints

```bash
# Health Check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Circuit Breakers
curl http://localhost:8080/actuator/circuitbreakers
```

### Swagger UIs

- Products: http://localhost:8081/api/v1/swagger-ui.html
- Inventory: http://localhost:8082/api/v1/swagger-ui.html
- Orders: http://localhost:8083/api/v1/swagger-ui.html
- Notifications: http://localhost:8084/api/v1/swagger-ui.html

---

## 🧪 Testing

### Probar el Flujo Completo

1. **Abrir Swagger de Orders:**
   http://localhost:8083/api/v1/swagger-ui.html

2. **Crear una orden:**
   ```json
   POST /api/v1/frontend/orders/purchase
   {
     "orderItems": [
       {
         "sku": "29444ed7a8f8495587365a6b61458735",
         "price": 2805.00,
         "quantity": 1
       }
     ]
   }
   ```

3. **Verificar:**
   - ✅ Orden creada en MySQL
   - ✅ Evento publicado en Kafka
   - ✅ Email enviado (si SMTP configurado)
   - ✅ Notificación guardada en PostgreSQL

---

## 📊 Patrones Implementados

- ✅ **Service Registry Pattern** (Eureka)
- ✅ **API Gateway Pattern** (Spring Cloud Gateway)
- ✅ **Circuit Breaker Pattern** (Resilience4j)
- ✅ **Event-Driven Architecture** (Kafka)
- ✅ **SOLID Principles**
- ✅ **Domain-Driven Design**
- ✅ **Repository Pattern**
- ✅ **DTO Pattern**

---

## 🔐 Seguridad

Actualmente en **modo desarrollo** con `permitAll()`.

Para producción, descomentar configuración OAuth2 + JWT en:
- `api-gateway/src/main/java/com/linktic_test/gateway/config/SecurityConfig.java`

---

## 🎯 Próximos Pasos

- [ ] Configurar Kafka para eventos en tiempo real
- [ ] Configurar SMTP para emails reales
- [ ] Implementar autenticación OAuth2 + JWT
- [ ] Agregar distributed tracing (Zipkin)
- [ ] Implementar API rate limiting
- [ ] Dockerizar todos los servicios
- [ ] CI/CD con GitHub Actions

---

## 📞 Soporte

Para más información, consulta la documentación en:
- [ARQUITECTURA_MICROSERVICIOS.md](ARQUITECTURA_MICROSERVICIOS.md)
- [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md)

---

## 📄 Licencia

Este proyecto es parte de la prueba técnica de LINKTIC.

---

**Desarrollado con ❤️ usando principios SOLID y patrones enterprise**

🚀 **¡Listo para producción!**

