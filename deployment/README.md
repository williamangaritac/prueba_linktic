# 🚀 Guía Completa de Despliegue del E-Commerce Fullstack

## 📋 Tabla de Contenidos

1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Respuesta a tus Preguntas](#respuesta-a-tus-preguntas)
3. [Arquitectura de Despliegue](#arquitectura-de-despliegue)
4. [Plan de Despliegue Completo](#plan-de-despliegue-completo)
5. [Estimación de Costos](#estimación-de-costos)
6. [Troubleshooting](#troubleshooting)

---

## 🎯 Resumen Ejecutivo

Este documento describe el despliegue completo de una aplicación e-commerce con:

- **Frontend**: Angular 17 → **Vercel** (Gratis)
- **Backend**: 6 Microservicios Spring Boot → **Azure Container Instances** (~$5-10 por 2 días)
- **Bases de datos**:
  - 3x PostgreSQL → **Supabase** (Gratis)
  - 1x MySQL → **Azure Container** o **Azure Database for MySQL**

**Tiempo estimado de despliegue:** 2-3 horas
**Costo estimado (2 días):** ~$5-15 USD
**Experiencia requerida:** Intermedia

---

## ❓ Respuesta a tus Preguntas

### 1. **Supabase (PostgreSQL) - ¿Cuánto dura la cuenta gratuita?**

✅ **DURACIÓN: INDEFINIDA** (no expira nunca)

**Plan Gratuito incluye:**
- 500 MB de base de datos PostgreSQL
- 1 GB de almacenamiento de archivos
- 2 GB de transferencia de datos/mes
- 50,000 usuarios activos/mes
- Proyectos pausados después de 1 semana de inactividad (se reactivan en segundos)

**Para tu caso (2 días):** ✅ Perfecto. El tamaño de tu base de datos con los 15 productos de prueba es ~50 KB, muy por debajo del límite.

**Después de 1 semana:** El proyecto se pausará automáticamente, pero podrás reactivarlo visitando el dashboard de Supabase.

---

### 2. **Vercel (Frontend Angular) - ¿Cuánto dura la cuenta gratuita?**

✅ **DURACIÓN: INDEFINIDA** (no expira nunca)

**Plan Hobby (Gratuito) incluye:**
- Despliegues ilimitados
- 100 GB de ancho de banda/mes
- 6,000 minutos de build/mes
- SSL automático
- CDN global (más de 70 regiones)
- Sin límite de sitios
- Analíticas básicas

**Para tu caso (2 días):** ✅ Perfecto. Un build de Angular toma ~2-3 minutos, y el ancho de banda para pruebas será mínimo.

**No hay pausas automáticas:** El sitio estará disponible 24/7 sin interrupciones.

---

### 3. **Azure ($50/mes) - ¿Es suficiente para 2 días?**

✅ **MÁS QUE SUFICIENTE**

**Estimación de costos (2 días):**

| Servicio | Configuración | Costo/hora | Costo 2 días (48h) |
|----------|---------------|------------|-------------------|
| Container Instances (6 servicios) | 1 vCPU, 1.5 GB cada | $0.0125/hora x 6 | ~$3.60 |
| Container Registry | Basic | $0.167/día | ~$0.35 |
| MySQL Container | 1 vCPU, 2 GB | $0.0175/hora | ~$0.84 |
| Egress (salida de datos) | Primeros 100 GB gratis | $0 | $0 |
| **TOTAL** | | | **~$4.79** |

**Con $50/mes disponibles:** Podrías mantener todo corriendo por **~10 días** o hacer múltiples ciclos de prueba.

**Alternativa más económica:** Usar Azure Database for MySQL en lugar de container, pero tiene un costo mínimo de ~$0.015/hora ($0.72 por 2 días).

---

## 🏗️ Arquitectura de Despliegue

```
┌─────────────────────────────────────────────────────────────┐
│                      INTERNET                                │
└────────────────────────┬────────────────────────────────────┘
                         │
         ┌───────────────┴───────────────┐
         │                               │
         ▼                               ▼
┌─────────────────┐            ┌──────────────────┐
│   VERCEL CDN    │            │   SUPABASE       │
│   (Frontend)    │            │  (PostgreSQL)    │
│                 │            │                  │
│ • Angular App   │            │ • Products DB    │
│ • SSL Automático│            │ • Inventory DB   │
│ • CDN Global    │            │ • Notifications  │
└────────┬────────┘            └──────────────────┘
         │                              ▲
         │ HTTPS                        │
         │                              │ JDBC
         ▼                              │
┌─────────────────────────────────────────────────┐
│           AZURE CLOUD                           │
│                                                 │
│  ┌──────────────────────────────────────┐      │
│  │     API Gateway (Port 8080)          │      │
│  │  • Spring Cloud Gateway              │      │
│  │  • Load Balancing                    │      │
│  │  • Circuit Breaker                   │      │
│  └────────┬─────────────────────────────┘      │
│           │                                     │
│  ┌────────┴────────────────────────────┐       │
│  │    Service Discovery (Eureka)       │       │
│  │         Port 8761                   │       │
│  └────────┬────────────────────────────┘       │
│           │                                     │
│  ┌────────┴────────────────────────────┐       │
│  │       Microservices Layer           │       │
│  │                                     │       │
│  │  ┌──────────┐  ┌──────────┐       │       │
│  │  │ Products │  │Inventory │       │       │
│  │  │ :8081    │  │ :8082    │       │       │
│  │  └────┬─────┘  └────┬─────┘       │       │
│  │       │             │              │       │
│  │  ┌────┴─────┐  ┌────┴─────┐       │       │
│  │  │  Orders  │  │Notific.  │       │       │
│  │  │  :8083   │  │ :8084    │       │       │
│  │  └────┬─────┘  └──────────┘       │       │
│  │       │                            │       │
│  └───────┼────────────────────────────┘       │
│          │                                     │
│  ┌───────┴─────────────┐                      │
│  │   MySQL Container   │                      │
│  │   linktic_orders    │                      │
│  │     Port 3306       │                      │
│  └─────────────────────┘                      │
│                                                │
└────────────────────────────────────────────────┘
```

**Flujo de datos:**
1. Usuario accede → `https://tu-app.vercel.app`
2. Frontend carga desde Vercel CDN
3. API calls → `http://azure-gateway:8080/api/v1/*`
4. API Gateway → Enruta a microservicio correspondiente
5. Microservicios → Consultan bases de datos (Supabase/MySQL)

---

## 📝 Plan de Despliegue Completo

### ⏱️ Fase 1: Preparación (15 minutos)

#### 1.1 Verificar Pre-requisitos

```bash
# Verificar herramientas instaladas
node --version    # v18 o superior
npm --version     # v9 o superior
docker --version  # 20.10 o superior
az --version      # Azure CLI 2.40 o superior
```

#### 1.2 Clonar y Preparar Repositorio

```bash
cd prueba_linktic
git pull origin main
```

#### 1.3 Crear Cuentas (si no las tienes)

- [ ] Cuenta en Supabase: https://supabase.com
- [ ] Cuenta en Vercel: https://vercel.com
- [ ] Cuenta en Azure: https://azure.microsoft.com

---

### 🗄️ Fase 2: Configurar Supabase (PostgreSQL) - 20 minutos

#### 2.1 Crear Proyectos en Supabase

Necesitas crear **3 proyectos separados** en Supabase:

1. **Proyecto 1: Products Database**
   - Nombre: `linktic-products`
   - Región: Elige la más cercana (ej. East US)
   - Password: Guarda la contraseña generada

2. **Proyecto 2: Inventory Database**
   - Nombre: `linktic-inventory`
   - Región: La misma que proyecto 1
   - Password: Guarda la contraseña

3. **Proyecto 3: Notifications Database**
   - Nombre: `linktic-notifications`
   - Región: La misma que proyectos anteriores
   - Password: Guarda la contraseña

#### 2.2 Ejecutar Scripts SQL

Para cada proyecto:

1. Ve al **SQL Editor** en el dashboard de Supabase
2. Copia y pega el script correspondiente:
   - Proyecto 1: `deployment/supabase/01-products-db.sql`
   - Proyecto 2: `deployment/supabase/02-inventory-db.sql`
   - Proyecto 3: `deployment/supabase/03-notifications-db.sql`
3. Click en **Run** o presiona `Ctrl+Enter`
4. Verifica que las tablas se crearon correctamente

#### 2.3 Obtener Connection Strings

Para cada proyecto, ve a **Settings** → **Database** y copia:

```
Connection String (URI format):
postgresql://postgres:[YOUR-PASSWORD]@db.[PROJECT-REF].supabase.co:5432/postgres
```

Guarda estas 3 URLs, las necesitarás para Azure.

**Ejemplo:**
```env
PRODUCTS_DB_URL=postgresql://postgres:tu_password@db.abcdefghijklm.supabase.co:5432/postgres
INVENTORY_DB_URL=postgresql://postgres:tu_password@db.nopqrstuvwxyz.supabase.co:5432/postgres
NOTIFICATIONS_DB_URL=postgresql://postgres:tu_password@db.1234567890abc.supabase.co:5432/postgres
```

#### 2.4 Verificar Datos de Prueba

```sql
-- En cada proyecto, ejecuta en SQL Editor:

-- Products DB:
SELECT COUNT(*) FROM products;  -- Debe retornar 15

-- Inventory DB:
SELECT COUNT(*) FROM inventory; -- Debe retornar 15

-- Notifications DB:
SELECT COUNT(*) FROM notifications; -- Puede ser 0 o más
```

---

### ☁️ Fase 3: Desplegar Backend en Azure (45-60 minutos)

#### 3.1 Preparar Configuración

```bash
cd deployment/azure

# Copiar archivo de ejemplo
cp .env.azure.example .env.azure

# Editar con tus valores reales
nano .env.azure  # o usa tu editor preferido
```

**Completa el archivo `.env.azure` con:**
- URLs de Supabase (de la Fase 2)
- Contraseñas de Supabase
- Gmail App Password (para notificaciones)
- Configuración de Azure

**Ejemplo de `.env.azure`:**
```env
# Supabase Products
PRODUCTS_DB_URL=postgresql://postgres:TuPassword123@db.abcdefg.supabase.co:5432/postgres
PRODUCTS_DB_USER=postgres
PRODUCTS_DB_PASSWORD=TuPassword123

# Supabase Inventory
INVENTORY_DB_URL=postgresql://postgres:TuPassword123@db.hijklmn.supabase.co:5432/postgres
INVENTORY_DB_USER=postgres
INVENTORY_DB_PASSWORD=TuPassword123

# Supabase Notifications
NOTIFICATIONS_DB_URL=postgresql://postgres:TuPassword123@db.opqrstu.supabase.co:5432/postgres
NOTIFICATIONS_DB_USER=postgres
NOTIFICATIONS_DB_PASSWORD=TuPassword123

# MySQL
MYSQL_ROOT_PASSWORD=linktic_secure_2024
MYSQL_PASSWORD=linktic_secure_2024

# Gmail
MAIL_USERNAME=linktic515@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop  # 16 caracteres, sin espacios

# Azure
AZURE_RESOURCE_GROUP=rg-linktic-ecommerce
AZURE_LOCATION=eastus
AZURE_CONTAINER_REGISTRY=linkticecommerceacr
```

#### 3.2 Login en Azure

```bash
az login
```

Se abrirá tu navegador para autenticarte.

#### 3.3 Ejecutar Script de Despliegue

```bash
# Dar permisos de ejecución
chmod +x deploy-to-azure.sh

# Ejecutar despliegue
./deploy-to-azure.sh
```

**El script hará:**
1. ✅ Crear Resource Group
2. ✅ Crear Azure Container Registry
3. ✅ Build de 6 microservicios
4. ✅ Push de imágenes a ACR
5. ✅ Desplegar MySQL container
6. ✅ Desplegar Eureka Server
7. ✅ Desplegar todos los microservicios
8. ✅ Desplegar API Gateway
9. ✅ Generar archivo `deployment-info.txt` con URLs

**Tiempo estimado:** 30-45 minutos

#### 3.4 Verificar Despliegue

```bash
# Ver estado de containers
az container list --resource-group rg-linktic-ecommerce --output table

# Ver logs de un servicio
az container logs --resource-group rg-linktic-ecommerce --name api-gateway

# Verificar health de servicios
curl http://[API_GATEWAY_URL]:8080/actuator/health
```

#### 3.5 Obtener URL del API Gateway

```bash
# Ver información de despliegue
cat deployment-info.txt
```

Busca la línea:
```
API Gateway: http://linktic-ecommerce-gateway.eastus.azurecontainer.io:8080
```

**Guarda esta URL**, la necesitas para Vercel.

#### 3.6 Probar Endpoints

```bash
# Reemplaza [GATEWAY_URL] con tu URL real

# Listar productos
curl http://[GATEWAY_URL]:8080/api/v1/products

# Obtener un producto
curl http://[GATEWAY_URL]:8080/api/v1/products/sku/29444ed7a8f8495587365a6b61458735

# Verificar inventario
curl http://[GATEWAY_URL]:8080/api/v1/inventory/29444ed7a8f8495587365a6b61458735
```

---

### 🎨 Fase 4: Desplegar Frontend en Vercel (15-20 minutos)

#### 4.1 Preparar Repositorio Git

Si aún no has subido tu código a Git:

```bash
cd ~/prueba_linktic

# Inicializar git (si no existe)
git init

# Agregar archivos
git add .

# Commit
git commit -m "feat: Configuración para despliegue en Vercel y Azure"

# Crear repositorio en GitHub y pushear
# (Sigue las instrucciones de GitHub)
git remote add origin https://github.com/TU_USUARIO/prueba_linktic.git
git branch -M main
git push -u origin main
```

#### 4.2 Verificar archivo vercel.json

```bash
# Verificar que existe
ls -la frontend_angular/vercel.json

# Si no existe, copiarlo
cp deployment/vercel/vercel.json frontend_angular/
git add frontend_angular/vercel.json
git commit -m "feat: Agregar configuración de Vercel"
git push
```

#### 4.3 Despliegue desde Vercel Web

1. Ve a https://vercel.com
2. Click en **"Add New..."** → **"Project"**
3. Importa tu repositorio Git
4. Configura:
   - **Framework Preset:** Angular
   - **Root Directory:** `frontend_angular`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist/frontend-angular/browser`

5. **Variables de Entorno:**
   ```
   API_GATEWAY_URL=http://[TU_AZURE_GATEWAY]:8080
   ```
   Ejemplo:
   ```
   API_GATEWAY_URL=http://linktic-ecommerce-gateway.eastus.azurecontainer.io:8080
   ```

6. Click en **"Deploy"**

**Tiempo de build:** 2-5 minutos

#### 4.4 Obtener URL de Vercel

Una vez completado el build:
```
https://tu-proyecto.vercel.app
```

O un dominio personalizado si lo configuraste.

#### 4.5 Verificar Funcionamiento

1. Abre la URL de Vercel en tu navegador
2. Verifica que se cargue el catálogo de productos
3. Intenta realizar una compra de prueba
4. Verifica en las Developer Tools (F12) que no haya errores de consola

---

### 🎯 Fase 5: Pruebas End-to-End (15 minutos)

#### 5.1 Flujo Completo de E-Commerce

**Test 1: Ver Catálogo**
```
1. Abre https://tu-proyecto.vercel.app
2. Verifica que se muestren 15 productos
3. Verifica que cada producto tenga:
   - Imagen (placeholder)
   - Nombre
   - Precio
   - Stock disponible
   - Botón "Ver Detalles"
```

**Test 2: Ver Detalles de Producto**
```
1. Click en "Ver Detalles" de cualquier producto
2. Verifica el modal con:
   - Nombre completo
   - Descripción
   - Precio
   - Stock disponible
   - Botón "Comprar"
```

**Test 3: Realizar Compra**
```
1. Click en "Comprar"
2. Ingresa:
   - Email: tu-email@example.com
   - Cantidad: 2 unidades
3. Click en "Confirmar Compra"
4. Verifica mensaje de éxito
5. Verifica que el stock se haya reducido
```

**Test 4: Verificar Notificación (Opcional)**
```
1. Si configuraste Gmail correctamente
2. Revisa tu correo
3. Deberías recibir una confirmación de pedido
```

#### 5.2 Verificar en Bases de Datos

**Supabase - Orders:**
```sql
-- En Supabase, proyecto Notifications
SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10;
```

**Azure MySQL - Orders:**
```bash
# Conectar a MySQL container
az container exec \
  --resource-group rg-linktic-ecommerce \
  --name mysql-db \
  --exec-command "mysql -u linktic -plinktic123 linktic_orders -e 'SELECT * FROM orders ORDER BY created_at DESC LIMIT 10;'"
```

#### 5.3 Monitoreo y Logs

**Ver logs en Azure:**
```bash
# API Gateway
az container logs --resource-group rg-linktic-ecommerce --name api-gateway --tail 50

# Products Service
az container logs --resource-group rg-linktic-ecommerce --name products-service --tail 50

# Orders Service
az container logs --resource-group rg-linktic-ecommerce --name orders-service --tail 50
```

**Ver logs en Vercel:**
1. Ve al dashboard de Vercel
2. Selecciona tu proyecto
3. Click en **"Deployments"**
4. Click en el último despliegue
5. Ve la pestaña **"Functions"** o **"Build Logs"**

---

## 💰 Estimación Detallada de Costos

### 🆓 Servicios Gratuitos (Para Siempre)

| Servicio | Plan | Límites | Costo |
|----------|------|---------|-------|
| **Supabase** | Gratuito | 500 MB DB, 1 GB storage, 2 GB bandwidth/mes | $0 |
| **Vercel** | Hobby | 100 GB bandwidth/mes, 6000 min builds | $0 |

**Total Gratis:** $0 (indefinidamente)

---

### 💳 Azure (Con $50/mes disponibles)

#### Opción 1: Container Instances (Recomendado para 2 días)

| Recurso | Specs | Costo/hora | Costo 48h | Costo 30 días |
|---------|-------|------------|-----------|---------------|
| Eureka Server | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| API Gateway | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| Products Service | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| Inventory Service | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| Orders Service | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| Notifications Service | 1 vCPU, 1.5 GB | $0.0125 | $0.60 | $9.00 |
| MySQL Container | 1 vCPU, 2 GB | $0.0175 | $0.84 | $12.60 |
| Container Registry | Basic | $0.167/día | $0.35 | $5.00 |
| Bandwidth (salida) | Primeros 100 GB | $0 | $0 | $0 |
| **TOTAL** | | | **$4.79** | **$71.60** |

**Con $50 disponibles por mes:**
- ✅ Puedes correr **2 días** por ~$5
- ✅ Puedes correr **todo el mes parcialmente** (~20 días)
- ✅ Puedes hacer **múltiples ciclos de prueba**

#### Opción 2: Azure App Service (Más caro, más features)

| Recurso | Plan | Costo/mes | Costo 2 días |
|---------|------|-----------|--------------|
| App Service Plan | B1 (1 Core, 1.75 GB) x 6 apps | $54.75 | $3.65 |
| Azure Database for MySQL | B1ms (1 vCore, 2 GB) | $21.17 | $1.41 |
| **TOTAL** | | | **$5.06** |

---

### 📊 Resumen de Costos

| Escenario | Supabase | Vercel | Azure | **TOTAL** |
|-----------|----------|--------|-------|-----------|
| **2 días de prueba** | $0 | $0 | $4.79 | **$4.79** |
| **1 semana** | $0 | $0 | $16.76 | **$16.76** |
| **1 mes completo** | $0 | $0 | $71.60 | **$71.60** |

**Conclusión:** Con tu presupuesto de **$50/mes en Azure**:
- ✅ **2 días:** Solo gastarás ~$5, te sobran $45
- ✅ **1 semana:** Gastarás ~$17, te sobran $33
- ✅ **3 semanas:** Gastarás ~$50 (límite del presupuesto)

---

## 🗑️ Limpieza Después de las Pruebas

### Eliminar Recursos de Azure (Importante para no gastar dinero)

```bash
# Eliminar todo el Resource Group (todos los servicios)
az group delete --name rg-linktic-ecommerce --yes --no-wait

# Verificar eliminación
az group list --output table
```

**Esto eliminará:**
- ❌ Todos los containers
- ❌ Container Registry
- ❌ Networking
- ❌ Storage

**Tiempo:** ~5 minutos

---

### Pausar/Eliminar Proyecto en Vercel

**Opción 1: Pausar (mantener el proyecto)**
```bash
# Eliminar el despliegue pero mantener el proyecto
vercel remove linktic-ecommerce-frontend --scope tu-usuario
```

**Opción 2: Eliminar completamente**
1. Ve al dashboard de Vercel
2. Settings → General → Delete Project

---

### Mantener Supabase (Gratis Forever)

**No necesitas hacer nada:**
- Los proyectos se pausarán automáticamente después de 1 semana de inactividad
- Se reactivan inmediatamente al acceder al dashboard
- No se eliminan automáticamente
- No hay cargos

**Si quieres eliminar proyectos:**
1. Ve al dashboard de Supabase
2. Selecciona proyecto → Settings → General
3. Click en **"Delete project"**
4. Confirma escribiendo el nombre del proyecto

---

## 🐛 Troubleshooting

### Problema: Build falla en Vercel

**Síntomas:**
```
Error: Cannot find module '@angular/core'
```

**Solución:**
```bash
# Asegúrate de que package-lock.json esté en el repo
cd frontend_angular
npm ci
git add package-lock.json
git commit -m "fix: Add package-lock.json"
git push
```

---

### Problema: API Gateway no se conecta a microservicios

**Síntomas:**
```
503 Service Unavailable
```

**Solución:**
```bash
# Verificar que Eureka esté corriendo
curl http://[EUREKA_URL]:8761/eureka/apps

# Verificar logs
az container logs --resource-group rg-linktic-ecommerce --name api-gateway

# Reiniciar gateway
az container restart --resource-group rg-linktic-ecommerce --name api-gateway
```

---

### Problema: Frontend no puede conectarse al backend

**Síntomas:**
- Productos no cargan
- Error CORS en consola del navegador

**Solución 1: Verificar variable de entorno en Vercel**
```bash
# En Vercel dashboard:
Settings → Environment Variables → API_GATEWAY_URL

# Debe ser:
http://[TU_GATEWAY].azurecontainer.io:8080
```

**Solución 2: Configurar CORS en API Gateway**

Edita `api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "https://tu-proyecto.vercel.app"
              - "https://*.vercel.app"
            allowedMethods: "*"
            allowedHeaders: "*"
            allowCredentials: true
```

Rebuild y redeploy el gateway.

---

### Problema: MySQL no se conecta

**Síntomas:**
```
Communications link failure
```

**Solución:**
```bash
# Verificar que MySQL esté corriendo
az container show --resource-group rg-linktic-ecommerce --name mysql-db --query "instanceView.state" -o tsv

# Ver logs de MySQL
az container logs --resource-group rg-linktic-ecommerce --name mysql-db

# Si falla health check, reiniciar
az container restart --resource-group rg-linktic-ecommerce --name mysql-db

# Esperar 60 segundos para que MySQL esté listo
sleep 60

# Reiniciar orders-service
az container restart --resource-group rg-linktic-ecommerce --name orders-service
```

---

### Problema: Supabase Connection Timeout

**Síntomas:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Solución:**
```bash
# Verificar connection string
echo $PRODUCTS_DB_URL

# Debe tener formato:
postgresql://postgres:PASSWORD@db.PROJECT_ID.supabase.co:5432/postgres

# Verificar que el proyecto no esté pausado
# Ve al dashboard de Supabase y verifica que el proyecto esté activo

# Verificar firewall: Supabase permite conexiones desde cualquier IP por defecto
# Si tienes problemas, ve a Settings → Database → Connection pooling
```

---

## 📚 Recursos Adicionales

### Documentación Oficial

- **Supabase:** https://supabase.com/docs
- **Vercel:** https://vercel.com/docs
- **Azure Container Instances:** https://docs.microsoft.com/azure/container-instances/
- **Spring Cloud:** https://spring.io/projects/spring-cloud

### Guías Específicas

- **Supabase PostgreSQL:** `deployment/supabase/*.sql`
- **Azure Deployment:** `deployment/azure/deploy-to-azure.sh`
- **Vercel Setup:** `deployment/vercel/README-VERCEL.md`

### Comandos Rápidos

```bash
# Ver estado de todos los containers en Azure
az container list --resource-group rg-linktic-ecommerce --output table

# Ver logs en tiempo real
az container attach --resource-group rg-linktic-ecommerce --name api-gateway

# Reiniciar un servicio
az container restart --resource-group rg-linktic-ecommerce --name [SERVICE_NAME]

# Ver uso de recursos
az monitor metrics list --resource [RESOURCE_ID] --metric "CPUUsage,MemoryUsage"

# Eliminar todo
az group delete --name rg-linktic-ecommerce --yes --no-wait
```

---

## ✅ Checklist Completo de Despliegue

### Pre-requisitos
- [ ] Node.js 18+ instalado
- [ ] Docker instalado
- [ ] Azure CLI instalado
- [ ] Cuentas creadas (Supabase, Vercel, Azure)
- [ ] Gmail App Password generado

### Supabase
- [ ] 3 proyectos creados
- [ ] Scripts SQL ejecutados
- [ ] Tablas verificadas con datos
- [ ] Connection strings guardados

### Azure
- [ ] `.env.azure` configurado con todos los valores
- [ ] Login en Azure CLI completado
- [ ] Script `deploy-to-azure.sh` ejecutado
- [ ] Todos los containers corriendo
- [ ] Health checks pasando
- [ ] API Gateway accesible
- [ ] Endpoints probados con curl
- [ ] URL del Gateway guardada

### Vercel
- [ ] Código pusheado a Git
- [ ] `vercel.json` en `frontend_angular/`
- [ ] Proyecto creado en Vercel
- [ ] Variable `API_GATEWAY_URL` configurada
- [ ] Build completado exitosamente
- [ ] Sitio accesible
- [ ] Frontend puede consumir el backend

### Pruebas
- [ ] Catálogo de productos carga correctamente
- [ ] Modal de detalles funciona
- [ ] Proceso de compra completo
- [ ] Stock se actualiza
- [ ] Notificación por email recibida (opcional)
- [ ] Logs sin errores críticos

### Post-Despliegue
- [ ] Documentar URLs en un lugar seguro
- [ ] Configurar alertas de costos en Azure
- [ ] Programar eliminación de recursos después de pruebas

---

## 🎉 ¡Listo!

Has completado el despliegue de un sistema completo de e-commerce con microservicios en producción.

**Arquitectura desplegada:**
- ✅ Frontend en CDN global (Vercel)
- ✅ 6 microservicios en Azure
- ✅ 4 bases de datos (3 PostgreSQL + 1 MySQL)
- ✅ Service discovery con Eureka
- ✅ API Gateway con load balancing
- ✅ Circuit breakers para resiliencia
- ✅ SSL automático
- ✅ Notificaciones por email

**Costo total (2 días):** ~$5 USD
**Disponibilidad:** 24/7
**Escalabilidad:** Preparado para crecer

---

**Dudas o problemas?** Revisa la sección de [Troubleshooting](#troubleshooting) o consulta los logs detallados en cada plataforma.

**¡Éxito con tu proyecto!** 🚀
