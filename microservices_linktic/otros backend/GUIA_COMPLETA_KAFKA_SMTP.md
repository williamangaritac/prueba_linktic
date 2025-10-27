# 🚀 Guía Completa: Configuración de Kafka y SMTP

## 📋 Tabla de Contenidos
1. [Instalación de Kafka](#1-instalación-de-kafka)
2. [Configuración de SMTP (Gmail)](#2-configuración-de-smtp-gmail)
3. [Iniciar los Microservicios](#3-iniciar-los-microservicios)
4. [Probar la Integración Completa](#4-probar-la-integración-completa)

---

## 1. Instalación de Kafka

### Opción A: Instalación Manual

#### Paso 1: Descargar Kafka
1. Ve a https://kafka.apache.org/downloads
2. Descarga **kafka_2.13-3.9.0.tgz** (o la versión más reciente)
3. Extrae el archivo en `C:\kafka\`
4. La ruta final debe ser: `C:\kafka\kafka_2.13-3.9.0`

#### Paso 2: Iniciar Kafka
Abre PowerShell como Administrador y ejecuta:

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic
.\start-kafka.ps1
```

Este script:
- ✅ Verifica si Kafka está instalado
- ✅ Genera un UUID para el cluster
- ✅ Formatea el directorio de logs
- ✅ Inicia Kafka con KRaft (sin Zookeeper)

**IMPORTANTE:** Deja esta ventana abierta. Kafka debe estar corriendo todo el tiempo.

#### Paso 3: Crear el Topic "order-events"
Abre una **NUEVA** ventana de PowerShell y ejecuta:

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic
.\create-kafka-topic.ps1
```

Este script:
- ✅ Verifica que Kafka esté corriendo
- ✅ Crea el topic `order-events`
- ✅ Muestra los detalles del topic

#### Paso 4: Verificar que Kafka está corriendo
```powershell
netstat -an | findstr :9092
```

Deberías ver:
```
TCP    0.0.0.0:9092           0.0.0.0:0              LISTENING
```

---

### Opción B: Usar Docker (Alternativa)

Si prefieres usar Docker:

```powershell
# Crear archivo docker-compose.yml
docker-compose up -d
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  kafka:
    image: confluentinc/cp-kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_NODE_ID: 1
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
```

---

## 2. Configuración de SMTP (Gmail)

### Paso 1: Crear una Contraseña de Aplicación en Gmail

1. **Ir a tu cuenta de Google:**
   - Ve a https://myaccount.google.com/

2. **Activar la verificación en 2 pasos:**
   - Ve a **Seguridad** → **Verificación en 2 pasos**
   - Actívala si no está activada

3. **Crear contraseña de aplicación:**
   - Ve a **Seguridad** → **Contraseñas de aplicaciones**
   - Selecciona **Correo** y **Otro (nombre personalizado)**
   - Escribe: "Notifications Service"
   - Click en **Generar**
   - **GUARDA LA CONTRASEÑA** (16 caracteres, ej: `abcd efgh ijkl mnop`)

### Paso 2: Configurar notifications_service

Edita el archivo: `notifications_service/src/main/resources/application.yml`

Busca la sección `spring.mail` y actualiza:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: contacto@linktic.com  # Reemplaza con tu email de Gmail
    password: abcdefghijklmnop      # Reemplaza con la contraseña de aplicación (sin espacios)
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
    test-connection: false
```

**IMPORTANTE:** 
- Usa tu email real de Gmail en `username`
- Usa la contraseña de aplicación (16 caracteres sin espacios) en `password`
- NO uses tu contraseña normal de Gmail

### Paso 3: Habilitar el envío de emails

En el mismo archivo `application.yml`, busca:

```yaml
app:
  email:
    from: contacto@linktic.com  # Tu email de Gmail
    to: contacto@linktic.com    # Email donde recibirás las notificaciones
    enabled: true               # Cambiar a true para habilitar envío
```

---

## 3. Iniciar los Microservicios

### Orden de Inicio Recomendado:

#### 1️⃣ **Kafka** (debe estar corriendo primero)
```powershell
# Terminal 1
.\start-kafka.ps1
```

#### 2️⃣ **products_service** (Puerto 8081)
```powershell
# Terminal 2
cd products_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean package -DskipTests
java -jar target/products_service-0.0.1-SNAPSHOT.jar
```

#### 3️⃣ **inventory_service** (Puerto 8082)
```powershell
# Terminal 3
cd inventory_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean package -DskipTests
java -jar target/inventory_service-0.0.1-SNAPSHOT.jar
```

#### 4️⃣ **orders_service** (Puerto 8083)
```powershell
# Terminal 4
cd orders_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean package -DskipTests
java -jar target/orders_service-0.0.1-SNAPSHOT.jar
```

#### 5️⃣ **notifications_service** (Puerto 8084)
```powershell
# Terminal 5
cd notifications_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\mvnw.cmd clean package -DskipTests
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

### Verificar que todos estén corriendo:
```powershell
netstat -an | findstr "8081 8082 8083 8084 9092"
```

Deberías ver:
```
TCP    0.0.0.0:8081           0.0.0.0:0              LISTENING
TCP    0.0.0.0:8082           0.0.0.0:0              LISTENING
TCP    0.0.0.0:8083           0.0.0.0:0              LISTENING
TCP    0.0.0.0:8084           0.0.0.0:0              LISTENING
TCP    0.0.0.0:9092           0.0.0.0:0              LISTENING
```

---

## 4. Probar la Integración Completa

### Flujo Completo:
```
Cliente → orders_service → Kafka → notifications_service → Email
```

### Paso 1: Abrir Swagger de orders_service
Abre en tu navegador:
```
http://localhost:8083/api/v1/swagger-ui.html
```

### Paso 2: Crear una Orden de Prueba

Busca el endpoint **POST /api/v1/orders** y usa este JSON:

```json
{
  "orderItems": [
    {
      "sku": "29444ed7a8f8495587365a6b61458735",
      "price": 2805.00,
      "quantity": 1
    },
    {
      "sku": "8b5c9d2e1f4a6b3c7d8e9f0a1b2c3d4e",
      "price": 2195.00,
      "quantity": 1
    }
  ]
}
```

### Paso 3: Verificar el Flujo

#### A. En la consola de orders_service (Terminal 4):
Deberías ver:
```
Order created successfully with number: ORD-20251025-XXXXXXXX
Order event emitted to Kafka: {"orderId":13,"orderNumber":"ORD-20251025-XXXXXXXX",...}
```

#### B. En la consola de notifications_service (Terminal 5):
Deberías ver:
```
Received order event from Kafka: {"orderId":13,"orderNumber":"ORD-20251025-XXXXXXXX",...}
Successfully processed order event for order: ORD-20251025-XXXXXXXX
Email sent successfully for order: ORD-20251025-XXXXXXXX
```

#### C. En tu email (Gmail):
Deberías recibir un email con:
- **Asunto:** Confirmación de Orden ORD-20251025-XXXXXXXX
- **Contenido:** Detalles de la orden con productos y precios

### Paso 4: Verificar en la Base de Datos

#### PostgreSQL (notifications):
```sql
SELECT * FROM notifications ORDER BY created_at DESC LIMIT 1;
```

Deberías ver la nueva notificación con `status = 'SENT'`.

#### MySQL (orders):
```sql
USE linktic_orders;
SELECT * FROM orders ORDER BY id DESC LIMIT 1;
```

Deberías ver la nueva orden creada.

---

## 🔍 Solución de Problemas

### Kafka no inicia
```powershell
# Verificar si el puerto está ocupado
netstat -an | findstr :9092

# Si está ocupado, matar el proceso
taskkill /F /IM java.exe

# Reiniciar Kafka
.\start-kafka.ps1
```

### Email no se envía
1. Verifica que la contraseña de aplicación sea correcta (16 caracteres sin espacios)
2. Verifica que `app.email.enabled: true` en application.yml
3. Revisa los logs de notifications_service para ver errores SMTP

### Microservicio no inicia
```powershell
# Matar todos los procesos Java
taskkill /F /IM java.exe

# Recompilar
.\mvnw.cmd clean package -DskipTests

# Reiniciar
java -jar target/NOMBRE_DEL_JAR.jar
```

### Kafka no recibe eventos
1. Verifica que el topic exista:
```powershell
cd C:\kafka\kafka_2.13-3.9.0
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

2. Monitorear mensajes en Kafka:
```powershell
.\bin\windows\kafka-console-consumer.bat --topic order-events --from-beginning --bootstrap-server localhost:9092
```

---

## ✅ Checklist Final

- [ ] Kafka instalado en `C:\kafka\kafka_2.13-3.9.0`
- [ ] Kafka corriendo en puerto 9092
- [ ] Topic `order-events` creado
- [ ] Contraseña de aplicación de Gmail generada
- [ ] `application.yml` de notifications_service configurado
- [ ] Los 4 microservicios corriendo (8081, 8082, 8083, 8084)
- [ ] Orden de prueba creada exitosamente
- [ ] Email recibido en Gmail
- [ ] Notificación guardada en PostgreSQL

---

¡Listo! Ahora tienes un sistema completo de microservicios con comunicación asíncrona vía Kafka y notificaciones por email. 🎉

