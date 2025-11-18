# 📧 Configuración de Email - Gmail SMTP

Esta guía te ayudará a configurar el servicio de notificaciones por email usando Gmail.

## 🎯 Requisitos Previos

- Una cuenta de Gmail activa
- Verificación en 2 pasos activada

---

## 📝 Pasos para Obtener Contraseña de Aplicación

### 1️⃣ Acceder a tu Cuenta de Google

Ve a: https://myaccount.google.com/

### 2️⃣ Activar Verificación en 2 Pasos

1. En el menú lateral, selecciona **"Seguridad"**
2. Busca la sección **"Verificación en 2 pasos"**
3. Si no está activada, haz clic en **"Comenzar"** y sigue los pasos
4. Configura tu método preferido (SMS, aplicación, etc.)

### 3️⃣ Crear Contraseña de Aplicación

1. Una vez activada la verificación en 2 pasos, regresa a **"Seguridad"**
2. Busca **"Contraseñas de aplicaciones"** (aparece solo si tienes 2FA activado)
3. Haz clic en **"Contraseñas de aplicaciones"**
4. Puede que te pida tu contraseña de Google nuevamente

### 4️⃣ Generar la Contraseña

1. En "Seleccionar app", elige **"Correo"**
2. En "Seleccionar dispositivo", elige **"Otro (nombre personalizado)"**
3. Escribe: **"Linktic E-Commerce"**
4. Haz clic en **"Generar"**

### 5️⃣ Copiar la Contraseña

Google te mostrará una contraseña de **16 caracteres** como esta:

```
abcd efgh ijkl mnop
```

**⚠️ IMPORTANTE:** Copia esta contraseña **SIN ESPACIOS**

---

## 🔧 Configurar en el Proyecto

### Opción 1: Archivo .env (Recomendado)

1. Copia el archivo `.env.example` a `.env`:
   ```bash
   cp .env.example .env
   ```

2. Edita el archivo `.env` y configura:
   ```env
   MAIL_USERNAME=tu-email@gmail.com
   MAIL_PASSWORD=abcdefghijklmnop
   MAIL_FROM=noreply@linktic.com
   ```

### Opción 2: Variables de Entorno del Sistema

```bash
# Windows PowerShell
$env:MAIL_USERNAME="tu-email@gmail.com"
$env:MAIL_PASSWORD="abcdefghijklmnop"

# Linux/Mac
export MAIL_USERNAME="tu-email@gmail.com"
export MAIL_PASSWORD="abcdefghijklmnop"
```

### Opción 3: Docker Compose

Edita `docker-compose.yml` en el servicio `notifications-service`:

```yaml
notifications-service:
  environment:
    - SPRING_MAIL_USERNAME=tu-email@gmail.com
    - SPRING_MAIL_PASSWORD=abcdefghijklmnop
```

---

## ✅ Verificar Configuración

### 1️⃣ Levantar el servicio de notificaciones

```bash
docker-compose up notifications-service
```

### 2️⃣ Crear una orden de prueba

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerEmail": "tu-email@gmail.com",
    "items": [
      {
        "sku": "29444ed7a8f8495587365a6b61458735",
        "quantity": 1
      }
    ]
  }'
```

### 3️⃣ Revisar tu email

Deberías recibir un email con el asunto: **"Confirmación de Orden #..."**

---

## 🔍 Troubleshooting

### Error: "Username and Password not accepted"

**Causa:** Contraseña incorrecta o no es una contraseña de aplicación

**Solución:**
1. Verifica que estés usando la contraseña de aplicación (16 caracteres)
2. NO uses tu contraseña normal de Gmail
3. Genera una nueva contraseña de aplicación

### Error: "Authentication failed"

**Causa:** Verificación en 2 pasos no activada

**Solución:**
1. Activa la verificación en 2 pasos en tu cuenta de Google
2. Genera una nueva contraseña de aplicación

### Error: "Connection timeout"

**Causa:** Puerto bloqueado o firewall

**Solución:**
1. Verifica que el puerto 587 esté abierto
2. Revisa tu firewall
3. Intenta con el puerto 465 (SSL):
   ```env
   MAIL_PORT=465
   ```

### No recibo emails

**Posibles causas:**
1. Revisa la carpeta de **Spam**
2. Verifica los logs del servicio:
   ```bash
   docker-compose logs notifications-service
   ```
3. Verifica que el email del cliente sea correcto

---

## 🔒 Seguridad

### ✅ Buenas Prácticas

- ✅ Usa contraseñas de aplicación, NO tu contraseña de Gmail
- ✅ Mantén el archivo `.env` en `.gitignore`
- ✅ NO compartas tu contraseña de aplicación
- ✅ Revoca contraseñas de aplicación que no uses

### ❌ NO Hacer

- ❌ NO subas el archivo `.env` a Git
- ❌ NO uses tu contraseña normal de Gmail
- ❌ NO compartas contraseñas en código fuente
- ❌ NO uses la misma contraseña para múltiples aplicaciones

---

## 📚 Referencias

- [Contraseñas de aplicaciones de Google](https://support.google.com/accounts/answer/185833)
- [Verificación en 2 pasos](https://support.google.com/accounts/answer/185839)
- [Spring Boot Mail](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.email)

---

## 💡 Alternativas a Gmail

Si prefieres no usar Gmail, puedes configurar otros proveedores:

### SendGrid
```env
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=tu-api-key
```

### Mailgun
```env
MAIL_HOST=smtp.mailgun.org
MAIL_PORT=587
MAIL_USERNAME=postmaster@tu-dominio.mailgun.org
MAIL_PASSWORD=tu-password
```

### Amazon SES
```env
MAIL_HOST=email-smtp.us-east-1.amazonaws.com
MAIL_PORT=587
MAIL_USERNAME=tu-smtp-username
MAIL_PASSWORD=tu-smtp-password
```

