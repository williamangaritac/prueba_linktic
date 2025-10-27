# 🏗️ Arquitectura de Microservicios - LINKTIC

## 📋 Índice
1. [Visión General](#visión-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Componentes](#componentes)
4. [Patrones Implementados](#patrones-implementados)
5. [Integración con Angular](#integración-con-angular)
6. [Guía de Inicio Rápido](#guía-de-inicio-rápido)

---

## 🎯 Visión General

Ecosistema de microservicios enterprise-grade implementado con **Spring Boot 3.5.7**, **Java 17**, y **Spring Cloud 2024.0.0**.

### Stack Tecnológico

| Capa | Tecnología | Patrón | Justificación |
|------|------------|--------|---------------|
| **Service Discovery** | Spring Cloud Eureka Server | Service Registry | Simple, integrado con Spring Cloud Gateway |
| **API Gateway** | Spring Cloud Gateway | API Gateway Pattern | Punto de entrada único, enrutamiento dinámico |
| **Resiliencia** | Spring Cloud Circuit Breaker + Resilience4j | Circuit Breaker, Retry, Rate Limiter | Tolerancia a fallos, degradación elegante |
| **Seguridad** | Spring Security + OAuth2 Resource Server + JWT | OAuth2 + JWT | Autenticación/Autorización stateless |
| **Mensajería** | Apache Kafka | Event-Driven Architecture | Comunicación asíncrona, desacoplamiento |
| **Microservicios** | Spring Boot REST | Domain-Driven Design | Separación de responsabilidades |
| **Frontend** | Angular 18 | SPA | Experiencia de usuario moderna |

---

## 🏛️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                    Angular Frontend (4200)                       │
│                  http://localhost:4200                           │
└────────────────────────┬────────────────────────────────────────┘
                         │ HTTP/REST
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   API Gateway (8080)                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ • CORS Configuration                                      │   │
│  │ • Circuit Breaker (Resilience4j)                         │   │
│  │ • Load Balancing                                         │   │
│  │ • Security (OAuth2 + JWT)                                │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────────────────┬────────────────────────────────────────┘
                         │ Service Discovery
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              Eureka Server (8761)                                │
│              Service Registry                                    │
└────────────────────────┬────────────────────────────────────────┘
                         │
        ┌────────────────┼────────────────┬────────────────┐
        ▼                ▼                ▼                ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  Products    │ │  Inventory   │ │   Orders     │ │Notifications │
│  Service     │ │  Service     │ │  Service     │ │  Service     │
│  (8081)      │ │  (8082)      │ │  (8083)      │ │  (8084)      │
│              │ │              │ │      │       │ │              │
│ PostgreSQL   │ │ PostgreSQL   │ │   MySQL      │ │ PostgreSQL   │
└──────────────┘ └──────────────┘ └──────┼───────┘ └──────▲───────┘
                                          │                │
                                          │   Kafka        │
                                          └────────────────┘
                                          order-events topic
```

---

## 🔧 Componentes

### 1️⃣ Eureka Server (Puerto 8761)
**Responsabilidad:** Service Discovery y Service Registry

**Características:**
- Registro automático de microservicios
- Health checks cada 10 segundos
- Dashboard web: http://localhost:8761

**Configuración:**
```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

### 2️⃣ API Gateway (Puerto 8080)
**Responsabilidad:** Punto de entrada único, enrutamiento, seguridad, resiliencia

**Características:**
- ✅ **Enrutamiento Dinámico:** Descubre servicios vía Eureka
- ✅ **Circuit Breaker:** Resilience4j con fallback automático
- ✅ **CORS:** Configurado para Angular (localhost:4200)
- ✅ **Load Balancing:** Balanceo de carga automático
- ✅ **Security:** OAuth2 + JWT (modo desarrollo: permitAll)

**Rutas Configuradas:**
```yaml
/api/v1/products/**      → products-service
/api/v1/inventory/**     → inventory-service
/api/v1/orders/**        → orders-service
/api/v1/notifications/** → notifications-service
```

**Circuit Breaker:**
- Sliding window: 10 requests
- Failure rate threshold: 50%
- Wait duration in open state: 10s
- Timeout: 5s

---

### 3️⃣ Products Service (Puerto 8081)
**Responsabilidad:** Gestión del catálogo de productos

**Base de Datos:** PostgreSQL (`linktic_products`)

**Endpoints Principales:**
- `GET /api/v1/products` - Lista paginada (10 por página)
- `GET /api/v1/products/sku/{sku}` - Detalle por SKU
- `GET /api/v1/frontend/products?page=0` - **Frontend: 6 por página**
- `GET /api/v1/frontend/products/sku/{sku}` - **Frontend: Ver Detalles**

**Datos:** 15 productos con SKUs en formato UUID

---

### 4️⃣ Inventory Service (Puerto 8082)
**Responsabilidad:** Gestión de inventario y stock

**Base de Datos:** PostgreSQL (`linktic_inventory`)

**Endpoints Principales:**
- `GET /api/v1/inventory` - Lista de inventario
- `GET /api/v1/inventory/sku/{sku}` - Stock por SKU
- `POST /api/v1/inventory/check` - Validar disponibilidad

---

### 5️⃣ Orders Service (Puerto 8083)
**Responsabilidad:** Gestión de órdenes de compra

**Base de Datos:** MySQL (`linktic_orders`)

**Endpoints Principales:**
- `POST /api/v1/orders` - Crear orden
- `GET /api/v1/orders` - Lista de órdenes
- `POST /api/v1/frontend/orders/purchase` - **Frontend: Botón Comprar**

**Flujo de Compra:**
1. Valida inventario (llama a inventory-service)
2. Obtiene nombres de productos (llama a products-service)
3. Crea orden en MySQL
4. Publica evento en Kafka topic `order-events`
5. Retorna confirmación al frontend

**Evento Kafka:**
```json
{
  "orderId": 13,
  "orderNumber": "ORD-20251025-XXXXXXXX",
  "items": [
    {
      "sku": "29444ed7a8f8495587365a6b61458735",
      "productName": "Solucion E-commerce",
      "price": 2805.00,
      "quantity": 1
    }
  ],
  "totalAmount": 2805.00,
  "eventType": "ORDER_CREATED"
}
```

---

### 6️⃣ Notifications Service (Puerto 8084)
**Responsabilidad:** Envío de notificaciones por email

**Base de Datos:** PostgreSQL (`linktic_notifications`)

**Características:**
- ✅ Consume eventos de Kafka (`order-events`)
- ✅ Envía emails vía SMTP (Gmail)
- ✅ Guarda registro de notificaciones
- ✅ Estados: PENDING, SENT, FAILED

**Configuración SMTP:**
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: contacto@linktic.com
    password: <APP_PASSWORD>
```

---

## 🎨 Patrones Implementados

### 1. **Service Registry Pattern** (Eureka)
- Registro automático de servicios
- Descubrimiento dinámico
- Health checks

### 2. **API Gateway Pattern**
- Punto de entrada único
- Enrutamiento centralizado
- Cross-cutting concerns (CORS, Security)

### 3. **Circuit Breaker Pattern** (Resilience4j)
- Tolerancia a fallos
- Fallback automático
- Degradación elegante

### 4. **Event-Driven Architecture** (Kafka)
- Comunicación asíncrona
- Desacoplamiento de servicios
- Escalabilidad

### 5. **SOLID Principles**
- **S**ingle Responsibility: Cada servicio tiene una responsabilidad única
- **O**pen/Closed: Extensible vía configuración
- **L**iskov Substitution: Interfaces bien definidas
- **I**nterface Segregation: DTOs específicos por caso de uso
- **D**ependency Inversion: Inyección de dependencias

---

## 🔗 Integración con Angular

### Configuración CORS
El API Gateway está configurado para permitir peticiones desde `http://localhost:4200`:

```java
corsConfig.setAllowedOrigins(List.of("http://localhost:4200"));
corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
```

### Endpoints para el Frontend

#### 1. **Listar Productos (6 por página)**
```typescript
// Angular Service
getProducts(page: number): Observable<Page<Product>> {
  return this.http.get<Page<Product>>(
    `http://localhost:8080/api/v1/frontend/products?page=${page}`
  );
}
```

**Respuesta:**
```json
{
  "content": [...],  // 6 productos
  "totalPages": 3,   // 15 productos / 6 = 3 páginas
  "totalElements": 15,
  "number": 0,       // Página actual
  "size": 6
}
```

#### 2. **Ver Detalles de Producto**
```typescript
// Botón "Ver Detalles"
getProductDetails(sku: string): Observable<Product> {
  return this.http.get<Product>(
    `http://localhost:8080/api/v1/frontend/products/sku/${sku}`
  );
}
```

#### 3. **Botón Comprar**
```typescript
// Botón "Comprar"
purchase(orderRequest: OrderRequest): Observable<OrderResponse> {
  return this.http.post<OrderResponse>(
    `http://localhost:8080/api/v1/frontend/orders/purchase`,
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
  "order": {
    "id": 13,
    "orderNumber": "ORD-20251025-XXXXXXXX",
    "orderItems": [...]
  },
  "notification": "Recibirás un email de confirmación en breve"
}
```

---

## 🚀 Guía de Inicio Rápido

### Prerrequisitos
- ✅ Java 17 (Eclipse Adoptium Temurin)
- ✅ Maven 3.9+
- ✅ PostgreSQL 15+
- ✅ MySQL 8+
- ✅ Apache Kafka 3.9+ (opcional)
- ✅ Node.js 18+ y Angular CLI (para frontend)

### Opción 1: Inicio Automático (Recomendado)

```powershell
# Iniciar todos los servicios
.\start-all-services.ps1

# Si ya están compilados:
.\start-all-services.ps1 -SkipBuild
```

### Opción 2: Inicio Manual

```powershell
# 1. Eureka Server
cd eureka-server
.\mvnw.cmd clean package -DskipTests
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar

# 2. API Gateway (esperar 20s)
cd api-gateway
.\mvnw.cmd clean package -DskipTests
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar

# 3. Microservicios (esperar 20s)
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

1. **Eureka Dashboard:** http://localhost:8761
   - Deberías ver 5 servicios registrados

2. **API Gateway:** http://localhost:8080/actuator/health

3. **Swagger UIs:**
   - Products: http://localhost:8081/api/v1/swagger-ui.html
   - Inventory: http://localhost:8082/api/v1/swagger-ui.html
   - Orders: http://localhost:8083/api/v1/swagger-ui.html
   - Notifications: http://localhost:8084/api/v1/swagger-ui.html

---

## 📊 Monitoreo y Observabilidad

Todos los servicios exponen endpoints de Actuator:

```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/circuitbreakers
```

---

## 🔐 Seguridad (Modo Desarrollo)

Actualmente configurado en **modo desarrollo** con `permitAll()`.

Para producción, descomentar configuración OAuth2 + JWT en `api-gateway/SecurityConfig.java`.

---

## 📚 Documentación Adicional

- [GUIA_COMPLETA_KAFKA_SMTP.md](GUIA_COMPLETA_KAFKA_SMTP.md) - Configuración de Kafka y SMTP
- [KAFKA_SETUP_WINDOWS.md](KAFKA_SETUP_WINDOWS.md) - Instalación de Kafka en Windows
- [INSTRUCCIONES_DB_NOTIFICATIONS.md](INSTRUCCIONES_DB_NOTIFICATIONS.md) - Setup de base de datos

---

## 🎯 Próximos Pasos

1. ✅ Configurar Kafka para eventos en tiempo real
2. ✅ Configurar SMTP para emails reales
3. ✅ Integrar con Angular frontend
4. ⏳ Implementar autenticación OAuth2 + JWT
5. ⏳ Agregar distributed tracing (Zipkin)
6. ⏳ Implementar API rate limiting
7. ⏳ Dockerizar todos los servicios

---

**Desarrollado con ❤️ usando principios SOLID y patrones enterprise**

