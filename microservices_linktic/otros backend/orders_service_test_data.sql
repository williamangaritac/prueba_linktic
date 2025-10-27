-- Script para insertar datos de prueba en el microservicio orders_service
-- Basado en los productos del microservicio products_service
-- Ejecutar en phpMyAdmin con MySQL

USE linktic_orders;

-- Limpiar datos existentes (opcional)
DELETE FROM order_items;
DELETE FROM orders;

-- Resetear auto_increment
ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE order_items AUTO_INCREMENT = 1;

-- Insertar órdenes de prueba
INSERT INTO orders (order_number) VALUES
('ORD-2024-001'),
('ORD-2024-002'),
('ORD-2024-003'),
('ORD-2024-004'),
('ORD-2024-005'),
('ORD-2024-006'),
('ORD-2024-007'),
('ORD-2024-008'),
('ORD-2024-009'),
('ORD-2024-010');

-- Insertar items de órdenes basados en productos REALES del products_service e inventory_service
-- SINCRONIZADO CON FORMATO UUID Y PRECIOS REALES
-- Productos disponibles (PostgreSQL - linktic_products y linktic_inventory):
-- 29444ed7a8f8495587365a6b61458735: Solucion E-commerce - $2805.00 (Inventory: 150)
-- 721ee031b6dd421ca59cd23d712e8438: Core de Seguros - $19502.00 (Inventory: 50)
-- 15270af906f54eaca4282559e80f8c06: HIS/Telesalud - $14360.50 (Inventory: 80)
-- ddca28786303459bb843c30c22d5fc31: ERP - $4994.00 (Inventory: 30)
-- 09eebaa32c89416bab0812fef5c0f4a5: CRM - $12031.00 (Inventory: 210)
-- d55281848baa45798c2f1252f28fc60f: Gestor Documental - $10856.00 (Inventory: 90)
-- 2029b6f777e54c9db3a6d104d6edfc6b: Fabrica de Software - $10932.50 (Inventory: 70)
-- d2cc00ae1c1340c9bf0c28a394faad7a: Servicios TIC - $9346.00 (Inventory: 300)
-- 2a9d998f6bba4255977db00efcb6ba56: PROLINKTIC-SGDEA - $12323.00 (Inventory: 120)
-- 94eb9162015e4b72ac6d65787593e803: Gestion de Proyectos - $9801.00 (Inventory: 50)
-- 1741cdceb0294096ab9623ae1dde3cbf: Gestion Judicial - $1241.00 (Inventory: 8)
-- 9cb2460d43c24e79ba933563d57184e1: Mesa de Ayuda - $6720.50 (Inventory: 22)
-- dc9f356c6db74fe39260985e0dadec18: Informacion Turistica - $13886.50 (Inventory: 180)
-- 59005a7de9e34745ac2629c5f47bcfde: Plataforma de Certificacion - $6912.00 (Inventory: 40)
-- 37b12eaf9013415a99f77f27a84a1c04: Fabrica de Software SED - $4027.50 (Inventory: 650)

-- Orden 1: Cliente compra Solucion E-commerce y Core de Seguros
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('29444ed7a8f8495587365a6b61458735', 2805.00, 1, 1),    -- Solucion E-commerce
('721ee031b6dd421ca59cd23d712e8438', 19502.00, 1, 1);   -- Core de Seguros

-- Orden 2: Cliente compra HIS/Telesalud y ERP
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('15270af906f54eaca4282559e80f8c06', 14360.50, 1, 2),   -- HIS/Telesalud
('ddca28786303459bb843c30c22d5fc31', 4994.00, 1, 2);    -- ERP

-- Orden 3: Cliente compra CRM
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('09eebaa32c89416bab0812fef5c0f4a5', 12031.00, 2, 3);   -- CRM

