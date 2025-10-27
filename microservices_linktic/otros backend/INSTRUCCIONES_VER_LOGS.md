# 📊 Instrucciones para Ver Logs Detallados de Spring Boot

## 🎯 Cómo Ver los Logs de Cada Proceso

Para ver cómo trabaja cada proceso interno de Spring Boot, necesitas abrir **5 terminales de PowerShell** (una por cada servicio).

---

## 🚀 Método Recomendado: Terminales Manuales

### Terminal 1: Eureka Server (Puerto 8761)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar `
  --logging.level.root=INFO `
  --logging.level.com.netflix.eureka=DEBUG `
  --logging.level.com.netflix.discovery=DEBUG `
  --logging.pattern.console="%d{HH:mm:ss.SSS} [%highlight(%-5level)] %cyan([%thread]) %yellow(%logger{36}) - %msg%n"
```

**Espera 30 segundos** antes de iniciar el siguiente servicio.

---

### Terminal 2: Products Service (Puerto 8081)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

java -jar products_service\target\products_service-0.0.1-SNAPSHOT.jar `
  --logging.level.root=INFO `
  --logging.level.com.linktic_test.products_service=DEBUG `
  --logging.level.org.springframework.web=DEBUG `
  --logging.level.org.hibernate.SQL=DEBUG `
  --logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE `
  --logging.pattern.console="%d{HH:mm:ss.SSS} [PRODUCTS] [%thread] %-5level %logger{36} - %msg%n"
```

---

### Terminal 3: Inventory Service (Puerto 8082)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

java -jar inventory_service\target\inventory_service-0.0.1-SNAPSHOT.jar `
  --logging.level.root=INFO `
  --logging.level.com.linktic_test.inventory_service=DEBUG `
  --logging.level.org.springframework.web=DEBUG `
  --logging.level.org.hibernate.SQL=DEBUG `
  --logging.pattern.console="%d{HH:mm:ss.SSS} [INVENTORY] [%thread] %-5level %logger{36} - %msg%n"
```

---

### Terminal 4: Orders Service (Puerto 8083)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

java -jar orders_service\target\orders_service-0.0.1-SNAPSHOT.jar `
  --logging.level.root=INFO `
  --logging.level.com.linktic_test.orders_service=DEBUG `
  --logging.level.org.springframework.web=DEBUG `
  --logging.level.org.springframework.kafka=DEBUG `
  --logging.level.org.hibernate.SQL=DEBUG `
  --logging.pattern.console="%d{HH:mm:ss.SSS} [ORDERS] [%thread] %-5level %logger{36} - %msg%n"
```

---

### Terminal 5: Notifications Service (Puerto 8084)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

java -jar notifications_service\target\notifications_service-0.0.1-SNAPSHOT.jar `
  --logging.level.root=INFO `
  --logging.level.com.linktic_test.notifications_service=DEBUG `
  --logging.level.org.springframework.kafka=DEBUG `
  --logging.level.org.springframework.mail=DEBUG `
  --logging.pattern.console="%d{HH:mm:ss.SSS} [NOTIFICATIONS] [%thread] %-5level %logger{36} - %msg%n"
```

---

## 📊 Qué Verás en Cada Terminal

### 🟣 Terminal 1: EUREKA SERVER

```
08:30:15.123 [INFO ] [main] c.n.e.EurekaServerBootstrap - Eureka Server started
08:30:25.456 [DEBUG] [Eureka-EvictionTimer] c.n.e.registry.PeerAwareInstanceRegistryImpl - Running eviction task
08:30:35.789 [INFO ] [ReplicaAwareInstanceRegistry] c.n.e.registry - Registered instance PRODUCTS-SERVICE
```

### 🟢 Terminal 2: PRODUCTS SERVICE

```
08:31:00.123 [PRODUCTS] [http-nio-8081-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - GET "/api/v1/frontend/products?page=0"
08:31:00.234 [PRODUCTS] [http-nio-8081-exec-1] DEBUG org.hibernate.SQL - 
    select p1_0.id, p1_0.name, p1_0.price, p1_0.sku from products p1_0 limit ?, ?
08:31:00.345 [PRODUCTS] [http-nio-8081-exec-1] TRACE o.h.type.descriptor.sql.BasicBinder - binding parameter [1] as [INTEGER] - [0]
08:31:00.456 [PRODUCTS] [http-nio-8081-exec-1] TRACE o.h.type.descriptor.sql.BasicBinder - binding parameter [2] as [INTEGER] - [6]
08:31:00.567 [PRODUCTS] [http-nio-8081-exec-1] DEBUG c.l.p.services.ProductService - Found 6 products for page 0
```

### 🔵 Terminal 3: INVENTORY SERVICE

