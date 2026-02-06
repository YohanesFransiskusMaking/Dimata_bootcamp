-- =====================================================
-- Database: taksol_db
-- Description: Schema database sistem transportasi Taksol
-- =====================================================

CREATE DATABASE taksol_db;

USE taksol_db;

-- =====================================================
-- Tabel app_users
-- =====================================================
CREATE TABLE app_users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    no_hp VARCHAR(20) NOT NULL UNIQUE
);

-- =====================================================
-- Tabel roles
-- =====================================================
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50)
);

-- =====================================================
-- Tabel user_roles (normalisasi user - role)
-- =====================================================
CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role
        FOREIGN KEY (user_id) REFERENCES app_users(id),
    CONSTRAINT fk_role_user
        FOREIGN KEY (role_id) REFERENCES roles(id)
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
-- Tabel kendaraan
-- =====================================================
CREATE TABLE kendaraan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    plat_nomor VARCHAR(15) NOT NULL UNIQUE,
    jenis_kendaraan_id INT NOT NULL,
    driver_id INT NOT NULL,
    CONSTRAINT fk_jenis_kendaraan
        FOREIGN KEY (jenis_kendaraan_id) REFERENCES jenis_kendaraan(id),
    CONSTRAINT fk_user_driver
        FOREIGN KEY (driver_id) REFERENCES app_users(id)
);

-- =====================================================
-- Tabel status_order
-- =====================================================
CREATE TABLE status_order (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_status VARCHAR(50) NOT NULL
);

-- =====================================================
-- Tabel orders
-- =====================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lokasi_jemput VARCHAR(200) NOT NULL,
    lokasi_tujuan VARCHAR(200) NOT NULL,
    waktu_pesan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_id INT NOT NULL,
    pemesan_id INT NOT NULL,
    driver_id INT NULL,
    CONSTRAINT fk_status_order
        FOREIGN KEY (status_id) REFERENCES status_order(id),
    CONSTRAINT fk_user_role_pemesan
        FOREIGN KEY (pemesan_id) REFERENCES app_users(id),
    CONSTRAINT fk_user_role_driver
        FOREIGN KEY (driver_id) REFERENCES app_users(id)
);
