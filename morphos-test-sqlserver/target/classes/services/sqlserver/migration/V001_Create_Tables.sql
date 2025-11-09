-- ==========================================================
-- USERS
-- ==========================================================
CREATE TABLE users (
    id          UNIQUEIDENTIFIER DEFAULT NEWID() PRIMARY KEY,
    username    NVARCHAR(50) NOT NULL UNIQUE,
    email       NVARCHAR(255) NOT NULL CHECK (email LIKE '%@%.%'),
    created_at  DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    updated_at  DATETIMEOFFSET NULL,
    active      BIT DEFAULT 1,
    CONSTRAINT chk_username_length CHECK (LEN(username) >= 3)
);

CREATE INDEX idx_users_email ON users(email);

-- ==========================================================
-- ROLES
-- ==========================================================
CREATE TABLE roles (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(50) NOT NULL UNIQUE,
    description NVARCHAR(MAX)
);

-- ==========================================================
-- USER_ROLES
-- ==========================================================
CREATE TABLE user_roles (
    user_id     UNIQUEIDENTIFIER NOT NULL,
    role_id     BIGINT NOT NULL,
    assigned_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- ==========================================================
-- CUSTOMERS
-- ==========================================================
CREATE TABLE customers (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    full_name       NVARCHAR(150) NOT NULL,
    document_number NVARCHAR(20) UNIQUE,
    email           NVARCHAR(255) CHECK (email LIKE '%@%.%'),
    registered_at   DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET()
);

-- ==========================================================
-- PRODUCTS
-- ==========================================================
CREATE TABLE products (
    id       BIGINT IDENTITY(1,1) PRIMARY KEY,
    name     NVARCHAR(100) NOT NULL,
    price    DECIMAL(10,2) DEFAULT 0.00,
    stock    INT DEFAULT 0 CHECK (stock >= 0),
    category NVARCHAR(50)
);

CREATE INDEX idx_products_name ON products(name);

-- ==========================================================
-- ORDERS
-- ==========================================================
CREATE TABLE orders (
    id            BIGINT IDENTITY(1,1) PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    created_at    DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    total_amount  DECIMAL(12,2) DEFAULT 0.00,
    status        NVARCHAR(20) DEFAULT 'NEW',
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- ==========================================================
-- ORDER_ITEMS
-- ==========================================================
CREATE TABLE order_items (
    order_id    BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT DEFAULT 1 CHECK (quantity > 0),
    price       DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ==========================================================
-- VIEW v_order_summary
-- ==========================================================
CREATE VIEW v_order_summary AS
SELECT
    o.id AS order_id,
    c.full_name AS customer_name,
    SUM(oi.price * oi.quantity) AS total_value,
    COUNT(oi.product_id) AS total_items
FROM orders o
JOIN customers c ON c.id = o.customer_id
JOIN order_items oi ON oi.order_id = o.id
GROUP BY o.id, c.full_name;

-- ==========================================================
-- CHANGE_LOG
-- ==========================================================
CREATE TABLE change_log (
    id         BIGINT IDENTITY(1,1) PRIMARY KEY,
    table_name NVARCHAR(200) NOT NULL,
    record_id  NVARCHAR(200) NOT NULL,
    operation  NVARCHAR(10) CHECK (operation IN ('INSERT','UPDATE','DELETE')),
    changed_at DATETIMEOFFSET DEFAULT SYSDATETIMEOFFSET(),
    changed_by NVARCHAR(50)
);

-- ==========================================================
-- DATA_TYPES_DEMO
-- ==========================================================
CREATE TABLE data_types_demo (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    small_id        SMALLINT UNIQUE,
    regular_id      INT,
    notes           NCHAR(10),
    code            NVARCHAR(100),
    amount          DECIMAL(12,2),
    price           MONEY,
    ratio           REAL,
    precision_value FLOAT,
    active          BIT DEFAULT 1,
    bitmask         BINARY(1),
    binary_data     VARBINARY(MAX),
    created_at      DATETIME DEFAULT GETDATE(),
    updated_at      DATETIMEOFFSET,
    birth_date      DATE,
    wake_time       TIME,
    wake_time_tz    DATETIMEOFFSET,
    interval_field  NVARCHAR(50),
    user_uuid       UNIQUEIDENTIFIER NOT NULL,
    metadata        NVARCHAR(MAX),
    preferences     NVARCHAR(MAX),
    tags            NVARCHAR(MAX),
    ip_address      NVARCHAR(50),
    network_range   NVARCHAR(50),
    mac_address     NVARCHAR(20),
    xml_data        XML,
    status          NVARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE','PENDING')),
    score           INT CHECK (score BETWEEN 0 AND 100),
    country_code    NCHAR(2) DEFAULT 'US'
);