```
08:32:00.123 [INVENTORY] [http-nio-8082-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - POST "/api/v1/inventory/validate"
08:32:00.234 [INVENTORY] [http-nio-8082-exec-1] DEBUG c.l.i.services.InventoryService - Validating stock for SKU: 29444ed7...
08:32:00.345 [INVENTORY] [http-nio-8082-exec-1] DEBUG org.hibernate.SQL - 
    select i1_0.id, i1_0.quantity from inventory i1_0 where i1_0.sku=?
08:32:00.456 [INVENTORY] [http-nio-8082-exec-1] DEBUG c.l.i.services.InventoryService - Stock available: 100 units
```

### 🟡 Terminal 4: ORDERS SERVICE

```
08:33:00.123 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.servlet.DispatcherServlet - POST "/api/v1/frontend/orders/purchase"
08:33:00.234 [ORDERS] [http-nio-8083-exec-1] DEBUG c.l.o.services.OrderService - Creating order with 1 items
08:33:00.345 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.client.RestTemplate - HTTP GET http://localhost:8081/api/v1/products/sku/...
08:33:00.456 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.web.client.RestTemplate - Response 200 OK
08:33:00.567 [ORDERS] [http-nio-8083-exec-1] DEBUG org.hibernate.SQL - 
    insert into orders (order_number, total, status) values (?, ?, ?)
08:33:00.678 [ORDERS] [http-nio-8083-exec-1] DEBUG o.s.kafka.core.KafkaTemplate - Sending message to topic: order-events
```

### 🔷 Terminal 5: NOTIFICATIONS SERVICE

```
08:33:01.123 [NOTIFICATIONS] [kafka-listener-1] DEBUG o.s.kafka.listener - Received: ConsumerRecord(topic = order-events)
08:33:01.234 [NOTIFICATIONS] [kafka-listener-1] DEBUG c.l.n.listeners.OrderEventListener - Processing order event
08:33:01.345 [NOTIFICATIONS] [kafka-listener-1] DEBUG c.l.n.services.EmailService - Preparing email for: william.angaritac@gmail.com
08:33:01.456 [NOTIFICATIONS] [kafka-listener-1] DEBUG o.s.mail.javamail.JavaMailSenderImpl - Creating JavaMail Session
08:33:01.567 [NOTIFICATIONS] [kafka-listener-1] DEBUG org.hibernate.SQL - 
    insert into notifications (email, message, status) values (?, ?, ?)
```

---

## 🧪 Probar el Flujo Completo

### Paso 1: Abrir 5 Terminales

Abre 5 ventanas de PowerShell y ejecuta cada comando en una terminal diferente.

### Paso 2: Esperar a que Todos Inicien

Espera aproximadamente 60 segundos hasta que veas en cada terminal:
```
Started [ServiceName]Application in X.XXX seconds
```

### Paso 3: Abrir una 6ta Terminal para Probar

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic

# Probar listar productos
curl http://localhost:8081/api/v1/frontend/products?page=0
```

### Paso 4: Observar las Terminales

Verás en **Terminal 2 (Products)**:
- La petición HTTP GET
- La consulta SQL ejecutada
- Los parámetros (page=0, size=6)
- El resultado (6 productos)

### Paso 5: Crear una Orden

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

### Paso 6: Observar el Flujo en las 5 Terminales

1. **Terminal 4 (Orders):** Recibe la petición POST
2. **Terminal 2 (Products):** Recibe llamada para obtener nombre del producto
3. **Terminal 3 (Inventory):** Valida el stock disponible
4. **Terminal 4 (Orders):** Inserta la orden en MySQL y publica evento en Kafka
5. **Terminal 5 (Notifications):** Consume el evento y envía email

---

## 💡 Consejos

### Organizar las Ventanas

Organiza las 5 terminales en tu pantalla para verlas todas al mismo tiempo:

```
┌─────────────┬─────────────┐
│   EUREKA    │  PRODUCTS   │
│   (8761)    │   (8081)    │
├─────────────┼─────────────┤
│ INVENTORY   │   ORDERS    │
│   (8082)    │   (8083)    │
├─────────────┴─────────────┤
│     NOTIFICATIONS         │
│         (8084)            │
└───────────────────────────┘
```

### Detener Todos los Servicios

En cualquier terminal:
```powershell
Stop-Process -Name java -Force
```

O presiona `Ctrl+C` en cada terminal.

---

## 📚 Documentación Adicional

- [GUIA_LOGS_DETALLADOS.md](GUIA_LOGS_DETALLADOS.md) - Guía completa de logs
- [BACKEND_DESPLEGADO.md](BACKEND_DESPLEGADO.md) - Estado del backend
- [ARQUITECTURA_MICROSERVICIOS.md](ARQUITECTURA_MICROSERVICIOS.md) - Arquitectura del sistema

---

**¡Ahora puedes ver exactamente cómo trabaja cada proceso interno! 🚀**

