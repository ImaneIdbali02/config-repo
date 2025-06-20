-- Migration V2: Création des index pour optimiser les performances

-- Index sur order_number pour les recherches rapides
CREATE UNIQUE INDEX idx_orders_order_number ON orders(order_number);

-- Index sur customer_id pour les requêtes par client
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

-- Index sur status pour les requêtes par statut
CREATE INDEX idx_orders_status ON orders(status);

-- Index composé pour les requêtes par client et statut
CREATE INDEX idx_orders_customer_status ON orders(customer_id, status);

-- Index sur created_at pour les requêtes par date
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Index sur updated_at pour les requêtes de synchronisation
CREATE INDEX idx_orders_updated_at ON orders(updated_at);

-- Index sur les timestamps spécifiques
CREATE INDEX idx_orders_confirmed_at ON orders(confirmed_at) WHERE confirmed_at IS NOT NULL;
CREATE INDEX idx_orders_shipped_at ON orders(shipped_at) WHERE shipped_at IS NOT NULL;
CREATE INDEX idx_orders_delivered_at ON orders(delivered_at) WHERE delivered_at IS NOT NULL;

-- Index sur order_lines pour les requêtes par commande
CREATE INDEX idx_order_lines_order_id ON order_lines(order_id);

-- Index sur product_id pour les requêtes par produit
CREATE INDEX idx_order_lines_product_id ON order_lines(product_id);

-- Index sur product_sku pour les recherches par SKU
CREATE INDEX idx_order_lines_product_sku ON order_lines(product_sku);

-- Index sur order_modification_history pour les requêtes par commande
CREATE INDEX idx_order_history_order_id ON order_modification_history(order_id);

-- Index sur modified_at pour l'historique chronologique
CREATE INDEX idx_order_history_modified_at ON order_modification_history(modified_at);

-- Index sur modified_by pour les requêtes par utilisateur
CREATE INDEX idx_order_history_modified_by ON order_modification_history(modified_by);

-- Index composé pour les requêtes d'audit
CREATE INDEX idx_order_history_order_modified ON order_modification_history(order_id, modified_at DESC);

