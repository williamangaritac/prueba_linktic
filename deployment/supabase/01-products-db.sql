-- ============================================
-- SUPABASE - PRODUCTS DATABASE
-- ============================================
-- Este script debe ejecutarse en el SQL Editor de Supabase
-- Proyecto: products-linktic (o el nombre que elijas)

-- Crear tabla de productos
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

-- Crear índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at DESC);

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at
DROP TRIGGER IF EXISTS update_products_updated_at ON products;
CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insertar datos de prueba (15 productos)
INSERT INTO products (sku, name, description, price, status) VALUES
('29444ed7a8f8495587365a6b61458735', 'Solución E-commerce', 'Plataforma completa de comercio electrónico con gestión de inventario, pagos y envíos', 2805.00, true),
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

-- Habilitar Row Level Security (RLS) - opcional pero recomendado
ALTER TABLE products ENABLE ROW LEVEL SECURITY;

-- Política para permitir lectura pública (ajustar según necesidades)
CREATE POLICY "Permitir lectura pública de productos" ON products
    FOR SELECT
    USING (true);

-- Política para operaciones autenticadas (ajustar según necesidades)
CREATE POLICY "Permitir todas las operaciones autenticadas" ON products
    FOR ALL
    USING (auth.role() = 'authenticated');

-- Verificar datos insertados
SELECT COUNT(*) as total_products FROM products;
