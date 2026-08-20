CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(128) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(128),
    created_at TIMESTAMP NOT NULL
);