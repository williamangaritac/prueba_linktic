# 📊 Instrucciones para Crear Base de Datos de Notifications Service

## 🎯 Archivos Creados

Se han creado **2 scripts SQL** para diferentes bases de datos:

1. **`notifications_service_postgresql.sql`** - Para PostgreSQL
2. **`notifications_service_mysql.sql`** - Para MySQL

## 📝 Contenido de los Scripts

Cada script incluye:

✅ **Creación de la base de datos** `linktic_notifications`  
✅ **Creación de la tabla** `notifications` con todos los campos del modelo  
✅ **Índices** para optimizar consultas  
✅ **12 registros de prueba** basados en las órdenes de `orders_service`:
   - 10 notificaciones con estado `SENT` (enviadas exitosamente)
   - 1 notificación con estado `PENDING` (pendiente de envío)
   - 1 notificación con estado `FAILED` (falló el envío)

---

## 🐘 OPCIÓN 1: PostgreSQL

### Paso 1: Abrir pgAdmin o psql

#### Usando pgAdmin:
1. Abre **pgAdmin**
2. Conéctate a tu servidor PostgreSQL
3. Click derecho en **Databases** → **Query Tool**
4. Copia y pega el contenido de `notifications_service_postgresql.sql`
5. Click en el botón **Execute** (▶️)

#### Usando psql (línea de comandos):
```bash
psql -U postgres -f notifications_service_postgresql.sql
```

### Paso 2: Verificar la creación

```sql
-- Conectar a la base de datos
\c linktic_notifications

-- Ver la estructura de la tabla
\d notifications

-- Ver los datos insertados
SELECT id, order_number, status, type, created_at 
FROM notifications 
ORDER BY created_at DESC;
```

### Paso 3: Actualizar application.yml (si quieres usar PostgreSQL)

Edita `notifications_service/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/linktic_notifications
    username: postgres
    password: tu_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # Cambiar de 'update' a 'validate'
    show-sql: true
```

Y agrega la dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🐬 OPCIÓN 2: MySQL (Recomendado - igual que orders_service)

### Paso 1: Abrir phpMyAdmin o MySQL Workbench

#### Usando phpMyAdmin:
1. Abre **phpMyAdmin** en tu navegador (http://localhost/phpmyadmin)
2. Click en la pestaña **SQL**
3. Copia y pega el contenido de `notifications_service_mysql.sql`
4. Click en el botón **Go** o **Continuar**

#### Usando MySQL Workbench:
1. Abre **MySQL Workbench**
2. Conéctate a tu servidor MySQL
3. Abre un nuevo **Query Tab**
4. Copia y pega el contenido de `notifications_service_mysql.sql`
5. Click en el botón **Execute** (⚡)

#### Usando mysql (línea de comandos):
```bash
mysql -u root -p < notifications_service_mysql.sql
```

### Paso 2: Verificar la creación

```sql
-- Usar la base de datos
USE linktic_notifications;

-- Ver la estructura de la tabla
DESCRIBE notifications;

-- Ver los datos insertados
SELECT id, order_number, status, type, created_at 
FROM notifications 
ORDER BY created_at DESC;

-- Ver estadísticas por estado
SELECT status, COUNT(*) as total 
FROM notifications 
GROUP BY status;
```

### Paso 3: Actualizar application.yml (para usar MySQL)

Edita `notifications_service/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/linktic_notifications?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: tu_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: validate  # Cambiar de 'update' a 'validate'
    show-sql: true
```

Y agrega la dependencia en `pom.xml`:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 🔄 Recompilar y Reiniciar el Microservicio

Después de cambiar la configuración:

### 1. Detener el servicio actual
```powershell
# Buscar el proceso Java
tasklist | findstr java

# Matar el proceso (reemplaza PID con el número del proceso)
taskkill /F /PID <PID>

# O matar todos los procesos Java
taskkill /F /IM java.exe
```

### 2. Recompilar
```powershell
cd notifications_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean package -DskipTests
```

### 3. Iniciar el servicio
```powershell
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

---

## 📊 Estructura de la Tabla `notifications`

| Campo | Tipo | Descripción |
|-------|------|-------------|
| **id** | BIGINT | ID único (auto-incremental) |
| **order_number** | VARCHAR(50) | Número de orden (ej: ORD-2024-001) |
| **order_id** | BIGINT | ID de la orden |
| **recipient_email** | VARCHAR(255) | Email del destinatario |
| **subject** | VARCHAR(500) | Asunto del email |
| **message** | TEXT | Cuerpo del mensaje |
| **status** | VARCHAR(20) | Estado: PENDING, SENT, FAILED |
| **type** | VARCHAR(50) | Tipo: ORDER_CREATED, ORDER_UPDATED, etc. |
| **sent_at** | TIMESTAMP | Fecha/hora de envío |
| **created_at** | TIMESTAMP | Fecha/hora de creación |
| **error_message** | VARCHAR(1000) | Mensaje de error (si falló) |

---

## 🧪 Datos de Prueba Incluidos

### Notificaciones ENVIADAS (10):
- ORD-2024-001 a ORD-2024-010
- Todas con estado `SENT`
- Fechas escalonadas de 10 días atrás hasta 1 día atrás

### Notificación PENDIENTE (1):
- ORD-2024-011
- Estado `PENDING`
- Creada hoy

### Notificación FALLIDA (1):
- ORD-2024-012
- Estado `FAILED`
- Con mensaje de error: "SMTP server connection timeout"

---

## ✅ Verificación Final

Una vez que el servicio esté corriendo con la nueva base de datos:

### 1. Verificar Swagger UI
http://localhost:8084/api/v1/swagger-ui.html

### 2. Probar endpoints:

**Obtener todas las notificaciones:**
```
GET http://localhost:8084/api/v1/notifications
```

**Obtener notificaciones por orden:**
```
GET http://localhost:8084/api/v1/notifications/order/ORD-2024-001
```

**Obtener notificaciones enviadas:**
```
GET http://localhost:8084/api/v1/notifications/status/SENT
```

**Obtener notificaciones pendientes:**
```
GET http://localhost:8084/api/v1/notifications/status/PENDING
```

**Obtener notificaciones fallidas:**
```
GET http://localhost:8084/api/v1/notifications/status/FAILED
```

---

## 🎯 Resumen de Microservicios

| Microservicio | Puerto | Base de Datos | DB Name |
|---------------|--------|---------------|---------|
| products_service | 8081 | PostgreSQL | linktic_products |
| inventory_service | 8082 | PostgreSQL | linktic_inventory |
| orders_service | 8083 | MySQL | linktic_orders |
| **notifications_service** | **8084** | **PostgreSQL o MySQL** | **linktic_notifications** |

---

## 💡 Notas Importantes

1. **H2 vs PostgreSQL/MySQL**: Por defecto, el servicio usa H2 (en memoria). Si quieres persistencia real, usa PostgreSQL o MySQL.

2. **Consistencia**: Se recomienda usar **MySQL** para mantener consistencia con `orders_service`.

3. **Kafka**: Los scripts SQL son independientes de Kafka. Kafka solo se usa para recibir eventos en tiempo real.

4. **Email**: El envío de emails está deshabilitado por defecto (`app.email.enabled: false`). Los datos de prueba simulan emails ya enviados.

---

¡Listo! Ahora tienes una base de datos completa con datos de prueba para el microservicio de notificaciones. 🚀

