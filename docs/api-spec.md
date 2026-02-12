Taksol API-Spec

1. Users (app_users)
   1.1 Create User

Endpoint: POST /users

Description: Menambahkan user baru ke sistem

Authentication : Tidak memerlukan authentication (public endpoint).

Authorization : Tidak memerlukan role.

Request Header : Content-Type: application/json

Request Body:

| Field    | Type   | Required | Validation           |
| -------- | ------ | -------- | -------------------- |
| nama     | string | Yes      | max 200 char         |
| email    | string | Yes      | format email, unique |
| noHp     | string | Yes      | unique, max 20 char  |
| password | string | Yes      | minimal 6 karakter   |

Contoh Request Body:

{
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"password": "secret123"
}

Business Rules

- Email harus unik.

- No HP harus unik.

- Password akan disimpan sebagai password_hash (bcrypt/argon2).

- deleted_at harus NULL (tidak boleh register ulang jika belum dibersihkan).

Success Response : 201 Created

Response Header:
Content-Type: application/json
Location: /users/1

Response Body:
{
"id": 1,
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"createdAt": "2026-02-11T15:00:00"
}

Error Responses

Semua error menggunakan format standar berikut:
{
"timestamp": "2026-02-11T15:00:00",
"status": 400,
"error": "Bad Request",
"message": "Email sudah terdaftar",
"path": "/users"
}

400 Bad Request

Kasus:

Field wajib kosong

Format email tidak valid

Password kurang dari 6 karakter

Contoh:
{
"timestamp": "2026-02-11T15:00:00",
"status": 400,
"error": "Bad Request",
"message": "Email format tidak valid",
"path": "/users"
}

409 Conflict

Kasus:

Email sudah terdaftar

No HP sudah terdaftar

Contoh:
{
"timestamp": "2026-02-11T15:00:00",
"status": 409,
"error": "Conflict",
"message": "Email sudah terdaftar",
"path": "/users"
}

500 Internal Server Error

Kasus:

Database failure

Unexpected server error,
contoh:
{
"timestamp": "2026-02-11T15:00:00",
"status": 500,
"error": "Internal Server Error",
"message": "Terjadi kesalahan pada server",
"path": "/users"
}

Error Handling:

400 Bad Request → Field kosong

400 Bad Request → Email format salah

409 Conflict → Email sudah terdaftar

409 Conflict → NoHp sudah terdaftar

500 Internal Server Error → Database error

1.2 Get User

Endpoint: GET /users/{id}

Description: Mengambil detail user berdasarkan ID
Authorization :

- ADMIN dapat mengakses semua user.

- User hanya dapat mengakses datanya sendiri.

Request Header:
Authorization: Bearer <jwt_token>
Accept: application/json

Success Response 200 (OK)
Response-Header:
Content-Type: application/json

Response Body:
{
"id": 1,
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"createdAt": "2026-02-11T15:00:00"
}

Error Responses

Semua error menggunakan format standar berikut:

{
"timestamp": "2026-02-11T15:00:00",
"status": 401,
"error": "Unauthorized",
"message": "Token tidak valid",
"path": "/users/1"
}
401 Unauthorized

Kasus:

- Token tidak dikirim

- Token tidak valid

- Token expired

403 Forbidden

Kasus:

User mencoba mengakses data user lain tanpa role ADMIN:
{
"timestamp": "2026-02-11T15:00:00",
"status": 403,
"error": "Forbidden",
"message": "Anda tidak memiliki akses ke resource ini",
"path": "/users/2"
}

404 Not Found

Kasus:

User dengan ID tersebut tidak ditemukan

User sudah soft deleted (deleted_at tidak NULL):
{
"timestamp": "2026-02-11T15:00:00",
"status": 404,
"error": "Not Found",
"message": "User tidak ditemukan",
"path": "/users/99"
}

500 Internal Server Error

Kasus:

- Database failure
- Unexpected server error
  {
  "timestamp": "2026-02-11T15:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Terjadi kesalahan pada server",
  "path": "/users/1"
  }

Error Handling:
401 Unauthorized → Token invalid / expired

403 Forbidden → Tidak berhak mengakses user lain

404 Not Found → User tidak ditemukan

500 Internal Server Error → Database error

1.3 Update User

Endpoint: PUT /users/{id}
Description: Mengupdate data user berdasarkan ID.

Authentication : Required (JWT Bearer Token).

Authorization :

ADMIN dapat mengupdate semua user.

User hanya dapat mengupdate datanya sendiri.

Request Header:
Authorization: Bearer <jwt_token>
Content-Type: application/json
Accept: application/json

Request Body:
| Field | Type | Required | Validation |
| ----- | ------ | -------- | -------------------- |
| nama | string | Yes | max 200 char |
| email | string | Yes | format email, unique |
| noHp | string | Yes | unique, max 20 char |

