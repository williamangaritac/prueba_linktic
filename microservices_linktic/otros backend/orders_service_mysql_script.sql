-- Script para crear la base de datos y tablas del microservicio orders_service
-- Ejecutar en phpMyAdmin con MySQL

-- 1. Eliminar la base de datos si existe (para recrearla limpia)
DROP DATABASE IF EXISTS linktic_orders;

-- 2. Crear la base de datos
CREATE DATABASE linktic_orders
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 3. Usar la base de datos
USE linktic_orders;

-- 3. Crear la tabla orders (simplificada)
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_order_number (order_number)
) ENGINE=InnoDB;

-- 4. Crear la tabla order_items (simplificada)
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    quantity BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_sku (sku),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB;

-- 5. Insertar datos de prueba
INSERT INTO orders (order_number) VALUES
('ORD-2024-001'),
('ORD-2024-002'),
('ORD-2024-003'),
('ORD-2024-004'),
('ORD-2024-005');

INSERT INTO order_items (sku, price, quantity, order_id) VALUES
-- Items para ORD-2024-001
('SKU001', 99.99, 2, 1),
('SKU002', 50.00, 2, 1),
('SKU003', 49.99, 2, 1),

-- Items para ORD-2024-002
('SKU001', 99.99, 1, 2),
('SKU004', 50.51, 1, 2),

-- Items para ORD-2024-003
('SKU005', 89.99, 1, 3),

-- Items para ORD-2024-004
('SKU001', 99.99, 3, 4),
('SKU002', 50.00, 3, 4),

-- Items para ORD-2024-005
('SKU003', 25.25, 3, 5);

-- 6. Verificar la creación
SELECT 'Base de datos y tablas creadas exitosamente' AS status;
SELECT COUNT(*) AS total_orders FROM orders;
SELECT COUNT(*) AS total_order_items FROM order_items;

-- 7. Mostrar estructura de las tablas
DESCRIBE orders;
DESCRIBE order_items;
