-- Script para corregir la tabla order_items en MySQL
-- Ejecutar en phpMyAdmin

USE linktic_orders;

-- Verificar estructura actual
DESCRIBE order_items;

-- Eliminar la tabla y recrearla con la estructura correcta
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

-- Recrear tabla orders
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(100) NOT NULL UNIQUE,
    INDEX idx_order_number (order_number)
) ENGINE=InnoDB;

-- Recrear tabla order_items con DOUBLE para price
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    quantity BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_sku (sku),
    INDEX idx_order_id (order_id)
) ENGINE=InnoDB;

-- Verificar que la estructura es correcta
DESCRIBE orders;
DESCRIBE order_items;

-- Ahora ejecuta el script orders_service_test_data.sql para insertar los datos
