CREATE TABLE IF NOT EXISTS products
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(200),
    price NUMERIC
);