# Notifications Service

Microservicio de notificaciones para el sistema de e-commerce de LINKTIC. Este servicio escucha eventos de Kafka cuando se crean órdenes y envía notificaciones por email a los clientes.

## 🚀 Características

- **Kafka Consumer**: Escucha eventos del topic `order-events`
- **Email Notifications**: Envía correos electrónicos de confirmación de órdenes
- **Historial de Notificaciones**: Almacena todas las notificaciones enviadas en base de datos H2
- **API REST**: Endpoints para consultar el historial de notificaciones
- **Swagger UI**: Documentación interactiva de la API
- **Spring Boot 3.5.7**: Framework moderno y robusto
- **Java 17**: Última versión LTS de Java

## 📋 Requisitos Previos

- Java 17 (Eclipse Adoptium Temurin)
- Maven 3.6+
- Kafka (opcional, para recibir eventos reales)

## 🔧 Configuración

### Base de Datos
El servicio utiliza H2 (base de datos en memoria) por defecto. No requiere configuración adicional.

### Email
Por defecto, el envío de emails está configurado para modo desarrollo (simulado). Para habilitar el envío real de emails:

1. Editar `src/main/resources/application.yml`
2. Configurar las credenciales SMTP:
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: tu-email@gmail.com
    password: tu-password-de-aplicacion
```

3. Cambiar `app.email.enabled` a `true`

### Kafka
El servicio está configurado para conectarse a Kafka en `localhost:9092`. Si Kafka no está disponible, el servicio iniciará pero no recibirá eventos.

## 🏃 Ejecución

### Compilar el proyecto
```bash
mvnw clean package -DskipTests
```

### Ejecutar el microservicio
```bash
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

O usando Maven:
```bash
mvnw spring-boot:run
```

El servicio estará disponible en: **http://localhost:8084**

## 📚 Documentación API

### Swagger UI
Accede a la documentación interactiva en:
- **http://localhost:8084/api/v1/swagger-ui.html**

### Endpoints Principales

#### 1. Obtener todas las notificaciones
```
GET /api/v1/notifications
```

#### 2. Obtener notificaciones por número de orden
```
GET /api/v1/notifications/order/{orderNumber}
```
Ejemplo: `GET /api/v1/notifications/order/ORD-2024-001`

#### 3. Obtener notificaciones por estado
```
GET /api/v1/notifications/status/{status}
```
Estados válidos: `PENDING`, `SENT`, `FAILED`

Ejemplo: `GET /api/v1/notifications/status/SENT`

#### 4. Health Check
```
GET /api/v1/notifications/health
```

## 🔄 Integración con otros Microservicios

### Flujo de Notificaciones

1. **Cliente** → Crea una orden en `orders_service` (puerto 8083)
2. **orders_service** → Publica evento en Kafka topic `order-events`
3. **notifications_service** → Consume el evento de Kafka
4. **notifications_service** → Envía email de confirmación a `contacto@linktic.com`
5. **notifications_service** → Guarda registro de la notificación en base de datos

### Formato del Evento de Kafka

```json
{
  "orderId": 1,
  "orderNumber": "ORD-2024-001",
  "eventType": "ORDER_CREATED",
  "totalAmount": 5000.00,
  "items": [
    {
      "sku": "29444ed7a8f8495587365a6b61458735",
      "productName": "Solucion E-commerce",
      "price": 2805.00,
      "quantity": 1
    }
  ]
}
```

## 🗄️ Base de Datos H2

### Acceder a la consola H2
- **URL**: http://localhost:8084/api/v1/h2-console
- **JDBC URL**: `jdbc:h2:mem:notifications_db`
- **Username**: `sa`
- **Password**: (dejar vacío)

### Tabla: notifications

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | ID único de la notificación |
| order_number | VARCHAR | Número de la orden |
| order_id | BIGINT | ID de la orden |
| recipient_email | VARCHAR | Email del destinatario |
| subject | VARCHAR | Asunto del email |
| message | TEXT | Cuerpo del mensaje |
| status | VARCHAR | Estado: PENDING, SENT, FAILED |
| type | VARCHAR | Tipo: ORDER_CREATED, ORDER_UPDATED, etc. |
| sent_at | TIMESTAMP | Fecha/hora de envío |
| created_at | TIMESTAMP | Fecha/hora de creación |
| error_message | VARCHAR | Mensaje de error (si falló) |

## 🧪 Pruebas

### Probar el Consumer de Kafka (sin Kafka real)

Si no tienes Kafka corriendo, puedes probar el servicio consultando las notificaciones existentes a través de los endpoints REST.

### Probar con Kafka real

1. Asegúrate de que Kafka esté corriendo en `localhost:9092`
2. Crea una orden en `orders_service`
3. El evento se publicará automáticamente en Kafka
4. `notifications_service` lo consumirá y enviará el email
5. Consulta las notificaciones en: `GET /api/v1/notifications`

## 📊 Monitoreo

### Actuator Endpoints
- **Health**: http://localhost:8084/api/v1/actuator/health
- **Metrics**: http://localhost:8084/api/v1/actuator/metrics
- **Prometheus**: http://localhost:8084/api/v1/actuator/prometheus

## 🏗️ Arquitectura

```
notifications_service/
├── src/main/java/com/linktic_test/notifications_service/
│   ├── NotificationsServiceApplication.java
│   ├── config/
│   │   ├── JacksonConfig.java
│   │   ├── OpenApiConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── NotificationController.java
│   ├── kafka/
│   │   └── OrderEventConsumer.java
│   ├── model/
│   │   ├── dto/
│   │   │   ├── NotificationDTO.java
│   │   │   ├── OrderEventDTO.java
│   │   │   └── OrderItemDTO.java
│   │   └── entities/
│   │       ├── Notification.java
│   │       ├── NotificationStatus.java
│   │       └── NotificationType.java
│   ├── repository/
│   │   └── NotificationRepository.java
│   └── service/
│       ├── EmailService.java
│       └── NotificationService.java
└── src/main/resources/
    └── application.yml
```

## 🔐 Seguridad

Por defecto, todos los endpoints están abiertos para facilitar el desarrollo. En producción, se recomienda:

1. Habilitar autenticación básica
2. Configurar HTTPS
3. Restringir acceso a endpoints sensibles
4. Usar variables de entorno para credenciales

## 📝 Logs

Los logs están configurados en nivel DEBUG para facilitar el desarrollo. Puedes ver:
- Eventos recibidos de Kafka
- Emails enviados (o simulados)
- Errores en el procesamiento

## 🤝 Microservicios Relacionados

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| products_service | 8081 | Gestión de productos |
| inventory_service | 8082 | Gestión de inventario |
| orders_service | 8083 | Gestión de órdenes |
| **notifications_service** | **8084** | **Gestión de notificaciones** |

## 📧 Contacto

Email de notificaciones: **contacto@linktic.com**

---

**Desarrollado con ❤️ para LINKTIC**

