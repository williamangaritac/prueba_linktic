-- Script de inicialización para PostgreSQL
-- Crea las bases de datos necesarias para los microservicios

-- Crear base de datos para Products Service
CREATE DATABASE linktic_products;

-- Crear base de datos para Inventory Service
CREATE DATABASE linktic_inventory;

-- Crear base de datos para Notifications Service
CREATE DATABASE linktic_notifications;

-- Conectar a linktic_products y crear tablas
\c linktic_products;

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de prueba en products
INSERT INTO products (sku, name, description, price, status) VALUES
('29444ed7a8f8495587365a6b61458735', 'Solucion E-commerce', 'Plataforma completa de comercio electrónico con gestión de inventario, pagos y envíos', 2805.00, true),
('a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', 'Sistema de Gestión de Inventario', 'Control total de tu inventario en tiempo real', 1850.00, true),
('b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7', 'CRM Empresarial', 'Gestión de relaciones con clientes', 3200.00, true),
('c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8', 'Sistema de Facturación', 'Facturación electrónica automatizada', 1500.00, true),
('d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9', 'Portal de Empleados', 'Gestión de recursos humanos', 2100.00, true),
('e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', 'Sistema de Reportes', 'Análisis y reportes empresariales', 1750.00, true),
('f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1', 'Plataforma de Marketing', 'Automatización de marketing digital', 2950.00, true),
('g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2', 'Sistema de Logística', 'Gestión de envíos y entregas', 2400.00, true),
('h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3', 'Portal de Clientes', 'Autoservicio para clientes', 1650.00, true),
('i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4', 'Sistema de Soporte', 'Mesa de ayuda y tickets', 1900.00, true),
('j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5', 'Plataforma de Capacitación', 'E-learning corporativo', 2200.00, true),
('k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6', 'Sistema de Calidad', 'Control de calidad y auditorías', 1800.00, true),
('l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7', 'Portal de Proveedores', 'Gestión de proveedores', 1550.00, true),
('m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8', 'Sistema de Proyectos', 'Gestión de proyectos ágiles', 2650.00, true),
('n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9', 'Plataforma de BI', 'Business Intelligence avanzado', 3500.00, true)
ON CONFLICT (sku) DO NOTHING;

-- Conectar a linktic_inventory y crear tablas
\c linktic_inventory;

CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar datos de prueba en inventory
INSERT INTO inventory (sku, quantity) VALUES
('29444ed7a8f8495587365a6b61458735', 100),
('a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', 150),
('b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7', 80),
('c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8', 120),
('d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9', 90),
('e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0', 110),
('f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1', 70),
('g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2', 95),
('h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3', 130),
('i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4', 85),
('j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5', 105),
('k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6', 75),
('l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7', 140),
('m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8', 60),
('n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9', 50)
ON CONFLICT (sku) DO NOTHING;

-- Conectar a linktic_notifications y crear tablas
\c linktic_notifications;

CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

