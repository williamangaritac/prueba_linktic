-- Script para verificar productos en products_service
-- Los productos YA ESTÁN CORRECTOS con SKUs UUID
-- Ejecutar en PostgreSQL (linktic_products)

-- Conectar a la base de datos
\c linktic_products

-- Verificar productos existentes
SELECT 'Productos actuales en la base de datos' AS status;
SELECT COUNT(*) AS total_products FROM products;

-- Mostrar todos los productos con sus SKUs UUID
SELECT id, sku, name, price, status FROM products ORDER BY id;

-- Verificar que los SKUs coincidan con inventory_service
SELECT 'SKUs que deben existir en inventory_service:' AS info;
SELECT sku FROM products ORDER BY id;
