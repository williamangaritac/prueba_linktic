# 🐳 Resumen: Despliegue Fullstack con Docker Compose

## ✅ Archivos Creados

### 📦 Configuración Docker

1. **`docker-compose.yml`** - Orquestación de todos los servicios
   - 8 servicios: Frontend + 5 microservicios + 2 bases de datos
   - Redes y volúmenes configurados
   - Health checks para todos los servicios

2. **Dockerfiles:**
   - `eureka-server/Dockerfile`
   - `products_service/Dockerfile` (ya existía)
   - `inventory_service/Dockerfile`
   - `orders_service/Dockerfile`
   - `notifications_service/Dockerfile`
   - `C:/Users/willi/OneDrive/Escritorio/prueba_linktic/frontend_angular/Dockerfile`

3. **Configuración Frontend:**
   - `C:/Users/willi/OneDrive/Escritorio/prueba_linktic/frontend_angular/nginx.conf`

4. **Scripts SQL:**
   - `init-postgres.sql` - Inicializa 3 bases de datos PostgreSQL con datos
   - `init-mysql.sql` - Inicializa base de datos MySQL para órdenes

5. **Scripts de Despliegue:**
   - `docker-deploy.ps1` - Script automatizado para desplegar todo
   - `.dockerignore` - Optimiza builds de Docker
   - `.env.example` - Template para variables de entorno

6. **Documentación:**
   - `DOCKER_DEPLOYMENT_GUIDE.md` - Guía completa de uso
   - `RESUMEN_DOCKER_DEPLOYMENT.md` - Este archivo

---

## 🚀 Cómo Desplegar

### ⚠️ IMPORTANTE: Iniciar Docker Desktop PRIMERO

**Antes de ejecutar cualquier comando:**

1. Busca "Docker Desktop" en el menú de inicio de Windows
2. Haz click para iniciarlo
3. Espera a que el ícono aparezca en la barra de tareas
4. Espera a que el ícono deje de parpadear (1-2 minutos)

### Paso 1: Ejecutar el Script

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic
.\docker-deploy.ps1
```

### Paso 2: Esperar (2-3 minutos)

El script:
- ✅ Verifica Docker
- ✅ Detiene servicios locales
- ✅ Construye imágenes (primera vez: 5-10 minutos)
- ✅ Levanta todos los servicios
- ✅ Espera a que estén listos
- ✅ Muestra URLs y estado

### Paso 3: Verificar

```powershell
docker-compose ps
```

Deberías ver 8 servicios corriendo.

---

## 📊 Arquitectura Desplegada

```
┌─────────────────────────────────────────────────────────────┐
│                    DOCKER COMPOSE                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐                                          │
│  │   Frontend   │  :4200  (Angular + Nginx)                │
│  │   Angular    │                                          │
│  └──────┬───────┘                                          │
│         │                                                   │
│         ├──────────────────────────────────────┐           │
│         │                                       │           │
│  ┌──────▼───────┐  ┌──────────────┐  ┌────────▼────────┐  │
│  │   Products   │  │    Eureka    │  │     Orders      │  │
│  │   Service    │  │    Server    │  │    Service      │  │
│  │    :8081     │  │    :8761     │  │     :8083       │  │
│  └──────┬───────┘  └──────────────┘  └────────┬────────┘  │
│         │                                       │           │
│  ┌──────▼───────┐  ┌──────────────┐           │           │
│  │  Inventory   │  │Notifications │           │           │
│  │   Service    │  │   Service    │           │           │
│  │    :8082     │  │    :8084     │           │           │
│  └──────┬───────┘  └──────┬───────┘           │           │
│         │                  │                   │           │
│  ┌──────▼──────────────────▼───────┐  ┌───────▼────────┐  │
│  │        PostgreSQL               │  │     MySQL      │  │
│  │          :5432                  │  │     :3306      │  │
│  │  - linktic_products             │  │ linktic_orders │  │
│  │  - linktic_inventory            │  └────────────────┘  │
│  │  - linktic_notifications        │                      │
│  └─────────────────────────────────┘                      │
│                                                             │
│         Red: microservices-network                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🌐 URLs de Acceso

### Frontend
- **Angular App:** http://localhost:4200

### Backend
- **Eureka Dashboard:** http://localhost:8761
- **Products API:** http://localhost:8081/api/v1/frontend/products?page=0
- **Inventory API:** http://localhost:8082
- **Orders API:** http://localhost:8083
- **Notifications API:** http://localhost:8084

### Bases de Datos
- **PostgreSQL:** localhost:5432 (usuario: postgres, password: postgres)
- **MySQL:** localhost:3306 (usuario: linktic, password: linktic123)

---

## 🧪 Probar el Sistema

### 1. Abrir el Frontend
```
http://localhost:4200
```

### 2. Verificar Productos
- Deberías ver 6 productos por página
- Cada producto muestra: nombre, descripción, **PRECIO**, SKU
- Total: 15 productos en 3 páginas

### 3. Verificar Eureka
```
http://localhost:8761
```
Deberías ver 4 servicios registrados.

### 4. Probar API de Productos
```powershell
curl http://localhost:8081/api/v1/frontend/products?page=0
```

