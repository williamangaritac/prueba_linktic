# 📊 Datos de Prueba - Sistema E-Commerce Linktic

Este documento describe todos los datos de prueba que se cargan automáticamente cuando despliegas el proyecto.

## 🗄️ Bases de Datos

### PostgreSQL (3 bases de datos)

#### 1. **linktic_products** - Catálogo de Productos

**Tabla**: `products`

| ID | SKU | Nombre | Descripción | Precio | Stock |
|----|-----|--------|-------------|--------|-------|
| 1 | 29444ed7a8f8495587365a6b61458735 | Solución E-commerce | Plataforma completa de comercio electrónico con gestión de inventario, pagos y envíos | $2,805.00 | 100 |
| 2 | a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6 | Sistema de Gestión de Inventario | Control total de tu inventario en tiempo real | $1,850.00 | 150 |
| 3 | b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7 | CRM Empresarial | Gestión de relaciones con clientes | $3,200.00 | 80 |
| 4 | c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8 | Sistema de Facturación | Facturación electrónica automatizada | $1,500.00 | 120 |
| 5 | d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9 | Portal de Empleados | Gestión de recursos humanos | $2,100.00 | 90 |
| 6 | e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0 | Sistema de Reportes | Análisis y reportes empresariales | $1,750.00 | 110 |
| 7 | f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1 | Plataforma de Marketing | Automatización de marketing digital | $2,950.00 | 70 |
| 8 | g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2 | Sistema de Logística | Gestión de envíos y entregas | $2,400.00 | 95 |
| 9 | h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3 | Portal de Clientes | Autoservicio para clientes | $1,650.00 | 130 |
| 10 | i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4 | Sistema de Soporte | Mesa de ayuda y tickets | $1,900.00 | 85 |
| 11 | j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5 | Plataforma de Capacitación | E-learning corporativo | $2,200.00 | 105 |
| 12 | k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6 | Sistema de Calidad | Control de calidad y auditorías | $1,800.00 | 75 |
| 13 | l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7 | Portal de Proveedores | Gestión de proveedores | $1,550.00 | 140 |
| 14 | m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8 | Sistema de Proyectos | Gestión de proyectos ágiles | $2,650.00 | 60 |
| 15 | n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9 | Plataforma de BI | Business Intelligence avanzado | $3,500.00 | 50 |

**Total de productos**: 15  
**Valor total del inventario**: ~$32,405.00  
**Stock total**: 1,460 unidades

---

#### 2. **linktic_inventory** - Control de Inventario

**Tabla**: `inventory`

Cada producto tiene su registro de inventario correspondiente con las cantidades mostradas en la tabla anterior.

**Estructura**:
```sql
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

#### 3. **linktic_notifications** - Sistema de Notificaciones

**Tabla**: `notifications`

Esta tabla se crea vacía y se llena automáticamente cuando se crean órdenes.

**Estructura**:
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Estados posibles**:
- `PENDING`: Notificación pendiente de envío
- `SENT`: Notificación enviada exitosamente
- `FAILED`: Error al enviar notificación

---

### MySQL (1 base de datos)

#### **linktic_orders** - Gestión de Órdenes

**Tabla 1**: `orders`

Esta tabla se crea vacía y se llena cuando los usuarios crean órdenes desde el frontend.

**Estructura**:
```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(255) UNIQUE NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    customer_email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Estados de orden**:
- `PENDING`: Orden creada, pendiente de procesamiento
- `CONFIRMED`: Orden confirmada
- `PROCESSING`: Orden en proceso
- `SHIPPED`: Orden enviada
- `DELIVERED`: Orden entregada
- `CANCELLED`: Orden cancelada

---

**Tabla 2**: `order_items`

Almacena los items de cada orden.

**Estructura**:
```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    sku VARCHAR(255) NOT NULL,
    product_name VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

---

## 🔄 Flujo de Datos

### Cuando se crea una orden:

1. **Frontend** → Envía petición POST a `/api/orders`
2. **Orders Service** → Valida y crea la orden en MySQL
3. **Orders Service** → Verifica stock en Inventory Service
4. **Inventory Service** → Reduce el stock en PostgreSQL
5. **Orders Service** → Publica evento en Kafka
6. **Notifications Service** → Consume evento de Kafka
7. **Notifications Service** → Crea registro en tabla `notifications`
8. **Notifications Service** → Envía email al cliente (si está configurado)

---

## 🧪 Casos de Prueba Sugeridos

### 1. Crear una orden simple

```bash
curl -X POST http://localhost:8080/orders-service/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Juan Pérez",
    "customerEmail": "juan@example.com",
    "items": [
      {
        "sku": "29444ed7a8f8495587365a6b61458735",
        "quantity": 2,
        "price": 2805.00
      }
    ]
  }'
