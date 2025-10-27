# 🚀 Guía de Inicio Rápido - LINKTIC Microservices

## ✅ Estado Actual del Proyecto

Todos los servicios han sido compilados exitosamente:

- ✅ **Eureka Server** (8761) - Compilado
- ✅ **API Gateway** (8080) - Compilado
- ✅ **Products Service** (8081) - Compilado y actualizado
- ✅ **Inventory Service** (8082) - Compilado
- ✅ **Orders Service** (8083) - Compilado
- ✅ **Notifications Service** (8084) - Compilado

---

## 🎯 Opción 1: Inicio Automático (Recomendado)

### Paso 1: Ejecutar el Script Maestro

```powershell
# Desde la raíz del proyecto
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

# Iniciar todos los servicios (ya compilados)
.\start-all-services.ps1 -SkipBuild
```

El script:
1. ✅ Inicia Eureka Server (8761)
2. ✅ Espera 20 segundos
3. ✅ Inicia API Gateway (8080)
4. ✅ Espera 20 segundos
5. ✅ Inicia los 4 microservicios en paralelo
6. ✅ Verifica que todos los puertos estén activos

### Paso 2: Verificar en Eureka Dashboard

Abre tu navegador en: **http://localhost:8761**

Deberías ver 5 servicios registrados:
- API-GATEWAY
- PRODUCTS-SERVICE
- INVENTORY-SERVICE
- ORDERS-SERVICE
- NOTIFICATIONS-SERVICE

---

## 🔧 Opción 2: Inicio Manual

### Paso 1: Iniciar Eureka Server

```powershell
cd eureka-server
java -jar target/eureka-server-0.0.1-SNAPSHOT.jar
```

**Esperar 20 segundos** hasta ver:
```
Eureka Server started on port 8761
```

### Paso 2: Iniciar API Gateway

```powershell
# En una nueva terminal
cd api-gateway
java -jar target/api-gateway-0.0.1-SNAPSHOT.jar
```

**Esperar 20 segundos** hasta ver:
```
Netty started on port 8080
```

### Paso 3: Iniciar Products Service

```powershell
# En una nueva terminal
cd products_service
java -jar target/products_service-0.0.1-SNAPSHOT.jar
```

### Paso 4: Iniciar Inventory Service

```powershell
# En una nueva terminal
cd inventory_service
java -jar target/inventory_service-0.0.1-SNAPSHOT.jar
```

### Paso 5: Iniciar Orders Service

```powershell
# En una nueva terminal
cd orders_service
java -jar target/orders_service-0.0.1-SNAPSHOT.jar
```

### Paso 6: Iniciar Notifications Service

```powershell
# En una nueva terminal
cd notifications_service
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Verificación del Sistema

### 1. Verificar Puertos Activos

```powershell
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

### 2. Verificar Eureka Dashboard

**URL:** http://localhost:8761

Deberías ver todos los servicios con estado **UP**.

### 3. Verificar API Gateway Health

```powershell
curl http://localhost:8080/actuator/health
```

Respuesta esperada:
```json
{"status":"UP"}
```

---

## 🎨 Probar Integración con Angular

### Endpoints Disponibles para el Frontend

#### 1. Listar Productos (6 por página)

```bash
# Página 0 (productos 1-6)
curl http://localhost:8080/api/v1/frontend/products?page=0

# Página 1 (productos 7-12)
curl http://localhost:8080/api/v1/frontend/products?page=1

# Página 2 (productos 13-15)
curl http://localhost:8080/api/v1/frontend/products?page=2
```

**Respuesta:**
```json
{
  "content": [
    {
      "id": 1,
      "sku": "29444ed7a8f8495587365a6b61458735",
      "name": "Solucion E-commerce",
      "description": "Plataforma completa de comercio electrónico",
      "price": 2805.00,
      "status": true
    }
    // ... 5 productos más
  ],
  "totalPages": 3,
  "totalElements": 15,
  "number": 0,
  "size": 6
}
```

#### 2. Ver Detalles de Producto

```bash
curl http://localhost:8080/api/v1/frontend/products/sku/29444ed7a8f8495587365a6b61458735
```

**Respuesta:**
```json
{
  "id": 1,
  "sku": "29444ed7a8f8495587365a6b61458735",
  "name": "Solucion E-commerce",
  "description": "Plataforma completa de comercio electrónico con gestión de inventario, pagos y envíos",
  "price": 2805.00,
  "status": true,
  "createdAt": "2025-10-25T10:30:00",
  "updatedAt": "2025-10-25T10:30:00"
}
```

