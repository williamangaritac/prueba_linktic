-- Create products table
CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    status BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on SKU for faster lookups
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);

-- Create index on status for filtering active products
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status);

-- Create index on created_at for sorting
CREATE INDEX IF NOT EXISTS idx_products_created_at ON products(created_at);

-- Insert sample data
INSERT INTO products (sku, name, description, price, status) VALUES
('SKU001', 'Laptop Dell XPS 13', 'High-performance laptop with Intel i7 processor', 1299.99, true),
('SKU002', 'Mouse Logitech MX Master 3', 'Wireless mouse with advanced features', 99.99, true),
('SKU003', 'Keyboard Mechanical RGB', 'Gaming keyboard with RGB lighting', 149.99, true),
('SKU004', 'Monitor LG 27 inch 4K', 'Ultra HD monitor for professional work', 499.99, true),
('SKU005', 'Webcam HD 1080p', 'High definition webcam for streaming', 79.99, false),
('SKU006', 'USB-C Hub 7 in 1', 'Multi-port USB-C hub for connectivity', 49.99, true),
('SKU007', 'Headphones Sony WH-1000XM4', 'Noise-cancelling wireless headphones', 349.99, true),
('SKU008', 'External SSD 1TB', 'Fast external solid state drive', 129.99, true),
('SKU009', 'Laptop Stand Adjustable', 'Ergonomic laptop stand for desk', 39.99, true),
('SKU010', 'USB-C Cable 2m', 'High-speed USB-C charging cable', 19.99, false)
ON CONFLICT (sku) DO NOTHING;

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_products_updated_at ON products;
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

