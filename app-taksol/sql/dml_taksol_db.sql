-- Matikan sementara pengecekan foreign key
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE wallet_transactions;
TRUNCATE TABLE user_wallet;
TRUNCATE TABLE order_status_history;
TRUNCATE TABLE reviews;
TRUNCATE TABLE payments;
TRUNCATE TABLE order_details;
TRUNCATE TABLE user_orders;
TRUNCATE TABLE orders;
TRUNCATE TABLE kendaraan;
TRUNCATE TABLE jenis_kendaraan;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE roles;
TRUNCATE TABLE user_verification;
TRUNCATE TABLE app_config;
TRUNCATE TABLE app_users;

-- Aktifkan kembali foreign key check
SET FOREIGN_KEY_CHECKS = 1;





-- Budi dan Dina mendaftar
INSERT INTO app_users (nama, email, no_hp, password_hash)
VALUES 
('Budi', 'budi@example.com', '081234567890', 'hash_password_123'),
('Dina Customer', 'dina@example.com', '089876543210', 'hash_password_456');

-- Cek hasil registrasi
SELECT * FROM app_users;


-- Buat role DRIVER dan CUSTOMER
INSERT INTO roles (role) VALUES ('DRIVER'), ('CUSTOMER');

-- Assign role ke user
INSERT INTO user_roles (user_id, role_id)
VALUES 
((SELECT id FROM app_users WHERE nama='Budi'),
 (SELECT id FROM roles WHERE role='DRIVER')),
((SELECT id FROM app_users WHERE nama='Dina Customer'),
 (SELECT id FROM roles WHERE role='CUSTOMER'));

-- Cek hasil role assignment
SELECT au.nama, r.role
FROM app_users au
JOIN user_roles ur ON au.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;


-- Budi mengupload KTP, status PENDING
INSERT INTO user_verification (user_id, status, document_type, document_path)
VALUES ((SELECT id FROM app_users WHERE nama='Budi'), 'PENDING', 'KTP', '/docs/budi_ktp.jpg');

-- Cek status verifikasi
SELECT au.nama, uv.status, uv.document_type, uv.document_path
FROM user_verification uv
JOIN app_users au ON uv.user_id = au.id;


-- Buat jenis kendaraan
INSERT INTO jenis_kendaraan (nama_jenis, kapasitas, tarif_per_km, deskripsi)
VALUES 
('Motor', 1, 2.5, 'Motor untuk 1 penumpang'),
('Mobil', 4, 5.0, 'Mobil untuk 4 penumpang');

-- Driver Budi menambahkan motor
INSERT INTO kendaraan (plat_nomor, jenis_kendaraan_id, driver_id, status)
VALUES ('DK1234XY', 
        (SELECT id FROM jenis_kendaraan WHERE nama_jenis='Motor'),
        (SELECT id FROM app_users WHERE nama='Budi'),
        'ACTIVE');

-- Cek kendaraan driver
SELECT k.plat_nomor, jk.nama_jenis, k.status, au.nama AS driver
FROM kendaraan k
JOIN jenis_kendaraan jk ON k.jenis_kendaraan_id = jk.id
JOIN app_users au ON k.driver_id = au.id;


-- Dina buat order
INSERT INTO orders (lokasi_jemput, lokasi_tujuan)
VALUES ('Jl. Sudirman No.10', 'Jl. Thamrin No.20');

-- Hubungkan customer ke order
INSERT INTO user_orders (user_id, order_id, role_in_order)
VALUES (
    (SELECT id FROM app_users WHERE nama='Dina Customer'),
    LAST_INSERT_ID(),
    'CUSTOMER'
);

-- Tambahkan order detail
INSERT INTO order_details (order_id, jenis_kendaraan_id, jarak_km, tarif_per_km, subtotal)
VALUES (
    (SELECT id FROM orders ORDER BY id DESC LIMIT 1),
    (SELECT id FROM jenis_kendaraan WHERE nama_jenis='Motor'),
    10,
    2.5,
    25.00
);

