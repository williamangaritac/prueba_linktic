-- ============================================
-- SUPABASE - NOTIFICATIONS DATABASE
-- ============================================
-- Este script debe ejecutarse en el SQL Editor de Supabase
-- Proyecto: notifications-linktic (o el nombre que elijas)

-- Crear tipo enum para el estado de notificaciones
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'FAILED');

-- Crear tabla de notificaciones
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    message TEXT NOT NULL,
    status notification_status DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0
);

-- Crear índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_notifications_order_number ON notifications(order_number);
CREATE INDEX IF NOT EXISTS idx_notifications_customer_email ON notifications(customer_email);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at DESC);

-- Habilitar Row Level Security (RLS)
ALTER TABLE notifications ENABLE ROW LEVEL SECURITY;

-- Política para permitir lectura pública (ajustar según necesidades)
CREATE POLICY "Permitir lectura pública de notificaciones" ON notifications
    FOR SELECT
    USING (true);

-- Política para operaciones autenticadas
CREATE POLICY "Permitir todas las operaciones autenticadas" ON notifications
    FOR ALL
    USING (auth.role() = 'authenticated');

-- Función para crear nueva notificación
CREATE OR REPLACE FUNCTION create_notification(
    p_order_number VARCHAR(255),
    p_customer_email VARCHAR(255),
    p_subject VARCHAR(500),
    p_message TEXT
)
RETURNS BIGINT AS $$
DECLARE
    v_notification_id BIGINT;
BEGIN
    INSERT INTO notifications (order_number, customer_email, subject, message, status)
    VALUES (p_order_number, p_customer_email, p_subject, p_message, 'PENDING')
    RETURNING id INTO v_notification_id;

    RETURN v_notification_id;
END;
$$ LANGUAGE plpgsql;

-- Función para marcar notificación como enviada
CREATE OR REPLACE FUNCTION mark_notification_sent(
    p_notification_id BIGINT
)
RETURNS BOOLEAN AS $$
BEGIN
    UPDATE notifications
    SET status = 'SENT',
        sent_at = CURRENT_TIMESTAMP
    WHERE id = p_notification_id;

    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Función para marcar notificación como fallida
CREATE OR REPLACE FUNCTION mark_notification_failed(
    p_notification_id BIGINT,
    p_error_message TEXT
)
RETURNS BOOLEAN AS $$
BEGIN
    UPDATE notifications
    SET status = 'FAILED',
        error_message = p_error_message,
        retry_count = retry_count + 1
    WHERE id = p_notification_id;

    RETURN FOUND;
END;
$$ LANGUAGE plpgsql;

-- Vista para estadísticas de notificaciones
CREATE OR REPLACE VIEW notification_stats AS
SELECT
    status,
    COUNT(*) as count,
    COUNT(*) * 100.0 / SUM(COUNT(*)) OVER () as percentage
FROM notifications
GROUP BY status;

-- Insertar algunas notificaciones de prueba (opcional)
INSERT INTO notifications (order_number, customer_email, subject, message, status) VALUES
('ORDER-TEST-001', 'test@example.com', 'Confirmación de Pedido', 'Tu pedido ha sido recibido', 'SENT'),
('ORDER-TEST-002', 'demo@example.com', 'Pedido en Proceso', 'Tu pedido está siendo procesado', 'PENDING')
ON CONFLICT DO NOTHING;

-- Verificar tabla creada
SELECT COUNT(*) as total_notifications FROM notifications;
