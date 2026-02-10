# Taksol Database (taksol_db)

## Deskripsi

`taksol_db` adalah database untuk sistem pemesanan transportasi
yang dirancang menyerupai alur aplikasi seperti Gojek / Grab.

Database ini mendukung:

- Multi-role user (ADMIN, DRIVER, CUSTOMER + sub-role)
- Pemesanan transportasi oleh customer
- Driver menerima / mengerjakan order
- Perhitungan tarif dan detail perjalanan
- Pencatatan pembayaran dan status order

---

## Teknologi

- Database: **MySQL**
- Database Client: **DBeaver**
- Version Control: **Git & GitHub**

---

---

## Konsep Desain Database

### 1. Multi Role & Hierarki Role

- User dapat memiliki lebih dari satu role (many-to-many)
- Role customer memiliki hierarki:
  - CUSTOMER_BASIC
  - CUSTOMER_SILVER
  - CUSTOMER_GOLD

Relasi ini diatur melalui:

- `roles`
- `user_roles`

---

### 2. Pemesanan (Order) Fleksibel

- Tabel `orders` hanya menyimpan **informasi umum order**
- Relasi user ke order disimpan di tabel `user_orders`
- Satu order bisa memiliki:
  - 1 CUSTOMER
  - 1 DRIVER (setelah driver menerima order)

Pendekatan ini membuat sistem:

- Lebih fleksibel
- Mudah dikembangkan (misalnya multi-driver, admin monitoring)

---

### 3. Detail Teknis Order Terpisah

- Tabel `order_details` menyimpan:
  - Jenis kendaraan
  - Jarak tempuh
  - Tarif per km
  - Subtotal
  - Kendaraan driver (setelah order diterima)

---

### 4. Pembayaran Terpisah

- Tabel `payments` mencatat:
  - Metode pembayaran
  - Status pembayaran
  - Waktu pembayaran

---

## Penjelasan Tabel Utama

### 1. `app_users`

Menyimpan semua pengguna sistem:

- Customer
- Driver
- Admin

---

### 2. `roles`

Menyimpan role dan hierarki role (parent-child).

---

### 3. `user_roles`

Tabel penghubung many-to-many antara user dan role.

---

### 4. `jenis_kendaraan`

Menyimpan tipe kendaraan (Mobil, Motor, dll) beserta tarif dan kapasitas.

---

### 5. `kendaraan`

Menyimpan kendaraan milik driver.

- Satu driver hanya memiliki satu kendaraan.

---

### 6. `orders`

Menyimpan data umum pemesanan:

- Lokasi jemput & tujuan
- Status order (PENDING, ACCEPTED, COMPLETED, CANCELLED)
- Status pembayaran

---

### 7. `user_orders`

Menghubungkan user dengan order berdasarkan peran:

- CUSTOMER
- DRIVER

---

### 8. `order_details`

Menyimpan detail teknis perjalanan dan tarif.

---

### 9. `payments`

Menyimpan riwayat pembayaran order.

---

## Alur Bisnis (Simulasi di DML)

Contoh alur yang disimulasikan dalam `dml_taksol_db.sql`:

1. Admin membuat role
2. User mendaftar (Andi, Budi, Admin)
3. Assign role ke user
4. Driver mendaftarkan kendaraan
5. Customer membuat order
6. Driver melihat order PENDING
7. Driver menerima order
8. Customer melakukan pembayaran
9. Order selesai (COMPLETED)

---

## Query Database

### DDL (Struktur Database)

📄 `sql/taksol_db.sql`  
Berisi:

- CREATE DATABASE
- CREATE TABLE
- PRIMARY KEY & FOREIGN KEY

### DML (Simulasi Data & Alur)

📄 `sql/dml_taksol_db.sql`  
Berisi:

- INSERT data awal
- UPDATE status order
- SELECT untuk customer & driver
- DELETE simulasi pembatalan order

---

---

## Author

Nama: **Yohanes Fransiskus Making**  
Program: **Dimata Bootcamp**