```

**Resultado esperado**:
- ✅ Orden creada en MySQL
- ✅ Stock reducido de 100 a 98 unidades
- ✅ Notificación creada en PostgreSQL
- ✅ Email enviado (si SMTP está configurado)

---

### 2. Verificar stock insuficiente

```bash
curl -X POST http://localhost:8080/orders-service/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "María García",
    "customerEmail": "maria@example.com",
    "items": [
      {
        "sku": "n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9",
        "quantity": 100,
        "price": 3500.00
      }
    ]
  }'
```

**Resultado esperado**:
- ❌ Error: Stock insuficiente (solo hay 50 unidades)
- ✅ No se crea la orden
- ✅ Stock no se modifica

---

### 3. Orden con múltiples productos

```bash
curl -X POST http://localhost:8080/orders-service/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Carlos López",
    "customerEmail": "carlos@example.com",
    "items": [
      {
        "sku": "29444ed7a8f8495587365a6b61458735",
        "quantity": 1,
        "price": 2805.00
      },
      {
        "sku": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
        "quantity": 2,
        "price": 1850.00
      }
    ]
  }'
```

**Resultado esperado**:
- ✅ Orden creada con 2 items
- ✅ Total: $6,505.00
- ✅ Stock actualizado para ambos productos
- ✅ Una notificación enviada

---

## 🔍 Consultas SQL Útiles

### PostgreSQL - Productos más vendidos

```sql
-- Conectar a linktic_products
\c linktic_products

-- Ver productos ordenados por precio
SELECT sku, name, price, status 
FROM products 
ORDER BY price DESC;

-- Ver productos con bajo stock
SELECT p.name, i.quantity 
FROM products p
JOIN inventory i ON p.sku = i.sku
WHERE i.quantity < 70
ORDER BY i.quantity ASC;
```

### PostgreSQL - Estado del inventario

```sql
-- Conectar a linktic_inventory
\c linktic_inventory

-- Ver inventario total
SELECT SUM(quantity) as total_units FROM inventory;

-- Ver productos con más stock
SELECT sku, quantity 
FROM inventory 
ORDER BY quantity DESC 
LIMIT 5;
```

### MySQL - Análisis de órdenes

```sql
-- Usar base de datos
USE linktic_orders;

-- Ver todas las órdenes
SELECT * FROM orders ORDER BY created_at DESC;

-- Ver total de ventas
SELECT SUM(total_amount) as total_sales FROM orders;

-- Ver órdenes por estado
SELECT status, COUNT(*) as count, SUM(total_amount) as total
FROM orders
GROUP BY status;

-- Ver productos más vendidos
SELECT sku, product_name, SUM(quantity) as total_sold
FROM order_items
GROUP BY sku, product_name
ORDER BY total_sold DESC;
```

---

## 🔄 Reiniciar Datos

Si necesitas volver a los datos iniciales:

```bash
# Detener y eliminar volúmenes
docker-compose down -v

# Volver a levantar (reinicializa las bases de datos)
docker-compose up --build
```

**⚠️ ADVERTENCIA**: Esto eliminará TODAS las órdenes y notificaciones creadas durante las pruebas.

---

## 📝 Notas Importantes

1. **Persistencia**: Los datos persisten entre reinicios de contenedores gracias a los volúmenes Docker
2. **Inicialización**: Los scripts SQL solo se ejecutan la primera vez que se crean los volúmenes
3. **Sincronización**: Los SKUs deben coincidir entre Products e Inventory para que funcione correctamente
4. **Kafka**: Las notificaciones se envían de forma asíncrona a través de Kafka
5. **SMTP**: Para recibir emails reales, configura las credenciales SMTP en `docker-compose.yml`

---

**Última actualización**: 2025-10-27  
**Versión**: 1.0.0

