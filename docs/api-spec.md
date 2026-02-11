Taksol API-Spec

1. Users (app_users)
   1.1 Create User

Endpoint: POST /users

Description: Menambahkan user baru ke sistem

Pre-condition:

nama, email, noHp, password wajib

email dan noHp harus unik

Request Body:

{
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"password": "secret123"
}

Response (201 Created):

{
"id": 1,
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"createdAt": "2026-02-11T15:00:00"
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

Response (200 OK):

{
"id": 1,
"nama": "Budi",
"email": "budi@example.com",
"noHp": "081234567890",
"createdAt": "2026-02-11T15:00:00"
}

Error Handling:

404 Not Found → User tidak ditemukan

401 Unauthorized → Token invalid

403 Forbidden → Tidak berhak mengakses

500 Internal Server Error → Database error

1.3 Update User

Endpoint: PUT /users/{id}

Request Body:

{
"nama": "budi",
"email": "budi@example.com",
"noHp": "081234567890"
}

Response (200 OK):

{
"id": 1,
"nama": "budi",
"email": "budi@example.com",
"noHp": "081234567890",
"updatedAt": "2026-02-11T15:10:00"
}

Error Handling:

400 Bad Request → Field kosong

404 Not Found → User tidak ditemukan

409 Conflict → Email atau NoHp sudah ada

401 Unauthorized → Token invalid

500 Internal Server Error → Database error

1.4 Delete User (Soft Delete)

Endpoint: DELETE /users/{id}

Response: 204 No Content

Error Handling:

404 Not Found → User tidak ditemukan

401 Unauthorized → Token invalid

403 Forbidden → Tidak boleh menghapus user lain

500 Internal Server Error → Database error

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

Request Body:

{
"walletId": 1,
"type": "TOPUP",
"amount": 100000
}

Error Handling:

404 Not Found → Wallet tidak ada

400 Bad Request → Amount < 0

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
