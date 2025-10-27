# 🐳 Guía de Despliegue con Docker Compose

## 📋 Requisitos Previos

### 1. Instalar Docker Desktop

**Windows:**
1. Descargar Docker Desktop desde: https://www.docker.com/products/docker-desktop
2. Ejecutar el instalador
3. Reiniciar el sistema
4. Abrir Docker Desktop y esperar a que inicie

**Verificar instalación:**
```powershell
docker --version
docker-compose --version
```

### 2. Configurar WSL 2 (Windows)

Docker Desktop requiere WSL 2 en Windows:
```powershell
wsl --install
wsl --set-default-version 2
```

---

## 🚀 Despliegue Rápido

### Opción 1: Script Automático (Recomendado)

```powershell
cd C:\Users\willi\OneDrive\Escritorio\prueba_linktic\microservices_linktic
.\docker-deploy.ps1
```

Este script:
- ✅ Verifica que Docker esté corriendo
- ✅ Detiene servicios locales
- ✅ Construye todas las imágenes Docker
- ✅ Levanta todos los servicios
- ✅ Espera a que estén listos
- ✅ Muestra el estado final

### Opción 2: Comandos Manuales

```powershell
# 1. Detener servicios locales
Stop-Process -Name java -Force -ErrorAction SilentlyContinue
Stop-Process -Name node -Force -ErrorAction SilentlyContinue

# 2. Construir imágenes
docker-compose build

# 3. Levantar servicios
docker-compose up -d

# 4. Ver logs
docker-compose logs -f
```

---

## 📦 Arquitectura del Despliegue

### Servicios Desplegados

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| **Frontend Angular** | 4200 | Aplicación web (Nginx) |
| **Eureka Server** | 8761 | Service Discovery |
| **Products Service** | 8081 | Gestión de productos |
| **Inventory Service** | 8082 | Gestión de inventario |
| **Orders Service** | 8083 | Gestión de órdenes |
| **Notifications Service** | 8084 | Envío de notificaciones |
| **PostgreSQL** | 5432 | Base de datos (Products, Inventory, Notifications) |
| **MySQL** | 3306 | Base de datos (Orders) |

### Red Docker

Todos los servicios están en la red `microservices-network` y pueden comunicarse entre sí usando sus nombres de servicio.

---

## 🔧 Configuración

### Variables de Entorno

Crea un archivo `.env` basado en `.env.example`:

```bash
# .env
GMAIL_APP_PASSWORD=tu-app-password-de-gmail
```

Para generar un App Password de Gmail:
1. Ve a https://myaccount.google.com/apppasswords
2. Selecciona "Correo" y "Otro (nombre personalizado)"
3. Copia el password generado
4. Pégalo en el archivo `.env`

---

## 📊 Verificar el Despliegue

### 1. Ver Estado de los Contenedores

```powershell
docker-compose ps
```

Deberías ver todos los servicios con estado `Up`:

```
NAME                    STATUS
eureka-server           Up (healthy)
products-service        Up (healthy)
inventory-service       Up (healthy)
orders-service          Up (healthy)
notifications-service   Up (healthy)
frontend-angular        Up
postgres_db             Up (healthy)
mysql_db                Up (healthy)
```

### 2. Ver Logs

```powershell
# Todos los servicios
docker-compose logs -f

# Un servicio específico
docker-compose logs -f products-service

# Últimas 100 líneas
docker-compose logs --tail=100 products-service
```

### 3. Probar los Endpoints

**Frontend:**
```
http://localhost:4200
```

**Eureka Dashboard:**
```
http://localhost:8761
```

**Products API:**
```powershell
curl http://localhost:8081/api/v1/frontend/products?page=0
```

**Health Checks:**
```powershell
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
```

---

## 🛠️ Comandos Útiles

### Gestión de Servicios

```powershell
# Iniciar todos los servicios
docker-compose up -d

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: borra datos)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart products-service

# Reconstruir un servicio
docker-compose up -d --build products-service

# Escalar un servicio (ejemplo: 3 instancias de products-service)
docker-compose up -d --scale products-service=3
```

### Logs y Debugging

```powershell
# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio
docker-compose logs -f products-service

# Ver logs desde hace 10 minutos
docker-compose logs --since 10m

# Buscar en logs
docker-compose logs | Select-String "ERROR"
```

### Acceso a Contenedores

```powershell
# Ejecutar bash en un contenedor
docker exec -it products-service sh

# Ver procesos en un contenedor
docker top products-service

# Ver estadísticas de recursos
docker stats
```

### Bases de Datos

```powershell
# Conectar a PostgreSQL
docker exec -it postgres_db psql -U postgres -d linktic_products

# Conectar a MySQL
docker exec -it mysql_db mysql -u linktic -plinktic123 linktic_orders

# Backup de PostgreSQL
docker exec postgres_db pg_dump -U postgres linktic_products > backup.sql

# Backup de MySQL
docker exec mysql_db mysqldump -u linktic -plinktic123 linktic_orders > backup.sql
```

