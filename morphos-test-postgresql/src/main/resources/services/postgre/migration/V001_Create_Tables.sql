-- enable uuid extension (optional, useful for generating uuids)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------
-- DOMAINS (Postgres supports DOMAIN)
-- ----------------------------------------------------------
CREATE DOMAIN email AS VARCHAR(255)
  CHECK (VALUE ~* '^[^@]+@[^@]+\.[^@]+$');

CREATE DOMAIN uuid_str AS CHAR(36)
  CHECK (char_length(VALUE) = 36);

-- ----------------------------------------------------------
-- users
-- ----------------------------------------------------------
CREATE TABLE users (
    id          UUID PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       email NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE,
    active      BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_username_length CHECK (char_length(username) >= 3)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- ----------------------------------------------------------
-- roles
-- ----------------------------------------------------------
CREATE TABLE roles (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- ----------------------------------------------------------
-- user_roles (associative)
-- ----------------------------------------------------------
CREATE TABLE user_roles (
    user_id     UUID NOT NULL,
    role_id     BIGINT NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ----------------------------------------------------------
-- customers
-- ----------------------------------------------------------
CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(150) NOT NULL,
    document_number VARCHAR(20) UNIQUE,
    email           email,
    registered_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- products
-- ----------------------------------------------------------
CREATE TABLE products (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    price    NUMERIC(10,2) DEFAULT 0.00,
    stock    INT DEFAULT 0 CHECK (stock >= 0),
    category VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- ----------------------------------------------------------
-- orders
-- ----------------------------------------------------------
CREATE TABLE orders (
    id            BIGSERIAL PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total_amount  NUMERIC(12,2) DEFAULT 0.00,
    status        VARCHAR(20) DEFAULT 'NEW',
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- ----------------------------------------------------------
-- order_items (associative with composite PK)
-- ----------------------------------------------------------
CREATE TABLE order_items (
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    price       NUMERIC(10,2) NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_positive_qty CHECK (quantity > 0)
);

-- ----------------------------------------------------------
-- view: v_order_summary
-- ----------------------------------------------------------
CREATE OR REPLACE VIEW v_order_summary AS
SELECT
    o.id AS order_id,
    c.full_name AS customer_name,
    SUM(oi.price * oi.quantity) AS total_value,
    COUNT(oi.product_id) AS total_items
FROM orders o
JOIN customers c ON c.id = o.customer_id
JOIN order_items oi ON oi.order_id = o.id
GROUP BY o.id, c.full_name;

-- ----------------------------------------------------------
-- change_log
-- ----------------------------------------------------------
CREATE TABLE change_log (
    id         BIGSERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    record_id  TEXT NOT NULL,
    operation  VARCHAR(10) CHECK (operation IN ('INSERT','UPDATE','DELETE')),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(50)
);

CREATE TYPE status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'PENDING');

CREATE TABLE data_types_demo (
    id              BIGSERIAL PRIMARY KEY,
    small_id        SMALLSERIAL UNIQUE,
    regular_id      SERIAL,
    notes           CHAR(10),
    code            CHARACTER VARYING(100),
    amount          NUMERIC(12, 2),
    price           MONEY,
    ratio           REAL,
    precision_value DOUBLE PRECISION,
    active          BOOLEAN DEFAULT TRUE,
    bitmask         BIT(8),
    binary_data     BYTEA,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ,
    birth_date      DATE,
    wake_time       TIME,
    wake_time_tz    TIMETZ,
    interval_field  INTERVAL,
    user_uuid       UUID NOT NULL,
    metadata        JSON,
    preferences     JSONB,
    tags            TEXT[],
    ip_address      INET,
    network_range   CIDR,
    mac_address     MACADDR,
    xml_data        XML,
    status          status_enum DEFAULT 'ACTIVE',
    score           INTEGER CHECK (score >= 0 AND score <= 100),
    country_code    CHAR(2) DEFAULT 'US'
);