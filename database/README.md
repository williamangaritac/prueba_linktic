# 🗄️ Base de Datos - Linktic Store

Esta carpeta contiene toda la configuración e inicialización de las bases de datos del proyecto.

## 📋 Estructura de Bases de Datos

El proyecto utiliza **2 motores de bases de datos**:

### PostgreSQL (3 bases de datos)
- **linktic_products** - Catálogo de productos
- **linktic_inventory** - Gestión de inventario
- **linktic_notifications** - Registro de notificaciones

### MySQL (1 base de datos)
- **linktic_orders** - Gestión de órdenes y pedidos

---

## 🚀 Inicio Rápido

### 1️⃣ Levantar solo las bases de datos

```bash
# Desde la raíz del proyecto
docker-compose -f docker-compose.db.yml up -d
```

### 2️⃣ Verificar que estén corriendo

```bash
docker-compose -f docker-compose.db.yml ps
```

### 3️⃣ Ver logs

```bash
# Todos los servicios
docker-compose -f docker-compose.db.yml logs -f

# Solo PostgreSQL
docker-compose -f docker-compose.db.yml logs -f postgres

# Solo MySQL
docker-compose -f docker-compose.db.yml logs -f mysql
```

### 4️⃣ Detener las bases de datos

```bash
docker-compose -f docker-compose.db.yml down
```

### 5️⃣ Detener y eliminar volúmenes (⚠️ BORRA TODOS LOS DATOS)

```bash
docker-compose -f docker-compose.db.yml down -v
```

---

## 🔌 Conexión a las Bases de Datos

### PostgreSQL

| Parámetro | Valor |
|-----------|-------|
| **Host** | localhost |
| **Puerto** | 5432 |
| **Usuario** | linktic_user |
| **Contraseña** | linktic_password_2024 |
| **Bases de datos** | linktic_products<br>linktic_inventory<br>linktic_notifications |

**Cadena de conexión:**
```
postgresql://linktic_user:linktic_password_2024@localhost:5432/linktic_products
postgresql://linktic_user:linktic_password_2024@localhost:5432/linktic_inventory
postgresql://linktic_user:linktic_password_2024@localhost:5432/linktic_notifications
```

### MySQL

| Parámetro | Valor |
|-----------|-------|
| **Host** | localhost |
| **Puerto** | 3306 |
| **Usuario** | linktic_user |
| **Contraseña** | linktic_password_2024 |
| **Base de datos** | linktic_orders |
| **Root Password** | root_password_2024 |

**Cadena de conexión:**
```
mysql://linktic_user:linktic_password_2024@localhost:3306/linktic_orders
```

---

## 🖥️ Adminer - Interfaz Web

Adminer es una herramienta web para gestionar las bases de datos.

**URL:** http://localhost:8090

### Conectar a PostgreSQL
- **Sistema:** PostgreSQL
- **Servidor:** postgres
- **Usuario:** linktic_user
- **Contraseña:** linktic_password_2024
- **Base de datos:** linktic_products (o cualquier otra)

### Conectar a MySQL
- **Sistema:** MySQL
- **Servidor:** mysql
- **Usuario:** linktic_user
- **Contraseña:** linktic_password_2024
- **Base de datos:** linktic_orders

---

## 📁 Estructura de Archivos

```
database/
├── README.md                          # Este archivo
├── init-scripts/                      # Scripts de inicialización
│   ├── init-postgres.sql             # Crea DBs y tablas PostgreSQL
│   └── init-mysql.sql                # Crea tablas MySQL
└── docs/                             # Documentación adicional
    └── CONFIGURAR_EMAIL.md           # Guía para configurar Gmail
```

---

## 📊 Esquema de Datos

### Products (PostgreSQL)
```sql
- id (BIGSERIAL)
- sku (VARCHAR UNIQUE)
- name (VARCHAR)
- description (TEXT)
- price (DECIMAL)
- status (BOOLEAN)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### Inventory (PostgreSQL)
```sql
- id (BIGSERIAL)
- sku (VARCHAR UNIQUE)
- quantity (BIGINT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### Orders (MySQL)
```sql
- id (BIGINT AUTO_INCREMENT)
- order_number (VARCHAR UNIQUE)
- total_amount (DECIMAL)
- status (VARCHAR)
- customer_email (VARCHAR)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

### Order Items (MySQL)
```sql
- id (BIGINT AUTO_INCREMENT)
- order_id (BIGINT FK)
- sku (VARCHAR)
- product_name (VARCHAR)
- price (DECIMAL)
- quantity (INT)
- subtotal (DECIMAL)
```

### Notifications (PostgreSQL)
```sql
- id (BIGSERIAL)
- order_number (VARCHAR)
- customer_email (VARCHAR)
- subject (VARCHAR)
- message (TEXT)
- status (VARCHAR)
- sent_at (TIMESTAMP)
- created_at (TIMESTAMP)
```

---

## 🔧 Troubleshooting

### Error: Puerto ya en uso

Si el puerto 5432 o 3306 ya está en uso:

```bash
# Ver qué proceso usa el puerto
netstat -ano | findstr :5432
netstat -ano | findstr :3306

# Cambiar el puerto en .env
POSTGRES_PORT=5433
MYSQL_PORT=3307
```

### Error: No se puede conectar

1. Verificar que los contenedores estén corriendo
2. Verificar los logs: `docker-compose -f docker-compose.db.yml logs`
3. Reiniciar los contenedores: `docker-compose -f docker-compose.db.yml restart`

---

## 📝 Datos de Prueba

Los scripts de inicialización incluyen **15 productos de prueba** con su inventario correspondiente.

Para ver los datos:

```sql
-- PostgreSQL
SELECT * FROM products LIMIT 10;
SELECT * FROM inventory LIMIT 10;

-- MySQL
SELECT * FROM orders;
```

