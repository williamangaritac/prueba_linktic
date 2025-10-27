# 🔄 Instrucciones para Sincronizar los 3 Microservicios

## 📋 Resumen
Los tres microservicios deben usar el **mismo formato de SKU con UUIDs** para mantener la consistencia de datos.

---

## 🗂️ SKUs Sincronizados (Formato UUID)

| SKU (UUID) | Producto | Precio | Inventario |
|------------|----------|--------|------------|
| `29444ed7a8f8495587365a6b61458735` | Laptop Dell XPS 13 | $1299.99 | 150 |
| `721ee031b6dd421ca59cd23d712e8438` | Mouse Logitech MX Master 3 | $99.99 | 50 |
| `15270af906f54eaca4282559e80f8c06` | Keyboard Mechanical RGB | $149.99 | 80 |
| `ddca28786303459bb843c30c22d5fc31` | Monitor LG 27 inch 4K | $499.99 | 30 |
| `09eebaa32c89416bab0812fef5c0f4a5` | Webcam HD 1080p | $79.99 | 210 |
| `d55281848baa45798c2f1252f28fc60f` | USB-C Hub 7 in 1 | $49.99 | 90 |
| `2029b6f777e54c9db3a6d104d6edfc6b` | Headphones Sony WH-1000XM4 | $349.99 | 70 |
| `d2cc00ae1c1340c9bf0c28a394faad7a` | External SSD 1TB | $129.99 | 300 |
| `2a9d998f6bba4255977db00efcb6ba56` | Laptop Stand Adjustable | $39.99 | 120 |
| `94eb9162015e4b72ac6d65787593e803` | USB-C Cable 2m | $19.99 | 50 |
| `1741cdceb0294096ab9623ae1dde3cbf` | Wireless Charger 15W | $29.99 | 8 |
| `9cb2460d43c24e79ba933563d57184e1` | Bluetooth Speaker | $89.99 | 22 |
| `dc9f356c6db74fe39260985e0dadec18` | Smart Watch | $199.99 | 180 |
| `59005a7de9e34745ac2629c5f47bcfde` | Tablet 10 inch | $299.99 | 40 |
| `37b12eaf9013415a99f77f27a84a1c04` | Power Bank 20000mAh | $49.99 | 650 |

---

## 📝 Pasos de Ejecución

### **PASO 1: Actualizar products_service (PostgreSQL)**

**Archivo:** `products_service_fix_sku_uuid.sql`

**Ejecutar en pgAdmin o terminal PostgreSQL:**

```bash
# Opción 1: Desde terminal PostgreSQL
psql -U postgres -d linktic_products -f products_service_fix_sku_uuid.sql

# Opción 2: Copiar y pegar en pgAdmin
# Abrir pgAdmin -> linktic_products -> Query Tool -> Pegar contenido del archivo -> Ejecutar
```

**Verificar:**
```sql
SELECT sku, name, price FROM products ORDER BY id;
```

---

### **PASO 2: Verificar inventory_service (PostgreSQL)**

**El inventory_service YA tiene los SKUs correctos en formato UUID.**

**Verificar en pgAdmin:**
```sql
SELECT sku, quantity FROM inventory ORDER BY id;
```

**Si necesitas recrear los datos:**
```sql
DELETE FROM inventory;
ALTER SEQUENCE inventory_id_seq RESTART WITH 1;

-- Luego ejecutar el contenido de: inventory_service/insercion de datos invenotry.sql
```

---

### **PASO 3: Actualizar orders_service (MySQL)**

**Archivo:** `orders_service_test_data.sql`

**Ejecutar en phpMyAdmin:**

1. Abrir phpMyAdmin
2. Seleccionar base de datos `linktic_orders`
3. Ir a pestaña "SQL"
4. Copiar y pegar el contenido completo de `orders_service_test_data.sql`
5. Hacer clic en "Continuar" o "Go"

