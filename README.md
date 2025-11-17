# 🛍️ Sistema de E-Commerce con Microservicios - Prueba Técnica Linktic

Sistema completo de e-commerce desarrollado con arquitectura de microservicios, implementando Spring Boot para el backend, Angular para el frontend, y Docker para el despliegue.

## 📋 Tabla de Contenidos

- [Descripción General](#-descripción-general)
- [Arquitectura](#-arquitectura)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Pruebas](#-pruebas)
- [Documentación API](#-documentación-api)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Características Principales](#-características-principales)

## 🎯 Descripción General

Este proyecto implementa un sistema de e-commerce completo con las siguientes funcionalidades:

- **Catálogo de Productos**: Visualización y gestión de productos
- **Gestión de Inventario**: Control de stock en tiempo real
- **Procesamiento de Órdenes**: Creación y seguimiento de pedidos
- **Sistema de Notificaciones**: Notificaciones por email usando Kafka
- **API Gateway**: Punto de entrada único para todos los servicios
- **Service Discovery**: Registro y descubrimiento de servicios con Eureka

## 🏗️ Arquitectura

El sistema está construido con una arquitectura de microservicios:

```
┌─────────────────┐
│  Frontend       │
│  (Angular 18)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  API Gateway    │
│  (Port 8080)    │
└────────┬────────┘
         │
    ┌────┴────┬──────────┬──────────────┐
    ▼         ▼          ▼              ▼
┌─────────┐ ┌──────┐ ┌────────┐ ┌──────────────┐
│Products │ │Orders│ │Inventory│ │Notifications│
│Service  │ │Service│ │Service  │ │Service       │
│:8081    │ │:8082 │ │:8083    │ │:8084         │
└────┬────┘ └───┬──┘ └────┬────┘ └──────┬───────┘
     │          │         │              │
     ▼          ▼         ▼              ▼
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│PostgreSQL│ │MySQL    │ │PostgreSQL│ │PostgreSQL│
└─────────┘ └─────────┘ └─────────┘ └─────────┘
                │
                ▼
           ┌─────────┐
           │  Kafka  │
           └─────────┘
```

### Microservicios

1. **Eureka Server** (Puerto 8761): Service Discovery
2. **API Gateway** (Puerto 8080): Enrutamiento y balanceo de carga
3. **Products Service** (Puerto 8081): Gestión de productos
4. **Orders Service** (Puerto 8082): Procesamiento de órdenes
5. **Inventory Service** (Puerto 8083): Control de inventario
6. **Notifications Service** (Puerto 8084): Envío de notificaciones

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud** (Gateway, Netflix Eureka)
- **Spring Data JPA**
- **Apache Kafka** (Mensajería asíncrona)
- **PostgreSQL** (Products, Inventory, Notifications)
- **MySQL** (Orders)
- **Maven** (Gestión de dependencias)
- **JUnit 5 & Mockito** (Testing)

### Frontend
- **Angular 18**
- **TypeScript**
- **Tailwind CSS**
- **RxJS**
- **Clean Architecture**

### DevOps
- **Docker & Docker Compose**
- **Nginx** (Servidor web para frontend)

## 📦 Requisitos Previos

Asegúrate de tener instalado:

- **Docker Desktop** (versión 20.10 o superior)
- **Docker Compose** (versión 2.0 o superior)
- **Git**
- **Java 17** (solo para desarrollo local sin Docker)
- **Maven 3.8+** (solo para desarrollo local sin Docker)
- **Node.js 18+** y **npm** (solo para desarrollo local sin Docker)

### Verificar instalación:

```bash
docker --version
docker-compose --version
git --version
```

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/williamangaritac/prueba_linktic.git
cd prueba_linktic
```

### 2. Configuración de Variables de Entorno (Opcional)

Puedes crear un archivo `.env` en la raíz del proyecto para personalizar las configuraciones:

```env
# Ejemplo de configuración
SPRING_PROFILES_ACTIVE=docker
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

## 🎮 Ejecución del Proyecto

### Opción 1: Despliegue Completo con Docker (Recomendado)

Esta es la forma más sencilla de ejecutar todo el sistema:

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# O en modo detached (segundo plano)
docker-compose up --build -d
```

**Tiempo estimado de inicio**: 3-5 minutos

### Opción 2: Usando el Script de PowerShell

```powershell
# En Windows PowerShell
.\docker-deploy.ps1
```

### 🗄️ Inicialización Automática de Bases de Datos

**¡IMPORTANTE!** Las bases de datos se inicializan automáticamente con datos de prueba la primera vez que ejecutas el proyecto:

#### PostgreSQL (3 bases de datos):
- **linktic_products**: 15 productos precargados (Soluciones de software empresarial)
- **linktic_inventory**: Inventario inicial para cada producto (50-150 unidades)
- **linktic_notifications**: Tabla lista para almacenar notificaciones

#### MySQL (1 base de datos):
- **linktic_orders**: Tablas de órdenes y order_items listas para usar

**Los datos persisten** gracias a los volúmenes de Docker (`postgres_data` y `mysql_data`), por lo que no se pierden al reiniciar los contenedores.

#### Para reiniciar con datos frescos:

```bash
# Detener y eliminar volúmenes (esto borra todos los datos)
docker-compose down -v

# Volver a levantar (se reinicializarán las bases de datos)
docker-compose up --build
```

### Verificar que los servicios están corriendo

```bash
# Ver el estado de los contenedores
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f products-service
```

## 🌐 Acceso a los Servicios

Una vez que todos los contenedores estén corriendo:

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Frontend** | http://localhost:4200 | Aplicación Angular |
| **API Gateway** | http://localhost:8080 | Punto de entrada API |
| **Eureka Dashboard** | http://localhost:8761 | Service Discovery |
| **Products API** | http://localhost:8080/products-service/api/products | API de Productos |
| **Orders API** | http://localhost:8080/orders-service/api/orders | API de Órdenes |
| **Inventory API** | http://localhost:8080/inventory-service/api/inventory | API de Inventario |
| **Notifications API** | http://localhost:8080/notifications-service/api/notifications | API de Notificaciones |

### Swagger UI (Documentación API)

- **Products Service**: http://localhost:8081/swagger-ui.html
- **Orders Service**: http://localhost:8082/swagger-ui.html
- **Inventory Service**: http://localhost:8083/swagger-ui.html
- **Notifications Service**: http://localhost:8084/swagger-ui.html

## 📊 Datos de Prueba Precargados

El sistema viene con datos de prueba listos para usar:

### Productos (15 productos disponibles):

| SKU | Nombre | Precio | Stock Inicial |
|-----|--------|--------|---------------|
| 29444ed7a8f8495587365a6b61458735 | Solución E-commerce | $2,805.00 | 100 unidades |
| a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6 | Sistema de Gestión de Inventario | $1,850.00 | 150 unidades |
| b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7 | CRM Empresarial | $3,200.00 | 80 unidades |
| c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8 | Sistema de Facturación | $1,500.00 | 120 unidades |
| ... | ... | ... | ... |

**Total**: 15 productos con inventario entre 50-150 unidades cada uno.

### Cómo probar el sistema:

1. **Ver productos**: Abre http://localhost:4200 en tu navegador
2. **Crear una orden**: Selecciona productos y completa el formulario de compra
3. **Verificar inventario**: El stock se actualiza automáticamente
4. **Ver notificaciones**: Revisa la tabla `notifications` en PostgreSQL

### Acceso directo a las bases de datos:

```bash
# PostgreSQL (Products, Inventory, Notifications)
docker exec -it postgres_db psql -U postgres -d linktic_products
docker exec -it postgres_db psql -U postgres -d linktic_inventory
docker exec -it postgres_db psql -U postgres -d linktic_notifications

# MySQL (Orders)
docker exec -it mysql_db mysql -u linktic -plinktic123 linktic_orders
```

### Consultas útiles:

```sql
-- Ver todos los productos
SELECT * FROM products;

-- Ver inventario disponible
SELECT * FROM inventory;

-- Ver órdenes creadas
SELECT * FROM orders;

-- Ver notificaciones enviadas
SELECT * FROM notifications;
```

## 🧪 Pruebas

### Ejecutar Pruebas Unitarias

#### Todos los servicios:

```bash
# Con Docker
docker-compose run --rm products-service mvn test
docker-compose run --rm orders-service mvn test
docker-compose run --rm inventory-service mvn test
docker-compose run --rm notifications-service mvn test
```

#### Localmente (sin Docker):

```bash
# Navegar a cada servicio y ejecutar
cd microservices_linktic/products_service
mvn test

cd ../orders_service
mvn test

cd ../inventory_service
mvn test

cd ../notifications_service
mvn test
```

### Cobertura de Pruebas

El proyecto incluye pruebas unitarias para:
- ✅ Controladores (Controllers)
- ✅ Servicios (Services)
- ✅ Repositorios (Repositories)
- ✅ Consumidores Kafka (Kafka Consumers)

## 📚 Documentación API

### Endpoints Principales

#### Products Service

```http
GET    /api/products              # Listar todos los productos
GET    /api/products/{id}         # Obtener producto por ID
POST   /api/products              # Crear nuevo producto
PUT    /api/products/{id}         # Actualizar producto
DELETE /api/products/{id}         # Eliminar producto
```

#### Orders Service

```http
GET    /api/orders                # Listar todas las órdenes
GET    /api/orders/{id}           # Obtener orden por ID
POST   /api/orders                # Crear nueva orden
PUT    /api/orders/{id}           # Actualizar orden
```

#### Inventory Service

```http
GET    /api/inventory             # Listar inventario
GET    /api/inventory/{sku}       # Obtener stock por SKU
POST   /api/inventory/check       # Verificar disponibilidad
PUT    /api/inventory/update      # Actualizar stock
```

#### Notifications Service

```http
GET    /api/notifications         # Listar notificaciones
GET    /api/notifications/{id}    # Obtener notificación por ID
```

### Ejemplo de Petición: Crear Orden

```bash
curl -X POST http://localhost:8080/orders-service/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Juan Pérez",
    "customerEmail": "juan@example.com",
    "items": [
      {
        "sku": "PROD-001",
        "quantity": 2,
        "price": 29.99
      }
    ]
  }'
```

## 📁 Estructura del Proyecto

```
prueba_linktic/
├── frontend_angular/              # Aplicación Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/             # Lógica de negocio
│   │   │   ├── infrastructure/   # Repositorios
│   │   │   └── presentation/     # Componentes UI
│   │   └── environments/
│   ├── Dockerfile
│   └── nginx.conf
│
├── microservices_linktic/
│   ├── eureka-server/            # Service Discovery
│   ├── api-gateway/              # API Gateway
│   ├── products_service/         # Servicio de Productos
│   ├── orders_service/           # Servicio de Órdenes
│   ├── inventory_service/        # Servicio de Inventario
│   ├── notifications_service/    # Servicio de Notificaciones
│   ├── init-mysql.sql           # Script inicialización MySQL
│   └── init-postgres.sql        # Script inicialización PostgreSQL
│
├── docker-compose.yml            # Orquestación de contenedores
├── docker-deploy.ps1            # Script de despliegue Windows
└── README.md                    # Este archivo
```

## ✨ Características Principales

### 1. **Arquitectura Limpia en Frontend**
- Separación de capas (Core, Infrastructure, Presentation)
- Casos de uso bien definidos
- Inyección de dependencias

### 2. **Comunicación Asíncrona**
- Kafka para eventos de órdenes
- Notificaciones en tiempo real

### 3. **Resiliencia**
- Circuit Breaker en API Gateway
- Retry policies
- Fallback endpoints

### 4. **Escalabilidad**
- Microservicios independientes
- Bases de datos separadas
- Service Discovery automático

### 5. **Observabilidad**
- Logs estructurados
- Eureka Dashboard
- Swagger para documentación

## 🛑 Detener los Servicios

```bash
# Detener todos los contenedores
docker-compose down

# Detener y eliminar volúmenes (limpieza completa)
docker-compose down -v

# Detener y eliminar imágenes
docker-compose down --rmi all
```

## 🐛 Solución de Problemas

### Los servicios no se registran en Eureka

```bash
# Verificar que Eureka esté corriendo
docker-compose logs eureka-server

# Reiniciar servicios
docker-compose restart
```

### Error de conexión a base de datos

```bash
# Verificar que las bases de datos estén corriendo
docker-compose ps

# Ver logs de la base de datos
docker-compose logs postgres
docker-compose logs mysql
```

### Puerto ya en uso

```bash
# Verificar puertos en uso
netstat -ano | findstr :8080

# Cambiar puertos en docker-compose.yml si es necesario
```

## 📞 Contacto

**Desarrollador**: William Angarita  
**GitHub**: https://github.com/williamangaritac

## 📄 Licencia

Este proyecto fue desarrollado como prueba técnica para Linktic.

---

**¡Gracias por revisar este proyecto!** 🚀

