# Taksol Database (taksol_db)

## Deskripsi

`taksol_db` adalah database sistem pemesanan transportasi  
yang dirancang menyerupai aplikasi seperti Gojek / Grab.

Project ini mencakup:

- Perancangan DDL MySQL
- Simulasi alur bisnis menggunakan DML
- Konversi struktur database ke PostgreSQL

---

---

## 1️⃣ DDL MySQL (Struktur Awal)

Berisi pembuatan:

- Database
- Tabel
- Primary Key & Foreign Key
- Constraint relasi

📄 File:  
`sql/taksol_db.sql`

🖼️ ERD / Struktur:  
`images/taksol_db_sql.png`

---

## 2️⃣ DML (Simulasi Alur Bisnis)

Berisi simulasi:

- Pembuatan role
- Registrasi user
- Assign role
- Driver menerima order
- Pembayaran
- Order selesai

📄 File:  
`sql/dml_taksol_db.sql`

---

## 3️⃣ Konversi ke PostgreSQL

Struktur database dikonversi dari MySQL ke PostgreSQL dengan penyesuaian:

- AUTO_INCREMENT → SERIAL / BIGSERIAL
- ENUM disesuaikan
- Constraint disesuaikan dengan standar PostgreSQL

📄 File:  
`sql/DDL_taksol_db_postgre.sql`

🖼️ ERD PostgreSQL:  
`images/taksol_db_postgre.png`

---

## Author

**Yohanes Fransiskus Making**  
Dimata Bootcamp
