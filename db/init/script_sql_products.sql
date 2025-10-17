-- ================================================
-- Script de inicialización: Productos
-- Descripción:
--   Crea la tabla 'product' y agrega registros iniciales
-- ================================================

-- Crear esquema (por claridad, puedes omitirlo si usas 'public')
CREATE SCHEMA IF NOT EXISTS public;

-- ================================================
-- CREACIÓN DE TABLA PRODUCT
-- ================================================
CREATE TABLE IF NOT EXISTS public.product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price FLOAT NOT NULL CHECK (price >= 0),
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Índices para mejorar búsquedas por nombre o precio
CREATE INDEX IF NOT EXISTS idx_product_name ON public.product (name);
CREATE INDEX IF NOT EXISTS idx_product_price ON public.product (price);

-- ================================================
-- INSERT DE DATOS INICIALES
-- ================================================
INSERT INTO public.product (name, price, description) VALUES
    ('Mechanical Keyboard', 129.99, 'RGB backlit mechanical keyboard with blue switches'),
    ('Wireless Mouse', 59.90, 'Ergonomic wireless mouse with 2.4GHz receiver'),
    ('24-inch Monitor', 749.00, 'Full HD 1080p IPS monitor with HDMI input'),
    ('USB-C Dock', 199.99, 'Docking station with HDMI, USB 3.0 and Ethernet ports'),
    ('Laptop Stand', 89.50, 'Adjustable aluminum laptop stand for desks')
ON CONFLICT DO NOTHING;

-- ================================================
-- VERIFICACIÓN
-- ================================================
-- Puedes ejecutar esto manualmente para revisar:
-- SELECT * FROM public.product;