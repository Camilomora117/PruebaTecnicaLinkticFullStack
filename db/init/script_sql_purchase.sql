-- Script para crear la tabla 'purchase'
CREATE TABLE IF NOT EXISTS purchase (
    id SERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES product(id)
);

-- Inserts de ejemplo
INSERT INTO purchase (product_id, quantity, created_at) VALUES
    (1, 100, '2025-10-01 10:00:00'),
    (1, 50,  '2025-10-05 14:30:00'),
    (3, 200, '2025-10-10 09:15:00')
ON CONFLICT DO NOTHING;

