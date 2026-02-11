-- =====================================================
-- Database: taksol_db
-- =====================================================
DROP DATABASE IF EXISTS taksol_db;
CREATE DATABASE taksol_db;
USE taksol_db;

-- =====================================================
-- Tabel app_users (semua pengguna: customer, driver, admin)
-- =====================================================
CREATE TABLE app_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    no_hp VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255),          -- untuk auth
    last_login DATETIME NULL,
    deleted_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- Roles & user_roles
-- =====================================================
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL,
    parent_id INT NULL,
    CONSTRAINT fk_role_parent FOREIGN KEY(parent_id) REFERENCES roles(id)
);

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_user_role FOREIGN KEY(user_id) REFERENCES app_users(id),
    CONSTRAINT fk_role_user FOREIGN KEY(role_id) REFERENCES roles(id)
);

-- =====================================================
-- Kendaraan & jenis kendaraan
-- =====================================================
CREATE TABLE jenis_kendaraan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_jenis VARCHAR(200) NOT NULL,
    kapasitas INT UNSIGNED NOT NULL,
    tarif_per_km DECIMAL(10,2) UNSIGNED NOT NULL,
    deskripsi TEXT
);

CREATE TABLE kendaraan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plat_nomor VARCHAR(15) NOT NULL UNIQUE,
    jenis_kendaraan_id INT NOT NULL,
    driver_id INT NOT NULL UNIQUE,
    status ENUM('ACTIVE','INACTIVE','MAINTENANCE') DEFAULT 'ACTIVE',
    CONSTRAINT fk_jenis_kendaraan FOREIGN KEY(jenis_kendaraan_id) REFERENCES jenis_kendaraan(id),
    CONSTRAINT fk_user_driver FOREIGN KEY(driver_id) REFERENCES app_users(id)
);

-- =====================================================
-- Orders & order_details
-- =====================================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    lokasi_jemput VARCHAR(200) NOT NULL,
    lokasi_tujuan VARCHAR(200) NOT NULL,
    assigned_driver_id INT NULL,         -- driver yang ditugaskan
    status ENUM('PENDING','ACCEPTED','ON_PROGRESS','COMPLETED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    harga_total DECIMAL(10,2) NOT NULL DEFAULT 0,
    status_pembayaran ENUM('UNPAID','PAID','REFUNDED') NOT NULL DEFAULT 'UNPAID',
    deleted_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_driver FOREIGN KEY(assigned_driver_id) REFERENCES app_users(id)
);

CREATE TABLE order_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,     -- satu order = satu detail
    jenis_kendaraan_id INT NOT NULL,
    kendaraan_id INT NULL,
    jarak_km DECIMAL(10,2) NOT NULL,
    tarif_per_km DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    deleted_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_detail_order FOREIGN KEY(order_id) REFERENCES orders(id),
    CONSTRAINT fk_detail_jenis_kendaraan FOREIGN KEY(jenis_kendaraan_id) REFERENCES jenis_kendaraan(id),
    CONSTRAINT fk_detail_kendaraan FOREIGN KEY(kendaraan_id) REFERENCES kendaraan(id)
);

CREATE TABLE user_orders (
    user_id INT NOT NULL,
    order_id BIGINT NOT NULL,
    role_in_order ENUM('CUSTOMER','DRIVER','ADMIN') NOT NULL,
    PRIMARY KEY(user_id, order_id),
    CONSTRAINT fk_user_order_user FOREIGN KEY(user_id) REFERENCES app_users(id),
    CONSTRAINT fk_user_order_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

-- =====================================================
-- Payments
-- =====================================================
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    jumlah DECIMAL(10,2) NOT NULL,
    metode ENUM('CASH','CARD','WALLET') NOT NULL,
    status ENUM('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    refund_amount DECIMAL(10,2) DEFAULT 0,
    refunded_at DATETIME NULL,
    waktu_pembayaran DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

-- =====================================================
-- Reviews
-- =====================================================
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    reviewer_id INT NOT NULL,
    reviewee_id INT NOT NULL,
    rating SMALLINT NOT NULL,
    comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_order FOREIGN KEY(order_id) REFERENCES orders(id),
    CONSTRAINT fk_review_reviewer FOREIGN KEY(reviewer_id) REFERENCES app_users(id),
    CONSTRAINT fk_review_reviewee FOREIGN KEY(reviewee_id) REFERENCES app_users(id)
);

-- =====================================================
-- Order Status History
-- =====================================================
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status ENUM('PENDING','ACCEPTED','ON_PROGRESS','COMPLETED','CANCELLED') NOT NULL,
    changed_by INT NULL,
    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    CONSTRAINT fk_order_status FOREIGN KEY(order_id) REFERENCES orders(id)
);

-- =====================================================
-- Wallet & Transactions
-- =====================================================
CREATE TABLE user_wallet (
    user_id INT PRIMARY KEY,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY(user_id) REFERENCES app_users(id)
);

CREATE TABLE wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id INT NOT NULL,
    type ENUM('TOPUP','PAYMENT','REFUND') NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    order_id BIGINT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_trans_wallet FOREIGN KEY(wallet_id) REFERENCES user_wallet(user_id),
    CONSTRAINT fk_wallet_trans_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

-- =====================================================
-- User Verification / KYC
-- =====================================================
CREATE TABLE user_verification (
    user_id INT PRIMARY KEY,
    status ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    document_type VARCHAR(50),
    document_path VARCHAR(255),
    rejected_reason TEXT,
    verified_at DATETIME NULL,
    verified_by INT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_verification_user FOREIGN KEY(user_id) REFERENCES app_users(id),
    CONSTRAINT fk_user_verification_by FOREIGN KEY(verified_by) REFERENCES app_users(id)
);

-- =====================================================
-- App Config
-- =====================================================
CREATE TABLE app_config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
