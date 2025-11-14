-- ============================================
-- SUPABASE - INVENTORY DATABASE
-- ============================================
-- Este script debe ejecutarse en el SQL Editor de Supabase
-- Proyecto: inventory-linktic (o el nombre que elijas)

-- Crear tabla de inventario
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    quantity BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT quantity_non_negative CHECK (quantity >= 0)
);

-- Crear índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_inventory_sku ON inventory(sku);
CREATE INDEX IF NOT EXISTS idx_inventory_quantity ON inventory(quantity);

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger para actualizar updated_at
DROP TRIGGER IF EXISTS update_inventory_updated_at ON inventory;
CREATE TRIGGER update_inventory_updated_at
    BEFORE UPDATE ON inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Insertar datos de prueba (15 productos con stock)
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

-- Habilitar Row Level Security (RLS)
ALTER TABLE inventory ENABLE ROW LEVEL SECURITY;

-- Política para permitir lectura pública
CREATE POLICY "Permitir lectura pública de inventario" ON inventory
    FOR SELECT
    USING (true);

-- Política para operaciones autenticadas
CREATE POLICY "Permitir todas las operaciones autenticadas" ON inventory
    FOR ALL
    USING (auth.role() = 'authenticated');

-- Función para actualizar stock (evitar stock negativo)
CREATE OR REPLACE FUNCTION update_stock(
    p_sku VARCHAR(100),
    p_quantity_change BIGINT
)
RETURNS TABLE (
    success BOOLEAN,
    message TEXT,
    new_quantity BIGINT
) AS $$
DECLARE
    v_current_quantity BIGINT;
    v_new_quantity BIGINT;
BEGIN
    -- Obtener cantidad actual
    SELECT quantity INTO v_current_quantity
    FROM inventory
    WHERE sku = p_sku;

    IF NOT FOUND THEN
        RETURN QUERY SELECT false, 'SKU no encontrado', 0::BIGINT;
        RETURN;
    END IF;

    -- Calcular nueva cantidad
    v_new_quantity := v_current_quantity + p_quantity_change;

    -- Validar que no sea negativa
    IF v_new_quantity < 0 THEN
        RETURN QUERY SELECT false, 'Stock insuficiente', v_current_quantity;
        RETURN;
    END IF;

    -- Actualizar stock
    UPDATE inventory
    SET quantity = v_new_quantity
    WHERE sku = p_sku;

    RETURN QUERY SELECT true, 'Stock actualizado exitosamente', v_new_quantity;
END;
$$ LANGUAGE plpgsql;

-- Verificar datos insertados
SELECT COUNT(*) as total_items, SUM(quantity) as total_stock FROM inventory;
