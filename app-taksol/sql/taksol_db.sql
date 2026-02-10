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
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================
-- Tabel roles (role + hierarki customer)
-- =====================================================
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL,             -- ADMIN / DRIVER / CUSTOMER
    parent_id INT NULL,                     -- NULL = root, misal CUSTOMER bisa punya subrole BASIC/SILVER/GOLD
    CONSTRAINT fk_role_parent FOREIGN KEY(parent_id) REFERENCES roles(id)
);

-- =====================================================
-- Tabel user_roles (many-to-many user ↔ roles)
-- =====================================================
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT fk_user_role FOREIGN KEY(user_id) REFERENCES app_users(id),
    CONSTRAINT fk_role_user FOREIGN KEY(role_id) REFERENCES roles(id)
);

-- =====================================================
-- Tabel jenis_kendaraan
-- =====================================================
CREATE TABLE jenis_kendaraan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_jenis VARCHAR(200) NOT NULL,
    kapasitas INT UNSIGNED NOT NULL,
    tarif_per_km DECIMAL(10,2) UNSIGNED NOT NULL,
    deskripsi TEXT
);

-- =====================================================
-- Tabel kendaraan (1 driver = 1 kendaraan)
-- =====================================================
CREATE TABLE kendaraan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plat_nomor VARCHAR(15) NOT NULL UNIQUE,
    jenis_kendaraan_id INT NOT NULL,
    driver_id INT NOT NULL UNIQUE,
    CONSTRAINT fk_jenis_kendaraan FOREIGN KEY (jenis_kendaraan_id) REFERENCES jenis_kendaraan(id),
    CONSTRAINT fk_user_driver FOREIGN KEY (driver_id) REFERENCES app_users(id)
);

-- =====================================================
-- Tabel orders (info umum)
-- =====================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lokasi_jemput VARCHAR(200) NOT NULL,
    lokasi_tujuan VARCHAR(200) NOT NULL,
    waktu_pesan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',      -- PENDING / ACCEPTED / COMPLETED / CANCELLED
    harga_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status_pembayaran VARCHAR(20) NOT NULL DEFAULT 'UNPAID'
);

-- =====================================================
-- Tabel user_orders (many-to-many user ↔ orders)
-- =====================================================
CREATE TABLE user_orders (
    user_id INT NOT NULL,
    order_id INT NOT NULL,
    role_in_order VARCHAR(20) NOT NULL, -- 'CUSTOMER', 'DRIVER', 'ADMIN'
    PRIMARY KEY(user_id, order_id),
    CONSTRAINT fk_user_order_user FOREIGN KEY(user_id) REFERENCES app_users(id),
    CONSTRAINT fk_user_order_order FOREIGN KEY(order_id) REFERENCES orders(id)
);

-- =====================================================
-- Tabel order_details (rincian teknis, termasuk jenis kendaraan yang dipilih customer)
-- =====================================================
CREATE TABLE order_details (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    jenis_kendaraan_id INT NOT NULL,
    kendaraan_id INT NULL,                      -- driver yang menerima order
    jarak_km DECIMAL(10,2) NOT NULL,
    tarif_per_km DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detail_order FOREIGN KEY(order_id) REFERENCES orders(id),
    CONSTRAINT fk_detail_jenis_kendaraan FOREIGN KEY(jenis_kendaraan_id) REFERENCES jenis_kendaraan(id),
    CONSTRAINT fk_detail_kendaraan FOREIGN KEY(kendaraan_id) REFERENCES kendaraan(id)
);

-- =====================================================
-- Tabel payments (riwayat pembayaran)
-- =====================================================
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    jumlah DECIMAL(10,2) NOT NULL,
    metode VARCHAR(50) NOT NULL,               -- e.g., CASH, OVO, GoPay
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING / PAID / FAILED
    waktu_pembayaran DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY(order_id) REFERENCES orders(id)
);
