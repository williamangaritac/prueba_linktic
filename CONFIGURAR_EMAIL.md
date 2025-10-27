# 📧 Configuración de Notificaciones por Email

## 🎯 Objetivo

Configurar el sistema para que envíe notificaciones por email cuando se creen órdenes.

---

## ⚠️ Problema Actual

El servicio de notificaciones **NO puede enviar emails** porque falta la contraseña de aplicación de Gmail.

**Error**: `SPRING_MAIL_PASSWORD` no está configurado.

---

## ✅ Solución: Configurar Contraseña de Aplicación de Gmail

### Paso 1: Activar Verificación en 2 Pasos

1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. En el menú lateral, selecciona **"Seguridad"**
3. Busca la sección **"Verificación en 2 pasos"**
4. Si no está activada, haz clic en **"Empezar"** y sigue los pasos
5. Completa la configuración (te pedirá tu número de teléfono)

### Paso 2: Generar Contraseña de Aplicación

1. Una vez activada la verificación en 2 pasos, regresa a **"Seguridad"**
2. Busca **"Contraseñas de aplicaciones"** (aparece después de activar 2FA)
3. Haz clic en **"Contraseñas de aplicaciones"**
4. Es posible que te pida tu contraseña de Google nuevamente
5. En "Seleccionar app", elige **"Correo"**
6. En "Seleccionar dispositivo", elige **"Otro (nombre personalizado)"**
7. Escribe: **"Linktic E-Commerce"**
8. Haz clic en **"Generar"**
9. Google te mostrará una contraseña de **16 caracteres** (ejemplo: `abcd efgh ijkl mnop`)
10. **¡COPIA ESTA CONTRASEÑA!** (no podrás verla de nuevo)

### Paso 3: Configurar el Archivo .env

1. Abre el archivo `.env` en la raíz del proyecto
2. Pega la contraseña que copiaste (sin espacios):

```env
GMAIL_APP_PASSWORD=abcdefghijklmnop
```

**Ejemplo CORRECTO**:
```env
GMAIL_APP_PASSWORD=xyzw1234abcd5678
```

**Ejemplo INCORRECTO** (con espacios):
```env
GMAIL_APP_PASSWORD=xyzw 1234 abcd 5678
```

### Paso 4: Reiniciar los Contenedores

```bash
# Detener los contenedores
docker-compose down

# Volver a levantar (cargará la nueva configuración)
docker-compose up --build -d
```

---

## 🧪 Probar el Envío de Emails

### 1. Verificar que el servicio está corriendo

```bash
docker-compose logs notifications-service --tail=50
```

**Deberías ver**:
```
notifications-service  | Started NotificationsServiceApplication
```

**NO deberías ver**:
```
Failed to authenticate using SASL
Authentication failed
```

### 2. Crear una orden de prueba