contoh:
{
"nama": "budi",
"email": "budi@example.com",
"noHp": "081234567890"
}

Business Rules

User harus ada dan deleted_at harus NULL.

Email tidak boleh sama dengan user lain.

No HP tidak boleh sama dengan user lain.

Field tidak boleh kosong.

updated_at otomatis terupdate oleh trigger database.

Response (200 OK)
Response Header:
Content-Type: application/json

Response Body:
{
"id": 1,
"nama": "budi",
"email": "budi@example.com",
"noHp": "081234567890",
"updatedAt": "2026-02-11T15:10:00"
}

Error Responses

Semua error menggunakan format standar berikut:
{
"timestamp": "2026-02-11T15:10:00",
"status": 400,
"error": "Bad Request",
"message": "Email sudah digunakan",
"path": "/users/1"
}
400 Bad Request

Kasus:

Field kosong

Format email tidak valid

Data tidak sesuai validasi

{
"timestamp": "2026-02-11T15:10:00",
"status": 400,
"error": "Bad Request",
"message": "Email format tidak valid",
"path": "/users/1"
}

401 Unauthorized

Kasus:

Token tidak dikirim

Token tidak valid

Token expired

{
"timestamp": "2026-02-11T15:10:00",
"status": 401,
"error": "Unauthorized",
"message": "Token tidak valid atau sudah expired",
"path": "/users/1"
}

403 Forbidden

Kasus:

User mencoba mengupdate data user lain tanpa role ADMIN
{
"timestamp": "2026-02-11T15:10:00",
"status": 403,
"error": "Forbidden",
"message": "Anda tidak memiliki akses untuk mengupdate user ini",
"path": "/users/2"
}

404 Not Found

Kasus:

User tidak ditemukan

User sudah soft deleted
{
"timestamp": "2026-02-11T15:10:00",
"status": 404,
"error": "Not Found",
"message": "User tidak ditemukan",
"path": "/users/99"
}

409 Conflict

Kasus:

Email sudah digunakan user lain

No HP sudah digunakan user lain
{
"timestamp": "2026-02-11T15:10:00",
"status": 409,
"error": "Conflict",
"message": "Email sudah terdaftar",
"path": "/users/1"
}

500 Internal Server Error

Kasus:

Database failure

Unexpected server error
{
"timestamp": "2026-02-11T15:10:00",
"status": 500,
"error": "Internal Server Error",
"message": "Terjadi kesalahan pada server",
"path": "/users/1"
}

Error Handling:

400 Bad Request → Field kosong / validasi gagal

401 Unauthorized → Token invalid / expired

403 Forbidden → Tidak berhak update user lain

404 Not Found → User tidak ditemukan

409 Conflict → Email / NoHp sudah digunakan

500 Internal Server Error → Database error

1.4 Delete User (Soft Delete)

Endpoint: DELETE /users/{id}
Description:
Melakukan soft delete terhadap user (mengisi field deleted_at tanpa menghapus data secara fisik).

Authentication : Required (JWT Bearer Token).

Authorization :

ADMIN dapat menghapus user mana pun.

User hanya dapat menghapus akunnya sendiri.

Tidak dapat menghapus user yang sudah di-soft delete.

Request Header:
Authorization: Bearer <jwt_token>
Accept: application/json

Business Rules :

User harus ada.
deleted_at harus NULL (tidak boleh sudah terhapus).
Soft delete dilakukan dengan mengisi deleted_at = CURRENT_TIMESTAMP.
Data relasi tetap ada (karena ini soft delete).
Setelah soft delete:

- User tidak bisa login.
- User tidak muncul di list aktif.
  Hanya ADMIN atau user itu sendiri yang boleh melakukan aksi ini.

Success Response: 204 No Content
Response Header : Content-Type: application/json
Tidak ada response body.

Error Responses

Semua error menggunakan format standar berikut:
{
"timestamp": "2026-02-11T15:20:00",
"status": 404,
"error": "Not Found",
"message": "User tidak ditemukan",
"path": "/users/10"
}

Error Handling:

- 404 Not Found

Kasus:

User tidak ditemukan

User sudah di-soft delete sebelumnya → User tidak ditemukan

- 401 Unauthorized

Kasus:

Token tidak dikirim

Token invalid

Token expired → Token invalid

- 403 Forbidden

Kasus:

User mencoba menghapus user lain tanpa role ADMIN → Tidak boleh menghapus user lain

- 500 Internal Server Error

Kasus:

Database failure

Unexpected server error → Database error

2. Roles & UserRoles
   2.1 List Roles

Endpoint: GET /roles

Response (200 OK):

[
{ "id": 1, "role": "ADMIN", "parentId": null },
{ "id": 2, "role": "CUSTOMER_BASIC", "parentId": null }
]

