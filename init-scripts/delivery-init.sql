CREATE TABLE deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL UNIQUE,
    payment_id UUID NOT NULL,
    customer_email VARCHAR(64) NOT NULL,
    status VARCHAR(32),
    tracking_number VARCHAR(100),
    estimated_delivery_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);