**Verificar:**
```sql
-- Ver resumen de órdenes
SELECT 
    o.id,
    o.order_number,
    COUNT(oi.id) AS items_count,
    SUM(oi.price * oi.quantity) AS total_amount
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.order_number
ORDER BY o.id;

-- Ver detalle de items
SELECT 
    o.order_number,
    oi.sku,
    oi.price,
    oi.quantity,
    (oi.price * oi.quantity) AS subtotal
FROM orders o
INNER JOIN order_items oi ON o.id = oi.order_id
ORDER BY o.id, oi.id;
```

---

## ✅ Verificación Final

### **1. Verificar que los SKUs coincidan en los 3 microservicios:**

**PostgreSQL (products_service):**
```sql
SELECT COUNT(*) FROM products WHERE sku = '29444ed7a8f8495587365a6b61458735';
-- Debe retornar: 1
```

**PostgreSQL (inventory_service):**
```sql
SELECT COUNT(*) FROM inventory WHERE sku = '29444ed7a8f8495587365a6b61458735';
-- Debe retornar: 1
```

**MySQL (orders_service):**
```sql
SELECT COUNT(*) FROM order_items WHERE sku = '29444ed7a8f8495587365a6b61458735';
-- Debe retornar: 5 (aparece en 5 órdenes diferentes)
```

### **2. Verificar integridad referencial:**

Todos los SKUs en `order_items` deben existir en `products` e `inventory`:

```sql
-- En MySQL (orders_service)
SELECT DISTINCT oi.sku 
FROM order_items oi
ORDER BY oi.sku;

-- Comparar con los SKUs en products e inventory
```

---

## 🚀 Iniciar Microservicios

Una vez sincronizados los datos, iniciar los microservicios en orden:

### **1. products_service (Puerto 8081)**
```powershell
cd products_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -jar target/products_service-0.0.1-SNAPSHOT.jar
```
**Swagger:** http://localhost:8081/api/v1/swagger-ui.html

### **2. inventory_service (Puerto 8082)**
```powershell
cd inventory_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -jar target/inventory_service-0.0.1-SNAPSHOT.jar
```
**Swagger:** http://localhost:8082/api/v1/swagger-ui.html

### **3. orders_service (Puerto 8083)**
```powershell
cd orders_service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -jar target/orders_service-0.0.1-SNAPSHOT.jar
```
**Swagger:** http://localhost:8083/api/v1/swagger-ui.html

---

## 📊 Datos de Prueba en orders_service

El script crea **10 órdenes** con los siguientes totales aproximados:

- **ORD-2024-001**: $1,499.97 (Laptop + 2 Mouse)
- **ORD-2024-002**: $649.98 (Keyboard + Monitor)
- **ORD-2024-003**: $699.98 (2 Headphones)
- **ORD-2024-004**: $229.95 (3 USB-C Hub + 2 Laptop Stand)
- **ORD-2024-005**: $259.98 (2 External SSD)
- **ORD-2024-006**: $399.92 (3 Mouse + 5 USB-C Cable)
- **ORD-2024-007**: $3,699.92 (Setup oficina completo x2)
- **ORD-2024-008**: $1,749.68 (Accesorios múltiples)
- **ORD-2024-009**: $669.87 (Componentes PC)
- **ORD-2024-010**: $2,617.91 (Setup personal completo)

**Total de items:** 26 registros en `order_items`

---

## 🔧 Troubleshooting

### Error: "SKU no encontrado"
- Verificar que el SKU existe en `products` e `inventory`
- Verificar que el formato UUID es exacto (32 caracteres hexadecimales)

### Error: "Conexión a base de datos"
- Verificar que PostgreSQL está corriendo (puerto 5432)
- Verificar que MySQL está corriendo (puerto 3306)
- Verificar credenciales en `application.yml`

### Error: "Puerto ya en uso"
- Verificar que no hay otra instancia del microservicio corriendo
- Usar `netstat -an | findstr :808X` para verificar puertos

---

## ✨ Resultado Final

Después de ejecutar todos los scripts, tendrás:

- ✅ **15 productos** sincronizados en `products_service`
- ✅ **15 registros de inventario** sincronizados en `inventory_service`
- ✅ **10 órdenes** con **26 items** en `orders_service`
- ✅ **Todos usando el mismo formato de SKU UUID**
- ✅ **Integridad referencial completa** entre los 3 microservicios
