-- Tambah role root
INSERT INTO roles(role, parent_id) VALUES
('ADMIN', NULL),
('DRIVER', NULL),
('CUSTOMER_BASIC', NULL);

select * from roles;

-- Ambil id CUSTOMER_BASIC
SET @customer_basic_id = (SELECT id FROM roles WHERE role='CUSTOMER_BASIC');

select @customer_basic_id;


-- Tambah sub-role loyalty
INSERT INTO roles(role, parent_id) VALUES
('CUSTOMER_SILVER', @customer_basic_id),
('CUSTOMER_GOLD', @customer_basic_id);

-- Tambah Users
INSERT INTO app_users(nama, email, no_hp) VALUES
('Andi', 'andi@email.com', '08123456789'),
('Budi', 'budi@email.com', '08234567890'),
('Admin1', 'admin@email.com', '08987654321');

select * from app_users;

-- Assign Roles
-- Andi = CUSTOMER_BASIC
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM app_users WHERE nama='Andi'), 
        (SELECT id FROM roles WHERE role='CUSTOMER_BASIC'));

-- Budi = DRIVER
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM app_users WHERE nama='Budi'), 
        (SELECT id FROM roles WHERE role='DRIVER'));

-- Admin1 = ADMIN
INSERT INTO user_roles(user_id, role_id)
VALUES ((SELECT id FROM app_users WHERE nama='Admin1'),
        (SELECT id FROM roles WHERE role='ADMIN'));


select u.nama, r.role from app_users u
join user_roles ur on u.id = ur.user_id 
join roles r on r.id = ur.role_id;


#driver budi mendaftarkan kendaraannya
-- Tambah jenis kendaraan
INSERT INTO jenis_kendaraan(nama_jenis, kapasitas, tarif_per_km, deskripsi)
VALUES ('Mobil', 4, 5000.00, 'Mobil untuk 4 penumpang');

select * from jenis_kendaraan;

-- Kendaraan milik Budi
INSERT INTO kendaraan(plat_nomor, jenis_kendaraan_id, driver_id)
VALUES ('DK1234AB',
        (SELECT id FROM jenis_kendaraan WHERE nama_jenis='Mobil'),
        (SELECT id FROM app_users WHERE nama='Budi'));

select * from kendaraan;


-- Customer membuat order, andi mau berangkat dari dps ke jimbaran

-- Buat order umum
INSERT INTO orders(lokasi_jemput, lokasi_tujuan)
VALUES ('Denpasar', 'Kuta');

select * from orders;

-- Ambil id order terakhir
SET @order_id = LAST_INSERT_ID();

select @order_id;

-- Hubungkan user dengan order: Andi sebagai CUSTOMER
INSERT INTO user_orders(user_id, order_id, role_in_order)
VALUES ((SELECT id FROM app_users WHERE nama='Andi'), @order_id, 'CUSTOMER');

select * from user_orders;

-- Detail order: pilih jenis kendaraan dan jarak
INSERT INTO order_details(order_id, jenis_kendaraan_id, jarak_km, tarif_per_km, subtotal)
VALUES (@order_id,
        (SELECT id FROM jenis_kendaraan WHERE nama_jenis='Mobil'),
        10, 5000.00, 50000.00);

select * from order_details;

-- Update harga total di orders
UPDATE orders SET harga_total = 50000.00 WHERE id = @order_id;

-- update dari 0 ke 500000
select * from orders; 


-- Budi sebagai driver melihat orderan dan menerima order tersebut

-- Hubungkan driver dengan order
INSERT INTO user_orders(user_id, order_id, role_in_order)
VALUES ((SELECT id FROM app_users WHERE nama='Budi'), @order_id, 'DRIVER');

-- order id sama dan dua role berbeda
select * from user_orders;

-- Update status order dari pending ke accepted
UPDATE orders SET status='ACCEPTED' WHERE id=@order_id;


-- Pilih kendaraan driver di detail-> sebelumnya kendaraan id masih null, ke kendaraan milik budi
UPDATE order_details
SET kendaraan_id = (SELECT id FROM kendaraan WHERE driver_id=(SELECT id FROM app_users WHERE nama='Budi'))
WHERE order_id=@order_id;

select* from order_details;

-- Andi membayar order menggunakan cash. Status pembayaran berubah menjadi PAID, dan riwayat dicatat
-- Catat pembayaran
INSERT INTO payments(order_id, jumlah, metode, status)
VALUES (@order_id, 50000.00, 'CASH', 'PAID');




select * from payments;

-- Budi selesai mengantar Andi → status order berubah menjadi COMPLETED.
UPDATE orders SET status='COMPLETED', status_pembayaran='PAID' WHERE id=@order_id;

select * from orders;


-- Driver melihat order yang tersedia (belum di-accept driver)
SELECT
    o.id AS order_id,
    o.lokasi_jemput,
    o.lokasi_tujuan,
    o.status,
    o.harga_total,
    od.jarak_km,
    od.subtotal,
    u.nama AS customer_nama,
    u.no_hp AS customer_nohp
FROM orders o
JOIN order_details od ON o.id = od.order_id
JOIN user_orders uo 
    ON o.id = uo.order_id 
   AND uo.role_in_order = 'CUSTOMER'
JOIN app_users u 
    ON u.id = uo.user_id
WHERE o.status = 'PENDING';



-- Driver ingin melihat semua order yang ditangani
-- Untuk Driver Budi
SELECT 
    o.id AS order_id,
    o.lokasi_jemput,
    o.lokasi_tujuan,
    o.status,
    o.harga_total,
    o.status_pembayaran,
    uo_driver.role_in_order AS my_role,
    u.nama AS customer_nama,
    u.no_hp AS customer_nohp,
    od.kendaraan_id,
    od.jarak_km,
    od.subtotal
FROM orders o
JOIN user_orders uo_driver 
    ON o.id = uo_driver.order_id 
   AND uo_driver.role_in_order = 'DRIVER'
JOIN app_users driver 
    ON driver.id = uo_driver.user_id 
   AND driver.nama = 'Budi'
JOIN user_orders uo_customer 
    ON o.id = uo_customer.order_id 
   AND uo_customer.role_in_order = 'CUSTOMER'
JOIN app_users u 
    ON u.id = uo_customer.user_id
JOIN order_details od 
    ON o.id = od.order_id;


-- Customer ingin melihat status ordernya.
-- Untuk Customer Andi
SELECT 
    o.id AS order_id,
    o.lokasi_jemput,
    o.lokasi_tujuan,
    o.status,
    o.harga_total,
    o.status_pembayaran,
    uo.role_in_order AS my_role,        -- role customer
    u2.nama AS driver_nama,
    u2.no_hp AS driver_nohp
FROM orders o
JOIN user_orders uo 
    ON o.id = uo.order_id 
    AND uo.role_in_order='CUSTOMER'    -- pastikan join ini hanya untuk CUSTOMER
JOIN app_users u 
    ON u.id = uo.user_id 
    AND u.nama='Andi'                  -- filter order customer Andi
LEFT JOIN user_orders uo2 
    ON o.id = uo2.order_id AND uo2.role_in_order='DRIVER'
LEFT JOIN app_users u2 
    ON u2.id = uo2.user_id;



-- Jika order dibatalkan sebelum driver menerima, hapus order dan detail.
-- Hapus detail order
DELETE FROM order_details WHERE order_id=@order_id;

select*from order_details;

-- Hapus user_orders
DELETE FROM user_orders WHERE order_id=@order_id;


-- Hapus order
DELETE FROM orders WHERE id=@order_id;










