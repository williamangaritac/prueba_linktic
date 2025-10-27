# 📊 Guía para Ver Logs Detallados de los Microservicios

## 🎯 Objetivo

Ver en consola cómo trabaja cada proceso interno de Spring Boot:
- Consultas SQL
- Peticiones HTTP
- Eventos de Kafka
- Registro en Eureka
- Transacciones
- Y mucho más...

---

## 🚀 Opción 1: Logs en Ventanas Separadas (Recomendado)

### Paso 1: Ejecutar el Script

```powershell
.\start-with-debug-logs.ps1
```

Este script:
- ✅ Abre 5 ventanas de PowerShell (una por servicio)
- ✅ Cada ventana muestra logs en tiempo real
- ✅ Logs con nivel DEBUG (muy detallados)
- ✅ Cada ventana tiene un color diferente

### Paso 2: Observar las Ventanas

Deberías ver 5 ventanas:

| Ventana | Servicio | Puerto | Color | Información que verás |
|---------|----------|--------|-------|----------------------|
| 1 | Eureka Server | 8761 | Magenta | Registro de servicios, heartbeats |
| 2 | Products Service | 8081 | Verde | SQL queries, HTTP requests |
| 3 | Inventory Service | 8082 | Azul | Validación de stock, updates |
| 4 | Orders Service | 8083 | Amarillo | Creación de órdenes, Kafka events |
| 5 | Notifications Service | 8084 | Cyan | Kafka consumer, emails |

### Paso 3: Probar Endpoints

En una nueva terminal, ejecuta:

```powershell
.\probar-endpoints.ps1
```

**Observa las ventanas de logs** y verás en tiempo real:
- Products Service: Consulta SQL para obtener productos
- Orders Service: Llamadas a Products e Inventory
- Kafka: Publicación y consumo de eventos
- Notifications Service: Procesamiento de emails

---

## 🚀 Opción 2: Logs en Terminal Individual

Si prefieres ver los logs de un solo servicio en tu terminal actual:

### Eureka Server
```powershell
.\start-consolidated-logs.ps1 -Service eureka
```

### Products Service
```powershell
.\start-consolidated-logs.ps1 -Service products
```

### Inventory Service
```powershell
.\start-consolidated-logs.ps1 -Service inventory
```

### Orders Service
```powershell
.\start-consolidated-logs.ps1 -Service orders
```

### Notifications Service
```powershell
.\start-consolidated-logs.ps1 -Service notifications
```

---

## 📊 Qué Verás en los Logs

### 🔍 EUREKA SERVER (8761)

```
08:30:15.123 [EUREKA] [main] INFO  c.n.e.EurekaServerBootstrap - Eureka Server started
08:30:25.456 [EUREKA] [Eureka-EvictionTimer] DEBUG c.n.e.registry.PeerAwareInstanceRegistryImpl - Running eviction task
08:30:35.789 [EUREKA] [ReplicaAwareInstanceRegistry] INFO  c.n.e.registry - Registered instance PRODUCTS-SERVICE/products_service:8081
```

**Información clave:**
- Inicio del servidor
- Registro de nuevos servicios
- Heartbeats cada 10 segundos
- Renovación de leases

---

### 🔍 PRODUCTS SERVICE (8081)

```
08:31:00.123 [PRODUCTS] [http-nio-8081-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - GET "/api/v1/frontend/products?page=0"
08:31:00.234 [PRODUCTS] [http-nio-8081-exec-1] DEBUG org.hibernate.SQL - 
    select
        p1_0.id,
        p1_0.created_at,
        p1_0.description,
        p1_0.name,
        p1_0.price,
        p1_0.sku,
        p1_0.status,
        p1_0.updated_at 
    from
        products p1_0 
    limit
        ?, ?
08:31:00.345 [PRODUCTS] [http-nio-8081-exec-1] TRACE o.h.type.descriptor.sql.BasicBinder - binding parameter [1] as [INTEGER] - [0]
08:31:00.456 [PRODUCTS] [http-nio-8081-exec-1] TRACE o.h.type.descriptor.sql.BasicBinder - binding parameter [2] as [INTEGER] - [6]
08:31:00.567 [PRODUCTS] [http-nio-8081-exec-1] DEBUG c.l.p.services.ProductService - Found 6 products for page 0
08:31:00.678 [PRODUCTS] [http-nio-8081-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - Completed 200 OK
```

**Información clave:**
- Peticiones HTTP recibidas
- Consultas SQL ejecutadas
- Parámetros de las queries
- Resultados obtenidos
- Tiempo de respuesta

---

### 🔍 INVENTORY SERVICE (8082)

```
08:32:00.123 [INVENTORY] [http-nio-8082-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - POST "/api/v1/inventory/validate"
08:32:00.234 [INVENTORY] [http-nio-8082-exec-1] DEBUG c.l.i.services.InventoryService - Validating stock for SKU: 29444ed7a8f8495587365a6b61458735
08:32:00.345 [INVENTORY] [http-nio-8082-exec-1] DEBUG org.hibernate.SQL - 
    select
        i1_0.id,
        i1_0.quantity,
        i1_0.sku 
    from
        inventory i1_0 
    where
        i1_0.sku=?
08:32:00.456 [INVENTORY] [http-nio-8082-exec-1] DEBUG c.l.i.services.InventoryService - Stock available: 100 units
08:32:00.567 [INVENTORY] [http-nio-8082-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - Completed 200 OK
```

**Información clave:**
- Validación de stock
- Consultas de inventario
- Cantidad disponible
- Actualizaciones de stock

---