Error Handling: 500 Internal Server Error → Database error

2.2 Assign Role to User

Endpoint: POST /user_roles

Request Body:

{
"userId": 1,
"roleId": 2
}

Response (201 Created):

{
"userId": 1,
"roleId": 2
}

Error Handling:

404 Not Found → User atau Role tidak ditemukan

409 Conflict → Role sudah terassign

400 Bad Request → Body invalid

500 Internal Server Error → Database error

3. Kendaraan & Jenis Kendaraan
   3.1 Create Jenis Kendaraan

Endpoint: POST /jenis_kendaraan

Request Body:

{
"namaJenis": "Mobil",
"kapasitas": 4,
"tarifPerKm": 5000,
"deskripsi": "Mobil standar"
}

Response (201 Created)

Error Handling:

400 Bad Request → Field kosong

400 Bad Request → Kapasitas < 0

400 Bad Request → Tarif < 0

500 Internal Server Error → Database error

3.2 Create Kendaraan

Endpoint: POST /kendaraan

Request Body:

{
"platNomor": "DK1234AB",
"jenisKendaraanId": 1,
"driverId": 2,
"status": "ACTIVE"
}

Error Handling:

400 Bad Request → Field kosong

409 Conflict → Plat nomor atau driver sudah ada

404 Not Found → Jenis kendaraan atau driver tidak ada

500 Internal Server Error → Database error

4. Orders & Order Details
   4.1 Create Order

Endpoint: POST /orders

Request Body:

{
"lokasiJemput": "Denpasar",
"lokasiTujuan": "Ubud",
"assignedDriverId": 2
}

Error Handling:

400 Bad Request → Field kosong

404 Not Found → Driver tidak ada

409 Conflict → Driver sudah terassign order

500 Internal Server Error → Database error

4.2 Create Order Detail

Endpoint: POST /order_details

Request Body:

{
"orderId": 1,
"jenisKendaraanId": 1,
"kendaraanId": 2,
"jarakKm": 10,
"tarifPerKm": 5000,
"subtotal": 50000
}

Error Handling:

404 Not Found → Order / kendaraan / jenis kendaraan tidak ada

409 Conflict → Order detail sudah ada

400 Bad Request → Field invalid

500 Internal Server Error → Database error

5. Payments
   5.1 Create Payment

Endpoint: POST /payments

Request Body:

{
"orderId": 1,
"jumlah": 50000,
"metode": "CASH"
}

Error Handling:

404 Not Found → Order tidak ada

409 Conflict → Payment sudah ada

400 Bad Request → Field invalid

500 Internal Server Error → Database error

6. Reviews
   6.1 Create Review

Endpoint: POST /reviews

Request Body:

{
"orderId": 1,
"reviewerId": 1,
"revieweeId": 2,
"rating": 5,
"comment": "Pelayanan cepat"
}

Error Handling:

404 Not Found → Order / User tidak ada

409 Conflict → Review sudah ada

400 Bad Request → Rating <1 atau >5

500 Internal Server Error → Database error

7. Wallet & Transactions
   7.1 Create Transaction

Endpoint: POST /wallet/transactions

Modul: Wallet & Transactions

Deskripsi:
Membuat transaksi pada wallet pengguna, bisa berupa TOPUP, PAYMENT, atau REFUND.

Request Body contoh:

{
"walletId": 1,
"type": "TOPUP",
"amount": 100000
}

Response:
201 Created

Error Handling:

404 Not Found → Wallet tidak ditemukan

400 Bad Request → Amount kurang dari 0

400 Bad Request → Tipe transaksi tidak valid (harus TOPUP, PAYMENT, REFUND)

409 Conflict → Saldo tidak cukup (khusus PAYMENT/REFUND)

500 Internal Server Error → Database error

8. User Verification
   8.1 Submit Verification

Endpoint: POST /user_verification

Request Body:

{
"userId": 1,
"documentType": "KTP",
"documentPath": "/uploads/ktp.jpg"
}

Error Handling:

404 Not Found → User tidak ada

400 Bad Request → Field kosong

409 Conflict → Verifikasi sudah ada

500 Internal Server Error → Database error

9. App Config
   9.1 Get Config

Endpoint: GET /app_config/{key}

Response (200 OK):

{
"configKey": "max_order_per_day",
"configValue": "10",
"description": "Maksimal order per user per hari"
}

Error Handling:

404 Not Found → Config key tidak ada

500 Internal Server Error → Database error

Catatan Umum:

Semua endpoint menggunakan JSON request/response

Status code: 200 OK / 201 Created / 204 No Content / 400 / 401 / 403 / 404 / 409 / 500

Soft delete diterapkan di beberapa tabel (app_users, orders, dll)
