# 📧 Configuración de Email - Notifications Service

## ✅ Cambio Realizado

Se ha actualizado la configuración de email en `notifications_service` para usar:

**Email:** `william.angaritac@gmail.com`

---

## 📝 Archivos Modificados

### `notifications_service/src/main/resources/application.yml`

Se actualizaron las siguientes propiedades:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: william.angaritac@gmail.com  # ✅ ACTUALIZADO
    password: # Configurar con App Password de Gmail

app:
  email:
    from: william.angaritac@gmail.com  # ✅ ACTUALIZADO
    to: william.angaritac@gmail.com    # ✅ ACTUALIZADO
    enabled: true
```

---

## 🔐 Configurar App Password de Gmail

Para que el servicio pueda enviar emails, necesitas generar una **App Password** en Gmail:

### Paso 1: Habilitar Verificación en 2 Pasos

1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. Selecciona **Seguridad** en el menú lateral
3. En "Cómo inicias sesión en Google", selecciona **Verificación en 2 pasos**
4. Sigue los pasos para habilitar la verificación en 2 pasos

### Paso 2: Generar App Password

1. Una vez habilitada la verificación en 2 pasos, regresa a **Seguridad**
2. En "Cómo inicias sesión en Google", selecciona **Contraseñas de aplicaciones**
3. Selecciona la aplicación: **Correo**
4. Selecciona el dispositivo: **Otro (nombre personalizado)**
5. Escribe: `LINKTIC Notifications Service`
6. Haz clic en **Generar**
7. **Copia la contraseña de 16 caracteres** que aparece

### Paso 3: Configurar la Contraseña

Tienes 2 opciones:

#### Opción 1: Variable de Entorno (Recomendado para Producción)

```powershell
# Windows PowerShell
$env:MAIL_PASSWORD = "tu-app-password-de-16-caracteres"

# Luego iniciar el servicio
java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
```

#### Opción 2: Editar application.yml (Solo para Desarrollo)

```yaml
spring:
  mail:
    username: william.angaritac@gmail.com
    password: "tu-app-password-de-16-caracteres"  # ⚠️ NO SUBIR A GIT
```

**⚠️ IMPORTANTE:** Si usas la Opción 2, asegúrate de NO subir el archivo con la contraseña a Git.

---

## 🔄 Recompilar el Servicio

### Si el servicio está corriendo:

1. **Detener el servicio:**
   - Cierra la ventana de PowerShell donde está corriendo
   - O presiona `Ctrl+C` en la terminal

2. **Recompilar:**
   ```powershell
   cd notifications_service
   .\mvnw.cmd clean package -DskipTests
   ```

3. **Iniciar con la nueva configuración:**
   ```powershell
   # Con variable de entorno
   $env:MAIL_PASSWORD = "tu-app-password"
   java -jar target/notifications_service-0.0.1-SNAPSHOT.jar
   ```

### Si usas el script maestro:

```powershell
# Detener todos los servicios (cerrar ventanas de PowerShell)

# Configurar variable de entorno
$env:MAIL_PASSWORD = "tu-app-password"

# Recompilar solo notifications_service
cd notifications_service
.\mvnw.cmd clean package -DskipTests
cd ..

# Iniciar todos los servicios
.\start-all-services.ps1 -SkipBuild
```

---

## 🧪 Probar el Envío de Emails

### Paso 1: Crear una Orden

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

### Paso 2: Verificar en los Logs

Deberías ver en los logs de `notifications_service`:

```
INFO  c.l.n.services.EmailService : Sending email to: william.angaritac@gmail.com
INFO  c.l.n.services.EmailService : Email sent successfully to william.angaritac@gmail.com
```

### Paso 3: Verificar en Gmail

1. Abre Gmail: https://mail.google.com/
2. Busca un email con asunto: **"Nueva Orden Creada - ORD-..."**
3. El email debe contener:
   - Número de orden
   - Lista de productos
   - Total de la orden

---

## 📊 Verificar Estado de Notificaciones

### Endpoint de Consulta

```bash
# Ver todas las notificaciones
curl http://localhost:8080/api/v1/notifications

# Ver notificaciones por estado
curl http://localhost:8080/api/v1/notifications/status/SENT
curl http://localhost:8080/api/v1/notifications/status/FAILED
```

### Swagger UI

Abre: http://localhost:8084/api/v1/swagger-ui.html

Prueba los endpoints:
- `GET /notifications` - Lista todas las notificaciones
- `GET /notifications/{id}` - Detalle de una notificación
- `GET /notifications/status/{status}` - Filtrar por estado

---

## ⚠️ Troubleshooting

### Error: "Authentication failed"

**Causa:** App Password incorrecta o no configurada

**Solución:**
1. Verifica que hayas generado una App Password
2. Verifica que la contraseña esté correctamente configurada
3. Asegúrate de que la verificación en 2 pasos esté habilitada

### Error: "Connection timeout"

**Causa:** Firewall o antivirus bloqueando el puerto 587

**Solución:**
1. Verifica tu conexión a internet
2. Desactiva temporalmente el firewall/antivirus
3. Verifica que el puerto 587 esté abierto

### Email no llega

**Posibles causas:**
1. Revisa la carpeta de **Spam** en Gmail
2. Verifica los logs de `notifications_service`
3. Verifica que `app.email.enabled: true` en application.yml

---

## 🔒 Seguridad en Producción

### Usar Variables de Entorno

```yaml
spring:
  mail:
    username: ${MAIL_USERNAME:william.angaritac@gmail.com}
    password: ${MAIL_PASSWORD}

app:
  email:
    from: ${MAIL_FROM:william.angaritac@gmail.com}
    to: ${MAIL_TO:william.angaritac@gmail.com}
```

### Configurar en el Sistema

```powershell
# Windows
setx MAIL_USERNAME "william.angaritac@gmail.com"
setx MAIL_PASSWORD "tu-app-password"
setx MAIL_FROM "william.angaritac@gmail.com"
setx MAIL_TO "william.angaritac@gmail.com"
```

### Docker (Futuro)

```yaml
# docker-compose.yml
services:
  notifications-service:
    environment:
      - MAIL_USERNAME=william.angaritac@gmail.com
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - MAIL_FROM=william.angaritac@gmail.com
      - MAIL_TO=william.angaritac@gmail.com
```

---

## 📝 Resumen de Cambios

| Propiedad | Valor Anterior | Valor Nuevo |
|-----------|----------------|-------------|
| `spring.mail.username` | `contacto@linktic.com` | `william.angaritac@gmail.com` |
| `app.email.from` | `contacto@linktic.com` | `william.angaritac@gmail.com` |
| `app.email.to` | `contacto@linktic.com` | `william.angaritac@gmail.com` |

---

## ✅ Checklist de Configuración

- [ ] Habilitar verificación en 2 pasos en Gmail
- [ ] Generar App Password en Gmail
- [ ] Configurar `MAIL_PASSWORD` como variable de entorno
- [ ] Detener `notifications_service` si está corriendo
- [ ] Recompilar `notifications_service`
- [ ] Iniciar `notifications_service` con la nueva configuración
- [ ] Crear una orden de prueba
- [ ] Verificar que el email llegue a `william.angaritac@gmail.com`
- [ ] Verificar logs de `notifications_service`
- [ ] Verificar estado de notificaciones en Swagger UI

---

**¡Configuración de email actualizada exitosamente! 📧**

