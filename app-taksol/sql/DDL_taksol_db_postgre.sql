-- =====================================================
-- Reset schema di PostgreSQL
-- =====================================================
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;


--auto update trigger (TRIGGER PENGGANTI ON UPDATE MySQL)
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

--enum types 
DO $$
BEGIN
    CREATE TYPE kendaraan_status AS ENUM ('ACTIVE','INACTIVE','MAINTENANCE');
    CREATE TYPE order_status AS ENUM ('PENDING','ACCEPTED','ON_PROGRESS','COMPLETED','CANCELLED');
    CREATE TYPE pembayaran_status AS ENUM ('UNPAID','PAID','REFUNDED');
    CREATE TYPE role_in_order AS ENUM ('CUSTOMER','DRIVER','ADMIN');
    CREATE TYPE metode_pembayaran AS ENUM ('CASH','CARD','WALLET');
    CREATE TYPE payment_status AS ENUM ('PENDING','SUCCESS','FAILED','REFUNDED');
    CREATE TYPE wallet_trans_type AS ENUM ('TOPUP','PAYMENT','REFUND');
    CREATE TYPE verification_status AS ENUM ('PENDING','APPROVED','REJECTED');
EXCEPTION WHEN duplicate_object THEN NULL;
END $$;




-- =====================================================
-- app_users
-- =====================================================
CREATE TABLE app_users (
    id SERIAL PRIMARY KEY,
    nama VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    no_hp VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    last_login TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--trigger 
CREATE TRIGGER trg_app_users_updated
BEFORE UPDATE ON app_users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_users_email ON app_users(email);
CREATE INDEX idx_users_deleted_at ON app_users(deleted_at);







-- =====================================================
-- Roles & user_roles
-- =====================================================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    parent_id INT REFERENCES roles(id)
);


CREATE TABLE user_roles (
    user_id INT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(id),
    PRIMARY KEY(user_id, role_id)
);


-- =====================================================
-- jenis_kendaraan & kendaraan
-- =====================================================
CREATE TABLE jenis_kendaraan (
    id SERIAL PRIMARY KEY,
    nama_jenis VARCHAR(200) NOT NULL,
    kapasitas INT NOT NULL CHECK (kapasitas >= 0),
    tarif_per_km NUMERIC(10,2) NOT NULL CHECK (tarif_per_km >= 0),
    deskripsi TEXT
);



CREATE TABLE kendaraan (
    id SERIAL PRIMARY KEY,
    plat_nomor VARCHAR(15) NOT NULL UNIQUE,
    jenis_kendaraan_id INT NOT NULL REFERENCES jenis_kendaraan(id),
    driver_id INT NOT NULL UNIQUE REFERENCES app_users(id),
    status kendaraan_status DEFAULT 'ACTIVE'
);

CREATE INDEX idx_kendaraan_driver ON kendaraan(driver_id);



-- =====================================================
-- Orders & order_details
-- =====================================================

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    lokasi_jemput VARCHAR(200) NOT NULL,
    lokasi_tujuan VARCHAR(200) NOT NULL,
    assigned_driver_id INT REFERENCES app_users(id),
    status order_status NOT NULL DEFAULT 'PENDING',
    harga_total NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (harga_total >= 0),
    status_pembayaran pembayaran_status NOT NULL DEFAULT 'UNPAID',
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_orders_updated
BEFORE UPDATE ON orders
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_driver ON orders(assigned_driver_id);
CREATE INDEX idx_orders_deleted ON orders(deleted_at);





CREATE TABLE order_details (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id) ON DELETE CASCADE,
    jenis_kendaraan_id INT NOT NULL REFERENCES jenis_kendaraan(id),
    kendaraan_id INT REFERENCES kendaraan(id),
    jarak_km NUMERIC(10,2) NOT NULL CHECK (jarak_km >= 0),
    tarif_per_km NUMERIC(10,2) NOT NULL CHECK (tarif_per_km >= 0),
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    deleted_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_order_details_updated
BEFORE UPDATE ON order_details
FOR EACH ROW EXECUTE FUNCTION set_updated_at();



CREATE TABLE user_orders (
    user_id INT NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    role role_in_order NOT NULL,
    PRIMARY KEY(user_id, order_id)
);


-- =====================================================
-- Payments
-- =====================================================


CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    jumlah NUMERIC(10,2) NOT NULL CHECK (jumlah >= 0),
    metode metode_pembayaran NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    refund_amount NUMERIC(10,2) DEFAULT 0 CHECK (refund_amount >= 0),
    refunded_at TIMESTAMP NULL,
    waktu_pembayaran TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_payments_updated
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION set_updated_at();



-- =====================================================
-- Reviews
-- =====================================================
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    reviewer_id INT NOT NULL REFERENCES app_users(id),
    reviewee_id INT NOT NULL REFERENCES app_users(id),
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER trg_reviews_updated
BEFORE UPDATE ON reviews
FOR EACH ROW EXECUTE FUNCTION set_updated_at();



-- =====================================================
-- Order Status History
-- =====================================================
CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status order_status NOT NULL,
    changed_by INT REFERENCES app_users(id),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT
);

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);



-- =====================================================
-- Wallet & Transactions
-- =====================================================


CREATE TABLE user_wallet (
    user_id INT PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE,
    balance NUMERIC(15,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE wallet_transactions (
    id BIGSERIAL PRIMARY KEY,
    wallet_id INT NOT NULL REFERENCES user_wallet(user_id) ON DELETE CASCADE,
    type wallet_trans_type NOT NULL,
    amount NUMERIC(15,2) NOT NULL CHECK (amount >= 0),
    order_id BIGINT REFERENCES orders(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- =====================================================
-- User Verification / KYC
-- =====================================================
DO $$
BEGIN
    CREATE TYPE verification_status AS ENUM ('PENDING','APPROVED','REJECTED');
EXCEPTION WHEN duplicate_object THEN null;
END $$;

CREATE TABLE user_verification (
    user_id INT PRIMARY KEY REFERENCES app_users(id),
    status verification_status DEFAULT 'PENDING',
    document_type VARCHAR(50),
    document_path VARCHAR(255),
    rejected_reason TEXT,
    verified_at TIMESTAMP NULL,
    verified_by INT REFERENCES app_users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- App Config
-- =====================================================
CREATE TABLE app_config (
    id SERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