-- Orden 4: Cliente compra Gestor Documental y PROLINKTIC-SGDEA
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('d55281848baa45798c2f1252f28fc60f', 10856.00, 1, 4),   -- Gestor Documental
('2a9d998f6bba4255977db00efcb6ba56', 12323.00, 1, 4);   -- PROLINKTIC-SGDEA

-- Orden 5: Cliente compra Fabrica de Software
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('2029b6f777e54c9db3a6d104d6edfc6b', 10932.50, 2, 5);   -- Fabrica de Software

-- Orden 6: Cliente compra Servicios TIC y Gestion de Proyectos
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('d2cc00ae1c1340c9bf0c28a394faad7a', 9346.00, 1, 6),    -- Servicios TIC
('94eb9162015e4b72ac6d65787593e803', 9801.00, 1, 6);    -- Gestion de Proyectos

-- Orden 7: Orden grande - Paquete empresarial completo
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('29444ed7a8f8495587365a6b61458735', 2805.00, 2, 7),    -- 2 Solucion E-commerce
('15270af906f54eaca4282559e80f8c06', 14360.50, 1, 7),   -- HIS/Telesalud
('ddca28786303459bb843c30c22d5fc31', 4994.00, 1, 7),    -- ERP
('09eebaa32c89416bab0812fef5c0f4a5', 12031.00, 1, 7);   -- CRM

-- Orden 8: Cliente compra servicios de soporte
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('9cb2460d43c24e79ba933563d57184e1', 6720.50, 3, 8),    -- 3 Mesa de Ayuda
('1741cdceb0294096ab9623ae1dde3cbf', 1241.00, 2, 8);    -- 2 Gestion Judicial

-- Orden 9: Cliente compra soluciones especializadas
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('dc9f356c6db74fe39260985e0dadec18', 13886.50, 1, 9),   -- Informacion Turistica
('59005a7de9e34745ac2629c5f47bcfde', 6912.00, 1, 9),    -- Plataforma de Certificacion
('37b12eaf9013415a99f77f27a84a1c04', 4027.50, 2, 9);    -- 2 Fabrica de Software SED

-- Orden 10: Cliente compra paquete completo de transformacion digital
INSERT INTO order_items (sku, price, quantity, order_id) VALUES
('29444ed7a8f8495587365a6b61458735', 2805.00, 1, 10),   -- Solucion E-commerce
('721ee031b6dd421ca59cd23d712e8438', 19502.00, 1, 10),  -- Core de Seguros
('15270af906f54eaca4282559e80f8c06', 14360.50, 1, 10),  -- HIS/Telesalud
('ddca28786303459bb843c30c22d5fc31', 4994.00, 1, 10),   -- ERP
('09eebaa32c89416bab0812fef5c0f4a5', 12031.00, 1, 10),  -- CRM
('d55281848baa45798c2f1252f28fc60f', 10856.00, 1, 10),  -- Gestor Documental
('2029b6f777e54c9db3a6d104d6edfc6b', 10932.50, 1, 10),  -- Fabrica de Software
('d2cc00ae1c1340c9bf0c28a394faad7a', 9346.00, 1, 10);   -- Servicios TIC

-- Verificar la inserción
SELECT 'Datos de prueba insertados exitosamente' AS status;
SELECT COUNT(*) AS total_orders FROM orders;
SELECT COUNT(*) AS total_order_items FROM order_items;

-- Mostrar resumen de órdenes con totales
SELECT 
    o.id,
    o.order_number,
    COUNT(oi.id) AS items_count,
    SUM(oi.price * oi.quantity) AS total_amount
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
GROUP BY o.id, o.order_number
ORDER BY o.id;

-- Mostrar todos los items de todas las órdenes
SELECT 
    o.order_number,
    oi.sku,
    oi.price,
    oi.quantity,
    (oi.price * oi.quantity) AS subtotal
FROM orders o
INNER JOIN order_items oi ON o.id = oi.order_id
ORDER BY o.id, oi.id;