### 🔍 ORDERS SERVICE (8083)

```
08:33:00.123 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - POST "/api/v1/frontend/orders/purchase"
08:33:00.234 [ORDERS] [http-nio-8083-exec-1] DEBUG c.l.o.services.OrderService - Creating order with 1 items
08:33:00.345 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.client.RestTemplate - HTTP GET http://localhost:8081/api/v1/products/sku/29444ed7a8f8495587365a6b61458735
08:33:00.456 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.client.RestTemplate - Response 200 OK
08:33:00.567 [ORDERS] [http-nio-8083-exec-1] DEBUG c.l.o.services.OrderService - Product name: Solucion E-commerce
08:33:00.678 [ORDERS] [http-nio-8083-exec-1] DEBUG org.hibernate.SQL - 
    insert 
    into
        orders
        (created_at, order_number, status, total, updated_at) 
    values
        (?, ?, ?, ?, ?)
08:33:00.789 [ORDERS] [http-nio-8083-exec-1] DEBUG c.l.o.services.OrderService - Order created with ID: 13
08:33:00.890 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.kafka.core.KafkaTemplate - Sending message to topic: order-events
08:33:00.901 [ORDERS] [http-nio-8083-exec-1] DEBUG c.l.o.services.OrderService - Kafka event published successfully
08:33:00.912 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - Completed 201 CREATED
```

**Información clave:**
- Recepción de petición de compra
- Llamada a Products Service (RestTemplate)
- Validación de inventario
- Inserción en MySQL
- Publicación de evento en Kafka
- Respuesta al cliente

---

### 🔍 NOTIFICATIONS SERVICE (8084)

```
08:33:01.123 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG o.s.kafka.listener.KafkaMessageListenerContainer - Received: ConsumerRecord(topic = order-events, partition = 0, offset = 42)
08:33:01.234 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG c.l.n.listeners.OrderEventListener - Processing order event: ORD-20251026-XXXXXXXX
08:33:01.345 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG c.l.n.services.EmailService - Preparing email for: william.angaritac@gmail.com
08:33:01.456 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG o.s.mail.javamail.JavaMailSenderImpl - Creating new JavaMail Session
08:33:01.567 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG c.l.n.services.EmailService - Email sent successfully
08:33:01.678 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG org.hibernate.SQL - 
    insert 
    into
        notifications
        (created_at, email, message, order_number, status, type) 
    values
        (?, ?, ?, ?, ?, ?)
08:33:01.789 [NOTIFICATIONS] [org.springframework.kafka.KafkaListenerEndpointContainer#0-0-C-1] DEBUG c.l.n.services.NotificationService - Notification saved with ID: 25
```

**Información clave:**
- Consumo de evento de Kafka
- Preparación de email
- Conexión SMTP
- Envío de email
- Guardado en PostgreSQL

---

## 🎬 Flujo Completo de una Compra

### Paso 1: Ejecutar el script de logs
```powershell
.\start-with-debug-logs.ps1
```

### Paso 2: Esperar a que todos los servicios inicien (60 segundos)

### Paso 3: En una nueva terminal, crear una orden
```powershell
curl -X POST http://localhost:8083/api/v1/frontend/orders/purchase `
  -H "Content-Type: application/json" `
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

### Paso 4: Observar las ventanas de logs

Verás el flujo completo:

1. **Orders Service (Amarillo):**
   - Recibe POST /api/v1/frontend/orders/purchase
   - Llama a Products Service para obtener nombre del producto
   - Valida inventario
   - Inserta orden en MySQL
   - Publica evento en Kafka

2. **Products Service (Verde):**
   - Recibe GET /api/v1/products/sku/{sku}
   - Ejecuta SELECT en PostgreSQL
   - Retorna datos del producto

3. **Inventory Service (Azul):**
   - Recibe validación de stock
   - Consulta inventario
   - Confirma disponibilidad

4. **Notifications Service (Cyan):**
   - Consume evento de Kafka
   - Prepara email
   - Envía email (si SMTP configurado)
   - Guarda notificación en PostgreSQL

---

## 💡 Consejos

### Ver solo logs de SQL
```powershell
# En la ventana del servicio, busca líneas que contengan "org.hibernate.SQL"
```

### Ver solo peticiones HTTP
```powershell
# Busca líneas que contengan "DispatcherServlet" o "RestTemplate"
```

### Ver solo eventos de Kafka
```powershell
# Busca líneas que contengan "kafka" o "KafkaTemplate"
```

### Detener todos los servicios
```powershell
Stop-Process -Name java -Force
```

---

## 📚 Scripts Disponibles

| Script | Descripción |
|--------|-------------|
| `start-with-logs.ps1` | Inicia servicios con logs INFO en ventanas separadas |
| `start-with-debug-logs.ps1` | Inicia servicios con logs DEBUG (muy detallados) |
| `start-consolidated-logs.ps1` | Inicia un servicio individual con logs en la terminal actual |
| `verificar-servicios.ps1` | Verifica estado de todos los servicios |
| `probar-endpoints.ps1` | Prueba los endpoints principales |

---

## 🔍 Niveles de Logs

| Nivel | Descripción | Uso |
|-------|-------------|-----|
| **TRACE** | Información muy detallada (parámetros de SQL) | Debugging profundo |
| **DEBUG** | Información de debugging (queries, requests) | Desarrollo |
| **INFO** | Información general (inicio, registro) | Producción |
| **WARN** | Advertencias | Producción |
| **ERROR** | Errores | Producción |

---

**¡Ahora puedes ver exactamente cómo trabaja cada proceso interno de Spring Boot! 🚀**

