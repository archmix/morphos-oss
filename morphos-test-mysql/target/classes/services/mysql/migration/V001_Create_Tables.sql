-- ----------------------------------------------------------
-- users
-- ----------------------------------------------------------
CREATE TABLE users (
    id          CHAR(36) NOT NULL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NULL,
    active      BOOLEAN DEFAULT TRUE,
    CONSTRAINT chk_username_length CHECK (CHAR_LENGTH(username) >= 3)
);

CREATE INDEX idx_users_email ON users(email);

-- ----------------------------------------------------------
-- roles
-- ----------------------------------------------------------
CREATE TABLE roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- ----------------------------------------------------------
-- user_roles
-- ----------------------------------------------------------
CREATE TABLE user_roles (
    user_id     CHAR(36) NOT NULL,
    role_id     BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ----------------------------------------------------------
-- customers
-- ----------------------------------------------------------
CREATE TABLE customers (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(150) NOT NULL,
    document_number VARCHAR(20) UNIQUE,
    email           VARCHAR(255),
    registered_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- products
-- ----------------------------------------------------------
CREATE TABLE products (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL,
    price    DECIMAL(10,2) DEFAULT 0.00,
    stock    INT DEFAULT 0 CHECK (stock >= 0),
    category VARCHAR(50)
);

CREATE INDEX idx_products_name ON products(name);

-- ----------------------------------------------------------
-- orders
-- ----------------------------------------------------------
CREATE TABLE orders (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(12,2) DEFAULT 0.00,
    status        VARCHAR(20) DEFAULT 'NEW',
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- ----------------------------------------------------------
-- order_items
-- ----------------------------------------------------------
CREATE TABLE order_items (
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    price       DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_positive_qty CHECK (quantity > 0)
);

-- ----------------------------------------------------------
-- change_log
-- ----------------------------------------------------------

CREATE TABLE change_log (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    record_id  VARCHAR(255) NOT NULL,
    operation  ENUM('INSERT','UPDATE','DELETE'),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(50)
);

-- ----------------------------------------------------------
-- data_types_demo
-- ----------------------------------------------------------

CREATE TABLE data_types_demo (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    small_id        SMALLINT UNIQUE NOT NULL,
    regular_id      INT,
    notes           CHAR(10),
    code            VARCHAR(100),
    amount          DECIMAL(12, 2),
    price           DECIMAL(12, 2),
    ratio           FLOAT,
    precision_value DOUBLE,
    active          BOOLEAN DEFAULT TRUE,
    bitmask         BIT(8),
    binary_data     BLOB,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NULL,
    birth_date      DATE,
    wake_time       TIME,
    interval_field  VARCHAR(50),
    user_uuid       CHAR(36) NOT NULL,
    metadata        JSON,
    tags            JSON,
    ip_address      VARCHAR(45),
    xml_data        TEXT,
    wake_time_tz    TIME,
    preferences     JSON,
    network_range   VARCHAR(45),
    mac_address     VARCHAR(6),
    status          ENUM('ACTIVE', 'INACTIVE', 'PENDING') DEFAULT 'ACTIVE',
    score           INT CHECK (score >= 0 AND score <= 100),
    country_code    CHAR(2) DEFAULT 'US'
);