CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    amount BIGINT NOT NULL,
    payment_status VARCHAR(32),
    transaction_id VARCHAR(100),
    create_at TIMESTAMP,
    paid_at TIMESTAMP,
    updated_at TIMESTAMP
);