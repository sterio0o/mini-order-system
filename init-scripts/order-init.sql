CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_email VARCHAR(64) NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(32),
    created_at TIMESTAMP NOT NULL
);