-- Cek order dan detail
SELECT o.id AS order_id, au.nama AS customer, o.lokasi_jemput, o.lokasi_tujuan, od.jarak_km, od.subtotal
FROM orders o
JOIN order_details od ON o.id = od.order_id
JOIN user_orders uo ON o.id = uo.order_id
JOIN app_users au ON uo.user_id = au.id
WHERE uo.role_in_order = 'CUSTOMER';



-- Ambil order terakhir
SET @last_order_id = (SELECT id FROM orders ORDER BY id DESC LIMIT 1);

-- Assign Budi sebagai driver dan update status
UPDATE orders
SET assigned_driver_id = (SELECT id FROM app_users WHERE nama='Budi'),
    status = 'ACCEPTED'
WHERE id = @last_order_id;

-- Catat histori status
INSERT INTO order_status_history (order_id, status, changed_by, remarks)
VALUES (@last_order_id, 'ACCEPTED', NULL, 'System auto-assigned driver');

-- Cek hasil
SELECT o.id, o.status, au.nama AS driver, osh.status AS history_status, osh.remarks
FROM orders o
LEFT JOIN app_users au ON o.assigned_driver_id = au.id
LEFT JOIN order_status_history osh ON o.id = osh.order_id;



-- Update status trip selesai
UPDATE orders
SET status = 'COMPLETED',
    status_pembayaran = 'PAID',
    harga_total = 25.00
WHERE id = @last_order_id;

-- Catat histori status selesai
INSERT INTO order_status_history (order_id, status, changed_by, remarks)
VALUES (@last_order_id, 'COMPLETED', (SELECT id FROM app_users WHERE nama='Budi'), 'Trip finished successfully');

-- Catat payment
INSERT INTO payments (order_id, jumlah, metode, status)
VALUES (@last_order_id, 25.00, 'WALLET', 'SUCCESS');

-- Cek payment & status order
SELECT o.id, o.status, o.status_pembayaran, p.jumlah, p.metode, p.status
FROM orders o
JOIN payments p ON o.id = p.order_id;

select * from payments;


-- Pastikan customer punya wallet
INSERT INTO user_wallet (user_id, balance)
SELECT id, 0
FROM app_users
WHERE nama='Dina Customer'
ON DUPLICATE KEY UPDATE balance = balance;

-- Ambil wallet_id
SET @wallet_id = (SELECT user_id FROM user_wallet uw JOIN app_users au ON uw.user_id = au.id WHERE au.nama='Dina Customer');

-- Catat wallet transaction
INSERT INTO wallet_transactions (wallet_id, type, amount, order_id)
VALUES (@wallet_id, 'PAYMENT', 25.00, @last_order_id);

-- Kurangi balance wallet
UPDATE user_wallet
SET balance = balance - 25.00,
    updated_at = NOW()
WHERE user_id = @wallet_id;

-- Cek wallet dan transaksi
SELECT au.nama, uw.balance, wt.type, wt.amount
FROM user_wallet uw
JOIN wallet_transactions wt ON uw.user_id = wt.wallet_id
JOIN app_users au ON uw.user_id = au.id
WHERE au.nama = 'Dina Customer';



-- Dina kasih rating Budi
INSERT INTO reviews (order_id, reviewer_id, reviewee_id, rating, comment)
VALUES (@last_order_id,
        (SELECT id FROM app_users WHERE nama='Dina Customer'),
        (SELECT id FROM app_users WHERE nama='Budi'),
        5,
        'Driver sangat profesional!');

-- Cek review
SELECT r.rating, r.comment, reviewer.nama AS reviewer, reviewee.nama AS reviewee
FROM reviews r
JOIN app_users reviewer ON r.reviewer_id = reviewer.id
JOIN app_users reviewee ON r.reviewee_id = reviewee.id;



-- Soft delete order
UPDATE orders
SET deleted_at = CURRENT_TIMESTAMP
WHERE id = @last_order_id;

-- Soft delete customer
UPDATE app_users
SET deleted_at = CURRENT_TIMESTAMP
WHERE nama='Dina Customer';

-- Cek soft delete
SELECT * FROM orders WHERE deleted_at IS NOT NULL;
SELECT * FROM app_users WHERE deleted_at IS NOT NULL;



