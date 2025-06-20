-- Migration V1: Création des tables principales pour les commandes
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    
    -- Adresse de livraison
    delivery_recipient_name VARCHAR(255) NOT NULL,
    delivery_street_address VARCHAR(500) NOT NULL,
    delivery_city VARCHAR(100) NOT NULL,
    delivery_postal_code VARCHAR(20) NOT NULL,
    delivery_country VARCHAR(100) NOT NULL,
    delivery_additional_instructions TEXT,
    
    -- Adresse de facturation
    billing_name VARCHAR(255) NOT NULL,
    billing_street_address VARCHAR(500) NOT NULL,
    billing_city VARCHAR(100) NOT NULL,
    billing_postal_code VARCHAR(20) NOT NULL,
    billing_country VARCHAR(100) NOT NULL,
    billing_company_name VARCHAR(255),
    billing_vat_number VARCHAR(50),
    
    -- Résumé de paiement
    total_amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    shipping_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    net_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'EUR',
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    
    -- Notes
    customer_notes TEXT,
    internal_notes TEXT,
    
    -- Contraintes
    CONSTRAINT chk_amounts_positive CHECK (
        total_amount > 0 AND 
        tax_amount >= 0 AND 
        shipping_amount >= 0 AND 
        discount_amount >= 0 AND 
        net_amount > 0
    ),
    CONSTRAINT chk_status_valid CHECK (
        status IN ('PENDING', 'CONFIRMED', 'PAID', 'PREPARING', 
                  'READY_FOR_SHIPMENT', 'SHIPPED', 'DELIVERED', 
                  'CANCELLED', 'REFUNDED')
    )
);

-- Table des lignes de commande
CREATE TABLE order_lines (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    product_image_url VARCHAR(500),
    product_description TEXT,
    
    -- Contraintes
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0),
    CONSTRAINT chk_prices_positive CHECK (unit_price > 0 AND total_price > 0),
    CONSTRAINT chk_total_price_calculation CHECK (total_price = unit_price * quantity)
);

-- Table de l'historique des modifications
CREATE TABLE order_modification_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    previous_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    reason VARCHAR(500) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    additional_notes TEXT,
    
    -- Contraintes
    CONSTRAINT chk_previous_status_valid CHECK (
        previous_status IN ('PENDING', 'CONFIRMED', 'PAID', 'PREPARING', 
                           'READY_FOR_SHIPMENT', 'SHIPPED', 'DELIVERED', 
                           'CANCELLED', 'REFUNDED')
    ),
    CONSTRAINT chk_new_status_valid CHECK (
        new_status IN ('PENDING', 'CONFIRMED', 'PAID', 'PREPARING', 
                      'READY_FOR_SHIPMENT', 'SHIPPED', 'DELIVERED', 
                      'CANCELLED', 'REFUNDED')
    )
);

-- Fonction pour mettre à jour automatiquement updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger pour mettre à jour automatiquement updated_at dans la table orders
CREATE TRIGGER update_orders_updated_at 
    BEFORE UPDATE ON orders 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

