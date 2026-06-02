CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(64) NOT NULL,
    price INT NOT NULL
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_email VARCHAR(64) NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(32),
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO products (product_name, price) VALUES ('IntelliJ IDEA', 1200);
INSERT INTO products (product_name, price) VALUES ('Net Beans', 500);
INSERT INTO products (product_name, price) VALUES ('Eclips', 1000);