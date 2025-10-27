-- Script para crear la base de datos y tabla del microservicio products_service
-- Ejecutar como superusuario de PostgreSQL (postgres)

-- 1. Crear la base de datos
CREATE DATABASE products_service
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- 2. Conectarse a la base de datos products_service y crear la tabla
\c products_service;

-- 3. Crear la tabla Product
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    status BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_product_sku ON product(sku);
CREATE INDEX IF NOT EXISTS idx_product_name ON product(name);
CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);

-- 5. Crear función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 6. Crear trigger para actualizar updated_at
CREATE TRIGGER update_product_updated_at 
    BEFORE UPDATE ON product 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- 7. Insertar datos de prueba
INSERT INTO product (sku, name, description, price, status) VALUES
('SKU001', 'Laptop Dell Inspiron', 'Laptop Dell Inspiron 15 3000 con procesador Intel Core i5', 1299.99, true),
('SKU002', 'Mouse Logitech MX Master', 'Mouse inalámbrico Logitech MX Master 3 para productividad', 99.99, true),
('SKU003', 'Teclado Mecánico RGB', 'Teclado mecánico gaming con retroiluminación RGB', 149.99, true),
('SKU004', 'Monitor Samsung 24"', 'Monitor Samsung de 24 pulgadas Full HD', 199.99, true),
('SKU005', 'Auriculares Sony WH-1000XM4', 'Auriculares inalámbricos con cancelación de ruido', 349.99, false);

-- 8. Los permisos ya están configurados para el usuario postgres (owner de la base de datos)

-- Verificar la creación
SELECT 'Database and table created successfully!' as status;