#### 3. Botón "Comprar" - Crear Orden

```bash
curl -X POST http://localhost:8080/api/v1/frontend/orders/purchase \
  -H "Content-Type: application/json" \
  -d '{
    "orderItems": [
      {
        "sku": "29444ed7a8f8495587365a6b61458735",
        "price": 2805.00,
        "quantity": 1
      }
    ]
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "¡Compra realizada exitosamente!",
  "order": {
    "id": 13,
    "orderNumber": "ORD-20251025-XXXXXXXX",
    "orderItems": [...]
  },
  "orderNumber": "ORD-20251025-XXXXXXXX",
  "notification": "Recibirás un email de confirmación en breve"
}
```

**Flujo Completo:**
1. ✅ Valida inventario en `inventory-service`
2. ✅ Obtiene nombres de productos desde `products-service`
3. ✅ Crea orden en MySQL
4. ✅ Publica evento en Kafka topic `order-events`
5. ✅ `notifications-service` consume el evento
6. ✅ Envía email de confirmación (si SMTP configurado)
7. ✅ Guarda notificación en PostgreSQL

---

## 📊 Swagger UIs

Accede a la documentación interactiva de cada servicio:

- **Products:** http://localhost:8081/api/v1/swagger-ui.html
- **Inventory:** http://localhost:8082/api/v1/swagger-ui.html
- **Orders:** http://localhost:8083/api/v1/swagger-ui.html
- **Notifications:** http://localhost:8084/api/v1/swagger-ui.html

---

## 🎨 Configuración en Angular

### environment.ts

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1'
};
```

### Ejemplo de Servicio

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/frontend/products`;

  constructor(private http: HttpClient) {}

  getProducts(page: number = 0): Observable<any> {
    return this.http.get(`${this.apiUrl}?page=${page}`);
  }

  getProductDetails(sku: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/sku/${sku}`);
  }
}
```

Ver documentación completa en: [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md)

---

## 🔍 Monitoreo

### Circuit Breaker Status

```bash
curl http://localhost:8080/actuator/circuitbreakers
```

### Metrics

```bash
curl http://localhost:8080/actuator/metrics
```

---

## ⚠️ Troubleshooting

### Problema: Servicio no se registra en Eureka

**Solución:**
1. Verificar que Eureka Server esté corriendo
2. Esperar 30 segundos (lease renewal interval)
3. Revisar logs del servicio

### Problema: CORS Error desde Angular

**Solución:**
El API Gateway ya está configurado para permitir peticiones desde `http://localhost:4200`.

Si usas otro puerto, edita:
```java
// api-gateway/src/main/java/.../config/CorsConfig.java
corsConfig.setAllowedOrigins(List.of("http://localhost:TU_PUERTO"));
```

### Problema: Circuit Breaker se abre

**Solución:**
1. Verificar que el servicio destino esté corriendo
2. Revisar logs del API Gateway
3. Esperar 10 segundos para que el circuit breaker se cierre

---

## 📚 Documentación Adicional

- [README.md](README.md) - Visión general del proyecto
- [ARQUITECTURA_MICROSERVICIOS.md](ARQUITECTURA_MICROSERVICIOS.md) - Arquitectura detallada
- [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md) - Guía de integración con Angular
- [GUIA_COMPLETA_KAFKA_SMTP.md](GUIA_COMPLETA_KAFKA_SMTP.md) - Configuración de Kafka y SMTP

---

## 🎯 Próximos Pasos

1. ✅ **Iniciar Kafka** (opcional):
   ```powershell
   .\start-kafka.ps1
   .\create-kafka-topic.ps1
   ```

2. ✅ **Configurar SMTP** (opcional):
   Editar `notifications_service/src/main/resources/application.yml`

3. ✅ **Integrar con Angular**:
   Seguir la guía en [INTEGRACION_ANGULAR_BACKEND.md](INTEGRACION_ANGULAR_BACKEND.md)

4. ✅ **Probar el flujo completo**:
   - Listar productos (6 por página)
   - Ver detalles de un producto
   - Crear una orden (botón "Comprar")
   - Verificar email de confirmación

---

**¡Todo listo para integrar con Angular! 🚀**