Desde el frontend (http://localhost:4200):
1. Selecciona un producto
2. Agrega al carrito
3. Completa el formulario con tu email: **william.angaritac@gmail.com**
4. Haz clic en "Realizar Pedido"

### 3. Verificar que se envió el email

**Opción A: Revisar tu bandeja de entrada**
- Revisa tu email: william.angaritac@gmail.com
- Busca un email con asunto: "Confirmación de Orden #..."

**Opción B: Revisar los logs**
```bash
docker-compose logs notifications-service --tail=100 | grep -i "email"
```

**Deberías ver**:
```
Email sent successfully to william.angaritac@gmail.com
```

**Opción C: Revisar la base de datos**
```bash
docker exec -it postgres_db psql -U postgres -d linktic_notifications -c "SELECT * FROM notifications ORDER BY created_at DESC LIMIT 5;"
```

**Deberías ver**:
```
 id | order_number | customer_email              | status | sent_at
----+--------------+-----------------------------+--------+---------------------
  1 | ORD-123456   | william.angaritac@gmail.com | SENT   | 2025-10-27 10:30:00
```

---

## 🔍 Troubleshooting

### Problema 1: "Authentication failed"

**Causa**: Contraseña incorrecta o no es una contraseña de aplicación.

**Solución**:
1. Verifica que usaste una **contraseña de aplicación**, NO tu contraseña normal de Gmail
2. Verifica que la contraseña no tenga espacios
3. Genera una nueva contraseña de aplicación y vuelve a intentar

### Problema 2: "Connection timeout"

**Causa**: Firewall o puerto bloqueado.

**Solución**:
1. Verifica que el puerto 587 no esté bloqueado
2. Intenta cambiar el puerto a 465 (SSL) en `docker-compose.yml`:
```yaml
- SPRING_MAIL_PORT=465
```

### Problema 3: "Email not sent" pero no hay errores

**Causa**: El servicio está configurado para NO enviar emails.

**Solución**:
Verifica en `application-docker.yml` que:
```yaml
app:
  email:
    enabled: true
```

### Problema 4: Kafka no está disponible

**Causa**: Kafka no se levantó correctamente.

**Solución**:
```bash
# Ver logs de Kafka
docker-compose logs kafka --tail=50

# Reiniciar Kafka
docker-compose restart kafka

# Esperar 30 segundos y reiniciar notifications-service
docker-compose restart notifications-service
```

---

## 📊 Flujo Completo de Notificaciones

```
1. Usuario crea orden en Frontend
   ↓
2. Orders Service guarda orden en MySQL
   ↓
3. Orders Service publica evento en Kafka (topic: order-events)
   ↓
4. Notifications Service consume evento de Kafka
   ↓
5. Notifications Service guarda notificación en PostgreSQL
   ↓
6. Notifications Service envía email vía Gmail SMTP
   ↓
7. Usuario recibe email de confirmación
```

---

## 🔐 Seguridad

### ✅ Buenas Prácticas

1. **NUNCA** compartas tu contraseña de aplicación
2. **NUNCA** subas el archivo `.env` a Git (ya está en `.gitignore`)
3. **SIEMPRE** usa contraseñas de aplicación, no tu contraseña principal
4. **REVOCA** contraseñas de aplicación que no uses

### 🗑️ Revocar Contraseña de Aplicación

Si necesitas revocar una contraseña:
1. Ve a https://myaccount.google.com/apppasswords
2. Busca "Linktic E-Commerce"
3. Haz clic en el icono de la papelera
4. Genera una nueva si es necesario

---

## 📝 Configuración Actual

### Email Configurado
- **Host**: smtp.gmail.com
- **Puerto**: 587
- **Usuario**: william.angaritac@gmail.com
- **Contraseña**: (configurar en `.env`)
- **TLS**: Habilitado

### Destinatarios
- **From**: william.angaritac@gmail.com
- **To**: william.angaritac@gmail.com (email del cliente en la orden)

---

## 🎯 Comandos Útiles

```bash
# Ver logs del servicio de notificaciones
docker-compose logs -f notifications-service

# Ver logs de Kafka
docker-compose logs -f kafka

# Reiniciar solo el servicio de notificaciones
docker-compose restart notifications-service

# Ver todas las notificaciones en la base de datos
docker exec -it postgres_db psql -U postgres -d linktic_notifications -c "SELECT * FROM notifications;"

# Ver el estado de todos los servicios
docker-compose ps

# Detener todo y limpiar
docker-compose down -v
```

---

## ✅ Checklist de Configuración

- [ ] Verificación en 2 pasos activada en Gmail
- [ ] Contraseña de aplicación generada
- [ ] Archivo `.env` creado con la contraseña
- [ ] Contraseña sin espacios
- [ ] Contenedores reiniciados
- [ ] Kafka funcionando correctamente
- [ ] Notifications Service sin errores en logs
- [ ] Orden de prueba creada
- [ ] Email recibido en bandeja de entrada

---

**Última actualización**: 2025-10-27  
**Autor**: Sistema E-Commerce Linktic