---

## 📝 Comandos Útiles

### Ver Estado
```powershell
docker-compose ps
```

### Ver Logs
```powershell
# Todos los servicios
docker-compose logs -f

# Un servicio específico
docker-compose logs -f products-service

# Últimas 100 líneas
docker-compose logs --tail=100 products-service
```

### Reiniciar Servicios
```powershell
# Reiniciar un servicio
docker-compose restart products-service

# Reiniciar todos
docker-compose restart
```

### Detener Todo
```powershell
# Detener (mantiene datos)
docker-compose down

# Detener y eliminar datos
docker-compose down -v
```

### Reconstruir
```powershell
# Reconstruir un servicio
docker-compose up -d --build products-service

# Reconstruir todo
docker-compose build --no-cache
docker-compose up -d
```

---

## 🔧 Actualizar Código

### Backend (Microservicios)

```powershell
# 1. Hacer cambios en el código

# 2. Recompilar el JAR
cd products_service
mvnw clean package -DskipTests

# 3. Volver a la raíz
cd ..

# 4. Reconstruir imagen Docker
docker-compose build products-service

# 5. Reiniciar el servicio
docker-compose up -d products-service

# 6. Ver logs
docker-compose logs -f products-service
```

### Frontend (Angular)

```powershell
# 1. Hacer cambios en el código del frontend

# 2. Reconstruir imagen Docker
docker-compose build frontend

# 3. Reiniciar el servicio
docker-compose up -d frontend

# 4. Ver logs
docker-compose logs -f frontend
```

---

## 🐛 Solución de Problemas

### Docker Desktop no está corriendo

**Error:**
```
error during connect: Get "http://%2F%2F.%2Fpipe%2FdockerDesktopLinuxEngine/v1.51/containers/json"
```

**Solución:**
1. Inicia Docker Desktop desde el menú de inicio
2. Espera a que esté listo (ícono en la barra de tareas)
3. Vuelve a ejecutar `.\docker-deploy.ps1`

### Puerto ya en uso

**Error:**
```
Bind for 0.0.0.0:8081 failed: port is already allocated
```

**Solución:**
```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8081

# Detener el proceso
Stop-Process -Id [PID] -Force

# O detener todos los servicios Java
Stop-Process -Name java -Force
```

### Servicio no inicia (unhealthy)

**Solución:**
```powershell
# Ver logs del servicio
docker-compose logs products-service

# Reiniciar el servicio
docker-compose restart products-service

# Si persiste, reconstruir
docker-compose up -d --build products-service
```

### Base de datos no se conecta

**Solución:**
```powershell
# Ver logs de la base de datos
docker-compose logs postgres
docker-compose logs mysql

# Reiniciar bases de datos
docker-compose restart postgres mysql

# Verificar que estén healthy
docker-compose ps
```

### Frontend no carga

**Solución:**
```powershell
# Ver logs
docker-compose logs frontend

# Reconstruir
docker-compose build frontend
docker-compose up -d frontend
```

---

## 📦 Datos de Prueba

### Productos (15 productos)
- SKU: `29444ed7a8f8495587365a6b61458735` - Solucion E-commerce - $2,805.00
- SKU: `a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6` - Sistema de Gestión de Inventario - $1,850.00
- ... (13 productos más)

### Inventario
- Todos los productos tienen stock disponible (50-150 unidades)

### Bases de Datos
- **PostgreSQL:** 3 bases de datos (products, inventory, notifications)
- **MySQL:** 1 base de datos (orders)

---

## ✅ Checklist de Despliegue

- [ ] Docker Desktop instalado
- [ ] Docker Desktop corriendo (ícono en barra de tareas)
- [ ] Puertos libres: 4200, 8761, 8081-8084, 5432, 3306
- [ ] Ejecutar `.\docker-deploy.ps1`
- [ ] Esperar 2-3 minutos
- [ ] Verificar con `docker-compose ps` (8 servicios Up)
- [ ] Abrir http://localhost:4200
- [ ] Verificar que se ven productos con precios
- [ ] Verificar Eureka en http://localhost:8761
- [ ] Probar botón "Ver Detalles"
- [ ] Probar botón "Comprar"

---

## 📚 Documentación Adicional

- **`DOCKER_DEPLOYMENT_GUIDE.md`** - Guía completa y detallada
- **`CAMBIO_PRECIO_FRONTEND.md`** - Cómo agregar precio en el frontend
- **`FRONTEND_MOSTRAR_PRECIO.md`** - Guía completa para mostrar precios
- **`BACKEND_DESPLEGADO.md`** - Documentación del backend

---

## 🎯 Próximos Pasos

1. ✅ Iniciar Docker Desktop
2. ✅ Ejecutar `.\docker-deploy.ps1`
3. ✅ Esperar a que todos los servicios estén Up
4. ✅ Abrir http://localhost:4200
5. ✅ Verificar que el precio se muestra en cada producto
6. ✅ Probar el flujo completo de compra

---

**¡Todo listo para desplegar con Docker! 🚀**

**Recuerda:** Primero inicia Docker Desktop, luego ejecuta `.\docker-deploy.ps1`

