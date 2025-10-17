-- Inventory Service Database Schema
-- This script creates the inventory table for the inventory service

-- Create inventory table
CREATE TABLE IF NOT EXISTS public.inventory (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    quantity INT NOT NULL CHECK (quantity >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create index on product_id for better query performance
CREATE INDEX IF NOT EXISTS idx_inventory_product_id ON public.inventory(product_id);

-- Insert some sample data for testing
INSERT INTO public.inventory (product_id, quantity, updated_at) VALUES
    (1, 100, NOW()),
    (2, 50, NOW()),
    (3, 25, NOW()),
    (4, 0, NOW()),
    (5, 200, NOW())
ON CONFLICT (product_id) DO NOTHING;

