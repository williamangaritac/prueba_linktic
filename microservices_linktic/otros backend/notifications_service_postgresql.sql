-- =====================================================
-- SCRIPT DE BASE DE DATOS PARA NOTIFICATIONS SERVICE
-- Base de Datos: PostgreSQL
-- =====================================================

-- 1. CREAR LA BASE DE DATOS
DROP DATABASE IF EXISTS linktic_notifications;
CREATE DATABASE linktic_notifications;

-- Conectar a la base de datos
\c linktic_notifications;

-- 2. CREAR LA TABLA DE NOTIFICACIONES
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL,
    order_id BIGINT,
    recipient_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    type VARCHAR(50) NOT NULL DEFAULT 'ORDER_CREATED',
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    error_message VARCHAR(1000),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'SENT', 'FAILED')),
    CONSTRAINT chk_type CHECK (type IN ('ORDER_CREATED', 'ORDER_UPDATED', 'ORDER_CANCELLED', 'GENERAL'))
);

-- 3. CREAR ÍNDICES PARA MEJORAR RENDIMIENTO
CREATE INDEX idx_notifications_order_number ON notifications(order_number);
CREATE INDEX idx_notifications_order_id ON notifications(order_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);

-- 4. INSERTAR DATOS DE PRUEBA (basados en las órdenes de orders_service)

-- Notificación para Orden 1: ORD-2024-001
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-001',
    1,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-001',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-001\n- Total: $5,000.00\n\nProductos:\n1. Solucion E-commerce (SKU: 29444ed7a8f8495587365a6b61458735) - Cantidad: 1 - Precio: $2,805.00\n2. Core de Seguros (SKU: 8b5c9d2e1f4a6b3c7d8e9f0a1b2c3d4e) - Cantidad: 1 - Precio: $2,195.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    CURRENT_TIMESTAMP - INTERVAL '10 days'
);

-- Notificación para Orden 2: ORD-2024-002
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-002',
    2,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-002',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-002\n- Total: $3,500.00\n\nProductos:\n1. HIS/Telesalud (SKU: 5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c) - Cantidad: 1 - Precio: $3,500.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '9 days',
    CURRENT_TIMESTAMP - INTERVAL '9 days'
);

-- Notificación para Orden 3: ORD-2024-003
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-003',
    3,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-003',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-003\n- Total: $7,200.00\n\nProductos:\n1. ERP (SKU: 1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d) - Cantidad: 2 - Precio: $1,800.00 c/u\n2. CRM (SKU: 7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b) - Cantidad: 2 - Precio: $1,800.00 c/u\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '8 days',
    CURRENT_TIMESTAMP - INTERVAL '8 days'
);

-- Notificación para Orden 4: ORD-2024-004
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-004',
    4,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-004',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-004\n- Total: $4,500.00\n\nProductos:\n1. Plataforma de Integracion (SKU: 3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f) - Cantidad: 1 - Precio: $2,250.00\n2. Sistema de Gestion Documental (SKU: 9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a) - Cantidad: 1 - Precio: $2,250.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '7 days',
    CURRENT_TIMESTAMP - INTERVAL '7 days'
);

-- Notificación para Orden 5: ORD-2024-005
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-005',
    5,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-005',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-005\n- Total: $6,300.00\n\nProductos:\n1. Portal de Autoservicio (SKU: 5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b) - Cantidad: 3 - Precio: $2,100.00 c/u\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '6 days',
    CURRENT_TIMESTAMP - INTERVAL '6 days'
);

-- Notificación para Orden 6: ORD-2024-006
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-006',
    6,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-006',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-006\n- Total: $3,000.00\n\nProductos:\n1. Sistema de Reportes y Analytics (SKU: 1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c) - Cantidad: 2 - Precio: $1,500.00 c/u\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    CURRENT_TIMESTAMP - INTERVAL '5 days'
);

-- Notificación para Orden 7: ORD-2024-007
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-007',
    7,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-007',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-007\n- Total: $8,500.00\n\nProductos:\n1. Solucion E-commerce (SKU: 29444ed7a8f8495587365a6b61458735) - Cantidad: 1 - Precio: $2,805.00\n2. Core de Seguros (SKU: 8b5c9d2e1f4a6b3c7d8e9f0a1b2c3d4e) - Cantidad: 1 - Precio: $2,195.00\n3. HIS/Telesalud (SKU: 5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c) - Cantidad: 1 - Precio: $3,500.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '4 days',
    CURRENT_TIMESTAMP - INTERVAL '4 days'
);

-- Notificación para Orden 8: ORD-2024-008
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-008',
    8,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-008',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-008\n- Total: $5,400.00\n\nProductos:\n1. ERP (SKU: 1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d) - Cantidad: 3 - Precio: $1,800.00 c/u\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '3 days'
);

-- Notificación para Orden 9: ORD-2024-009
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-009',
    9,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-009',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-009\n- Total: $4,000.00\n\nProductos:\n1. CRM (SKU: 7e8f9a0b1c2d3e4f5a6b7c8d9e0f1a2b) - Cantidad: 1 - Precio: $1,800.00\n2. Sistema de Gestion Documental (SKU: 9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a) - Cantidad: 1 - Precio: $2,200.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    CURRENT_TIMESTAMP - INTERVAL '2 days'
);

-- Notificación para Orden 10: ORD-2024-010
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, sent_at, created_at)
VALUES (
    'ORD-2024-010',
    10,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-010',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-010\n- Total: $10,500.00\n\nProductos:\n1. Solucion E-commerce (SKU: 29444ed7a8f8495587365a6b61458735) - Cantidad: 2 - Precio: $2,805.00 c/u\n2. Portal de Autoservicio (SKU: 5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b) - Cantidad: 2 - Precio: $2,100.00 c/u\n3. Sistema de Reportes y Analytics (SKU: 1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c) - Cantidad: 1 - Precio: $1,485.00\n\n¡Gracias por confiar en LINKTIC!',
    'SENT',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Agregar algunas notificaciones PENDIENTES (no enviadas aún)
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, created_at)
VALUES (
    'ORD-2024-011',
    11,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-011',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-011\n- Total: $2,805.00\n\nProductos:\n1. Solucion E-commerce (SKU: 29444ed7a8f8495587365a6b61458735) - Cantidad: 1 - Precio: $2,805.00\n\n¡Gracias por confiar en LINKTIC!',
    'PENDING',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP
);

-- Agregar una notificación FALLIDA (con error)
INSERT INTO notifications (order_number, order_id, recipient_email, subject, message, status, type, created_at, error_message)
VALUES (
    'ORD-2024-012',
    12,
    'contacto@linktic.com',
    'Confirmación de Orden ORD-2024-012',
    E'¡Gracias por tu compra!\n\nDetalles de tu orden:\n- Número de Orden: ORD-2024-012\n- Total: $3,500.00\n\nProductos:\n1. HIS/Telesalud (SKU: 5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c) - Cantidad: 1 - Precio: $3,500.00\n\n¡Gracias por confiar en LINKTIC!',
    'FAILED',
    'ORDER_CREATED',
    CURRENT_TIMESTAMP,
    'SMTP server connection timeout'
);

-- 5. VERIFICAR LOS DATOS INSERTADOS
SELECT 
    id,
    order_number,
    recipient_email,
    status,
    type,
    created_at
FROM notifications
ORDER BY created_at DESC;

-- 6. ESTADÍSTICAS
SELECT 
    status,
    COUNT(*) as total
FROM notifications
GROUP BY status;

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================

