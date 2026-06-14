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

CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    balance DECIMAL NOT NULL
);

CREATE INDEX idx_user_id ON wallets(user_id);