---

## 🧪 Probar el Sistema Completo

### 1. Abrir el Frontend

```
http://localhost:4200
```

### 2. Verificar que se Muestran los Productos

- Deberías ver 6 productos por página
- Cada producto debe mostrar: nombre, descripción, **precio**, SKU
- Total: 15 productos en 3 páginas

### 3. Probar "Ver Detalles"

- Click en "Ver Detalles" de cualquier producto
- Debe mostrar información completa incluyendo el precio

### 4. Probar "Comprar"

- Click en "Comprar"
- Se debe crear una orden
- Verificar en logs que:
  - Orders Service procesa la orden
  - Products Service es consultado
  - Inventory Service valida stock
  - Notifications Service envía email (si está configurado)

### 5. Verificar Eureka

```
http://localhost:8761
```

Deberías ver todos los servicios registrados:
- PRODUCTS-SERVICE
- INVENTORY-SERVICE
- ORDERS-SERVICE
- NOTIFICATIONS-SERVICE

---

## 🐛 Troubleshooting

### Problema: Servicios no inician

**Solución:**
```powershell
# Ver logs del servicio que falla
docker-compose logs products-service

# Verificar que Docker tiene suficiente memoria (mínimo 4GB)
# Docker Desktop > Settings > Resources > Memory
```

### Problema: Puerto ya en uso

**Solución:**
```powershell
# Ver qué proceso usa el puerto
netstat -ano | findstr :8081

# Detener el proceso
Stop-Process -Id [PID] -Force

# O cambiar el puerto en docker-compose.yml
```

### Problema: Base de datos no se conecta

**Solución:**
```powershell
# Verificar que las bases de datos estén healthy
docker-compose ps

# Ver logs de la base de datos
docker-compose logs postgres
docker-compose logs mysql

# Reiniciar las bases de datos
docker-compose restart postgres mysql
```

### Problema: Frontend no carga

**Solución:**
```powershell
# Ver logs del frontend
docker-compose logs frontend

# Verificar que el build de Angular fue exitoso
docker-compose build frontend

# Reconstruir y reiniciar
docker-compose up -d --build frontend
```

### Problema: Servicios no se registran en Eureka

**Solución:**
```powershell
# Esperar 60 segundos (tiempo de registro)
Start-Sleep -Seconds 60

# Verificar logs de Eureka
docker-compose logs eureka-server

# Verificar logs del servicio
docker-compose logs products-service
```

---

## 🔄 Actualizar el Código

Cuando hagas cambios en el código:

```powershell
# 1. Recompilar el JAR (si es backend)
cd products_service
mvnw clean package -DskipTests

# 2. Reconstruir la imagen Docker
cd ..
docker-compose build products-service

# 3. Reiniciar el servicio
docker-compose up -d products-service

# 4. Ver logs
docker-compose logs -f products-service
```

---

## 🧹 Limpieza

### Limpiar Todo

```powershell
# Detener y eliminar contenedores, redes, volúmenes
docker-compose down -v

# Eliminar imágenes no usadas
docker image prune -a

# Eliminar todo (CUIDADO)
docker system prune -a --volumes
```

---

## 📝 Notas Importantes

1. **Primera ejecución**: La primera vez que ejecutes `docker-compose build` tomará varios minutos porque descarga todas las imágenes base y dependencias.

2. **Memoria**: Asegúrate de que Docker Desktop tenga al menos 4GB de RAM asignados.

3. **Puertos**: Verifica que los puertos 4200, 8761, 8081-8084, 5432, 3306 estén libres.

4. **Datos**: Los datos de las bases de datos se persisten en volúmenes Docker. Para eliminarlos usa `docker-compose down -v`.

5. **Logs**: Los logs se pueden ver con `docker-compose logs -f`. Son útiles para debugging.

---

## ✅ Checklist de Despliegue

- [ ] Docker Desktop instalado y corriendo
- [ ] WSL 2 configurado (Windows)
- [ ] Puertos libres (4200, 8761, 8081-8084, 5432, 3306)
- [ ] Archivo `.env` creado con GMAIL_APP_PASSWORD
- [ ] Ejecutar `docker-compose build`
- [ ] Ejecutar `docker-compose up -d`
- [ ] Esperar 2-3 minutos
- [ ] Verificar con `docker-compose ps`
- [ ] Abrir http://localhost:4200
- [ ] Verificar Eureka en http://localhost:8761
- [ ] Probar endpoints de productos
- [ ] Probar flujo de compra

---

**¡Listo! Tu aplicación fullstack está corriendo en Docker! 🚀**

