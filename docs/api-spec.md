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

1.2 Authentication

1.2.1 Login
Endpoint: POST /auth/login

Description:
Melakukan autentikasi user dan menghasilkan Access Token (JWT – RS256) dan Refresh Token.

Authentication : Tidak memerlukan authentication (public endpoint).

Authorization : Tidak memerlukan role.

Request Header:
Content-Type: application/json
Accept: application/json

Request Body:
| Field | Type | Required | Validation |
| -------- | ------ | -------- | ------------------ |
| email | string | Yes | format email |
| password | string | Yes | minimal 6 karakter |

contoh:
{
"email": "budi@example.com",
"password": "secret123"
}

Business Rules:

1. Email harus terdaftar.
2. User tidak boleh dalam kondisi soft deleted (deleted_at IS NOT NULL).
3. Password harus sesuai dengan password_hash (bcrypt).
4. Access Token ditandatangani menggunakan RSA (RS256).
5. Access Token berisi:
   - userId
   - email
   - role
   - issuedAt
   - expiredAt
6. Refresh Token:

- Dibuat sebagai random secure string.
- Disimpan di database.
- Memiliki masa berlaku (default: 7 hari).
- Digunakan untuk mendapatkan access token baru.

7. Access Token berlaku singkat (default: 15 menit).

Success Response:
Content-Type: application/json

Response Body:
{
"accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
"refreshToken": "def50200a4b3c...",
"tokenType": "Bearer",
"expiresIn": 900,
"refreshExpiresIn": 604800
}

Error Response
Semua error menggunakan format standard berikut:
{
"timestamp": "2026-02-11T15:00:00",
"status": 401,
"error": "Unauthorized",
"message": "Email atau password salah",
"path": "/auth/login"
}

- 400 Bad Request
  Kasus:
  Field wajib kosong
  Format email tidak valid
  Password kurang dari 6 karakter

- 401 Unauthorized
  Kasus:
  Email tidak ditemukan
  Password salah
  User sudah soft deleted

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

  1.2.2 Refresh Token
  Endpoint: POST /auth/refresh

Description:
Menghasilkan access token baru menggunakan refresh token (dengan mekanisme token rotation).

Authentication : Tidak memerlukan access token.
Authorization : Tidak memerlukan role.

Request Header:
Content-Type: application/json
Accept: application/json

Request Body:
| Field | Type | Required | Validation |
| ------------ | ------ | -------- | ------------ |
| refreshToken | string | Yes | tidak kosong |

contoh:
{
"refreshToken": "def50200a4b3c..."
}

Business Rules:
Refresh token harus valid dan terdaftar di database.
Refresh token tidak boleh expired.
Refresh token tidak boleh revoked.
Jika valid:

- Buat access token baru.
- Buat refresh token baru (rotation).
- Tandai refresh token lama sebagai revoked.
  Jika refresh token dicurigai reuse (sudah revoked), semua token user dapat dibatalkan (opsional keamanan tinggi).

Success Response : 200 OK
{
"accessToken": "new.jwt.token",
"refreshToken": "new.refresh.token",
"tokenType": "Bearer",
"expiresIn": 900,
"refreshExpiresIn": 604800
}

Error Response
Standar format:
{
"timestamp": "2026-02-11T15:00:00",
"status": 401,
"error": "Unauthorized",
"message": "Refresh token tidak valid",
"path": "/auth/refresh"
}

- 401 Unauthorized
  Kasus:
  Refresh token tidak valid
  Refresh token expired
  Refresh token revoked

- 500 Internal Server Error
  Database failure / unexpected error.

  1.2.3 Logout

Endpoint: POST /auth/logout

Description:
Mencabut refresh token sehingga tidak dapat digunakan kembali.

Authentication : Tidak memerlukan access token.
Authorization : Tidak memerlukan role.

Request Body:
{
"refreshToken": "def50200a4b3c..."
}

Business Rules:
Refresh token harus ada di database.
Refresh token akan ditandai sebagai revoked atau dihapus.
Setelah logout, refresh token tidak dapat digunakan kembali.

Success Response:
{
"message": "Logout berhasil"
}

Error Responses:
401 Unauthorized → Refresh token tidak valid
500 Internal Server Error → Database error

1.3 Get User

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

1.4 Update User

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

1.5 Delete User (Soft Delete)

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
Description:
Mengambil daftar seluruh role yang tersedia di sistem, termasuk relasi parent role (jika ada).

Authentication:
Required (JWT Bearer Token).

Authorization:
Hanya role ADMIN yang dapat mengakses endpoint ini.

Request Header:
Authorization: Bearer <jwt_token>
Accept: application/json

Business Rules:

- Data role diambil dari tabel roles.

- parentId bernilai null jika role tidak memiliki parent.

- Endpoint ini hanya menampilkan role aktif.

Success Response (200 OK)
Response Header:
Content-Type: application/json

Response Body:
[
{ "id": 1, "role": "ADMIN", "parentId": null },
{ "id": 2, "role": "CUSTOMER_BASIC", "parentId": null },
{ "id": 3, "role": "CUSTOMER_SILVER", "parentId": 2 }
]

Error Responses
Semua error menggunakan format standar berikut:
{
"timestamp": "2026-02-11T16:00:00",
"status": 500,
"error": "Internal Server Error",
"message": "Terjadi kesalahan pada server",
"path": "/roles"
}

- 401 Unauthorized
  Kasus:
  Token tidak dikirim
  Token invalid
  Token expired

- 403 Forbidden
  Kasus:
  User bukan ADMIN

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

  2.2 Assign Role to User

Endpoint: POST /user_roles

Description:
Menambahkan role tertentu ke user.

Authentication:
Required (JWT Bearer Token).

Authorization:
Hanya role ADMIN yang dapat melakukan assign role.

Request Header:
Authorization: Bearer <jwt_token>
Content-Type: application/json
Accept: application/json

Request Body:
| Field | Type | Required | Validation |
| ------ | ---- | -------- | ---------------------------- |
| userId | long | Yes | Harus ada di tabel app_users |
| roleId | long | Yes | Harus ada di tabel roles |

Contoh Request Body:
{
"userId": 1,
"roleId": 2
}

Business Rules:

- User harus ada dan deleted_at harus NULL.
- Role harus ada.
- Kombinasi (user_id, role_id) tidak boleh sudah ada.
- Composite PK pada user_roles mencegah duplikasi data.

Success Response (201 Created):
Response Header: Content-Type: application/json

Response Body:
{
"userId": 1,
"roleId": 2
}

Error Responses

Semua error menggunakan format standar berikut:
{
"timestamp": "2026-02-11T16:05:00",
"status": 404,
"error": "Not Found",
"message": "User tidak ditemukan",
"path": "/user_roles"
}

- 400 Bad Request
  Kasus:
  Body kosong
  Field tidak lengkap
  Format tidak sesuai

- 401 Unauthorized
  Kasus:
  Token invalid / expired

- 403 Forbidden
  Kasus:
  Bukan ADMIN

- 404 Not Found
  Kasus:
  User tidak ditemukan
  Role tidak ditemukan

- 409 Conflict
  Kasus:
  Role sudah terassign ke user tersebut

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

3. Kendaraan & Jenis Kendaraan
   3.1 Create Jenis Kendaraan

Endpoint: POST /jenis_kendaraan

Description:
Menambahkan jenis kendaraan baru yang akan digunakan dalam sistem (misalnya Mobil, Motor, dll).

Authentication:
Memerlukan authentication (Bearer Token).

Authorization:
Hanya role ADMIN yang diperbolehkan.

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ---------- | ------- | -------- | ------------------------ |
| namaJenis | string | Yes | max 100 karakter, unique |
| kapasitas | integer | Yes | >= 1 |
| tarifPerKm | number | Yes | >= 0 |
| deskripsi | string | No | max 255 karakter |

Contoh Request Body:
{
"namaJenis": "Mobil",
"kapasitas": 4,
"tarifPerKm": 5000,
"deskripsi": "Mobil standar"
}

Business Rules:
namaJenis harus unik.
kapasitas tidak boleh kurang dari 1.
tarifPerKm tidak boleh negatif.
Soft delete berlaku (tidak boleh membuat namaJenis yang sama jika masih aktif).

Success Response (201 Created)

Response Header:
Content-Type: application/json
Location: /jenis_kendaraan/1

Response Body:
{
"id": 1,
"namaJenis": "Mobil",
"kapasitas": 4,
"tarifPerKm": 5000,
"deskripsi": "Mobil standar",
"createdAt": "2026-02-12T10:00:00"
}

Error Response
Standard Format:
{
"timestamp": "2026-02-12T10:00:00",
"status": 400,
"error": "Bad Request",
"message": "Kapasitas harus lebih dari 0",
"path": "/jenis_kendaraan"
}

- 400 Bad Request
  Kasus:
  Field wajib kosong
  Kapasitas < 1
  Tarif < 0
  Format data tidak valid

- 409 Conflict
  Kasus:
  namaJenis sudah terdaftar

- 401 Unauthorized
  Kasus:
  Token tidak valid atau tidak ada

- 403 Forbidden
  Kasus:
  Role bukan ADMIN

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

  3.2 Create Kendaraan

Endpoint: POST /kendaraan

Description:
Menambahkan kendaraan baru yang terhubung dengan driver tertentu.

Authentication:
Memerlukan authentication (Bearer Token).

Authorization:
Role ADMIN atau DRIVER.

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:

| Field            | Type    | Required | Validation                         |
| ---------------- | ------- | -------- | ---------------------------------- |
| platNomor        | string  | Yes      | unique, max 20 karakter            |
| jenisKendaraanId | integer | Yes      | harus ada di tabel jenis_kendaraan |
| driverId         | integer | Yes      | harus ada & memiliki role DRIVER   |
| status           | string  | Yes      | enum: ACTIVE, INACTIVE             |

contoh:
{
"platNomor": "DK1234AB",
"jenisKendaraanId": 1,
"driverId": 2,
"status": "ACTIVE"
}

Business Rules:
platNomor harus unik.
1 driver hanya boleh memiliki 1 kendaraan aktif.
Driver harus memiliki role DRIVER.
Tidak boleh assign kendaraan ke driver yang sudah di-soft-delete.

Success Response:
Content-Type: application/json
Location: /kendaraan/2

Response Body:
{
"id": 2,
"platNomor": "DK1234AB",
"jenisKendaraanId": 1,
"driverId": 2,
"status": "ACTIVE",
"createdAt": "2026-02-12T10:10:00"
}

Error Response
Standard Error Format:
{
"timestamp": "2026-02-12T10:10:00",
"status": 409,
"error": "Conflict",
"message": "Plat nomor sudah terdaftar",
"path": "/kendaraan"
}

- 400 Bad Request
  Kasus:
  Field wajib kosong
  Status tidak valid
  Format data salah

- 404 Not Found
  Kasus:
  Jenis kendaraan tidak ditemukan
  Driver tidak ditemukan

- 409 Conflict
  Kasus:
  Plat nomor sudah terdaftar
  Driver sudah memiliki kendaraan aktif

- 401 Unauthorized
  Kasus:
  Token tidak valid

- 403 Forbidden
  Kasus:
  Role tidak memiliki akses

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

4. Orders & Order Details
   4.1 Create Order

Endpoint: POST /orders

Description:
Membuat order baru oleh customer untuk perjalanan dari lokasi jemput ke lokasi tujuan.

Authentication:
Memerlukan authentication (Bearer Token).

Authorization:
Hanya role CUSTOMER_BASIC atau CUSTOMER_SILVER.

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ---------------- | ------- | -------- | -------------------------------- |
| lokasiJemput | string | Yes | max 200 karakter |
| lokasiTujuan | string | Yes | max 200 karakter |
| assignedDriverId | integer | No | harus ada & memiliki role DRIVER |

Catatan: assignedDriverId boleh null jika sistem menggunakan auto-assign driver.

Contoh:
{
"lokasiJemput": "Denpasar",
"lokasiTujuan": "Ubud",
"assignedDriverId": 2
}

Business Rules:
User yang login otomatis dicatat di tabel user_orders sebagai role CUSTOMER.
Status awal order: PENDING.
Status pembayaran awal: UNPAID.
Jika assignedDriverId diisi:

- Driver harus ada.

- Driver harus memiliki role DRIVER.

- Driver tidak boleh sedang memiliki order dengan status ACCEPTED atau ON_PROGRESS.

Soft delete berlaku (deleted_at IS NULL).

Success Respond :201 Created
Response Header :
Content-Type: application/json
Location: /orders/1

Response Body:
{
"id": 1,
"lokasiJemput": "Denpasar",
"lokasiTujuan": "Ubud",
"assignedDriverId": 2,
"status": "PENDING",
"statusPembayaran": "UNPAID",
"hargaTotal": 0,
"createdAt": "2026-02-12T11:00:00"
}

Error Response
Standard format:
{
"timestamp": "2026-02-12T11:00:00",
"status": 404,
"error": "Not Found",
"message": "Driver tidak ditemukan",
"path": "/orders"
}

- 400 Bad Request
  Kasus:
  Field wajib kosong
  Format data tidak valid

- 404 Not Found
  Kasus:
  Driver tidak ditemukan

- 409 Conflict
  Kasus:
  Driver sedang dalam order aktif

- 401 Unauthorized
  Kasus:
  Token tidak valid

- 403 Forbidden
  Kasus:
  Role bukan CUSTOMER

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

  4.2 Create Order Detail

Endpoint: POST /order_details

Description:
Menambahkan detail order seperti jenis kendaraan, kendaraan yang digunakan, jarak, dan perhitungan tarif.

Authentication:
Memerlukan authentication (Bearer Token).

Authorization:
Role CUSTOMER atau ADMIN.

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ---------------- | ------- | -------- | ---------------------------------- |
| orderId | integer | Yes | harus ada |
| jenisKendaraanId | integer | Yes | harus ada |
| kendaraanId | integer | Yes | harus ada & sesuai jenis kendaraan |
| jarakKm | number | Yes | >= 0 |
| tarifPerKm | number | Yes | >= 0 |
| subtotal | number | Yes | >= 0 |

Contoh:
{
"orderId": 1,
"jenisKendaraanId": 1,
"kendaraanId": 2,
"jarakKm": 10,
"tarifPerKm": 5000,
"subtotal": 50000
}

Business Rules:
1 order hanya boleh memiliki 1 order_detail (UNIQUE constraint).
orderId harus valid dan belum memiliki detail.
kendaraanId harus sesuai dengan jenisKendaraanId.
subtotal seharusnya = jarakKm \* tarifPerKm (backend tetap melakukan validasi).
Setelah order detail dibuat:

- orders.harga_total diperbarui.
  Tidak bisa membuat detail jika order sudah CANCELLED atau COMPLETED.

Success Response : 201 Created

Response Header:
Content-Type: application/json
Location: /order_details/1

Response Body:
{
"id": 1,
"orderId": 1,
"jenisKendaraanId": 1,
"kendaraanId": 2,
"jarakKm": 10,
"tarifPerKm": 5000,
"subtotal": 50000,
"createdAt": "2026-02-12T11:05:00"
}

Error Response
Standard format:
{
"timestamp": "2026-02-12T11:05:00",
"status": 409,
"error": "Conflict",
"message": "Order detail sudah ada",
"path": "/order_details"
}

Error Handling:

- 400 Bad Request
  Kasus:
  Field kosong
  Nilai negatif
  Subtotal tidak sesuai perhitungan

- 404 Not Found
  Kasus:
  Order tidak ditemukan
  Jenis kendaraan tidak ditemukan
  Kendaraan tidak ditemukan

- 409 Conflict
  Kasus:
  Order detail sudah ada
  Order sudah selesai atau dibatalkan

- 401 Unauthorized
  Kasus:
  Token invalid

- 403 Forbidden
  Kasus:
  Tidak berhak mengakses order tersebut

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

  4.3 Update Order Status

Endpoint : PATCH /orders/{id}/status

Description:
Mengubah status order sesuai dengan lifecycle yang telah ditentukan sistem.
Setiap perubahan status akan:

- Memvalidasi transisi status

- Memvalidasi role yang melakukan perubahan

- Mencatat perubahan ke order_status_history

Authentication:
Wajib menggunakan JWT Bearer Token.
Authorization: Bearer <access_token>

Authorization (Role Based):
| Role | Allowed Action |
| -------- | ------------------------------------- |
| CUSTOMER | CANCELLED (hanya jika status PENDING) |
| DRIVER | ACCEPTED, ON_PROGRESS, COMPLETED |
| ADMIN | Semua status |

Allowed Status Transition (State Machine):
| Current Status | Allowed Next Status |
| -------------- | ---------------------- |
| PENDING | ACCEPTED, CANCELLED |
| ACCEPTED | ON_PROGRESS, CANCELLED |
| ON_PROGRESS | COMPLETED |
| COMPLETED | - (final state) |
| CANCELLED | - (final state) |

Jika transisi tidak sesuai → 400 Bad Request

Request Header:
Content-Type: application/json
Authorization: Bearer <token>

Request Body:
| Field | Type | Required | Validation |
| ------ | ------ | -------- | ----------------- |
| status | string | Yes | ENUM order_status |

contoh:
{
"status": "ACCEPTED"
}

Business Rules:
Order dengan deleted_at != NULL tidak dapat diubah.
Driver hanya boleh mengubah order yang assigned ke dirinya.
Customer hanya boleh cancel order miliknya.
Jika status menjadi COMPLETED:

- Tidak dapat diubah lagi.
  Jika status menjadi CANCELLED:
- Tidak dapat diubah lagi.
  Setiap perubahan harus dicatat ke order_status_history.

Success Response : 200 OK
Response Header:
Content-Type: application/json

Response Body:
{
"id": 1,
"status": "ACCEPTED",
"updatedAt": "2026-02-12T11:20:00"
}

Error Response
Standard Format:
{
"timestamp": "2026-02-12T11:20:00",
"status": 400,
"error": "Bad Request",
"message": "Transisi status tidak valid",
"path": "/orders/1/status"
}

Error Handling:

- 400 Bad Request
  Kasus:
  Status tidak valid
  Transisi status tidak sesuai state machine
  Order sudah dalam status final
  Body kosong

- 401 Unauthorized
  Kasus:
  Token tidak ada
  Token invalid
  Token expired

- 403 Forbidden
  Kasus:
  Driver mengubah order milik driver lain
  Customer mencoba ACCEPT order
  Role tidak memiliki hak akses

- 404 Not Found
  Kasus:
  Order tidak ditemukan

- 409 Conflict
  Kasus:
  Order sedang diproses oleh driver lain
  Driver mencoba accept order yang sudah ACCEPTED

- 500 Internal Server Error
  Kasus:
  Gagal insert ke order_status_history
  Database failure

5. Payments
   5.1 Create Payment

Endpoint: POST /payments

Description:
Membuat pembayaran untuk sebuah order.
Endpoint ini akan:

- Membuat record pada tabel payments
- engupdate orders.status_pembayaran
- Mengupdate orders.status jika diperlukan
- Memicu wallet transaction jika metode = WALLET

Authentication:
Required (JWT Bearer Token)

Authorization:
CUSTOMER → hanya boleh membayar order miliknya
ADMIN → boleh membuat pembayaran untuk order mana pun
DRIVER → tidak boleh membuat payment

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ------- | ------ | -------- | ----------------------------- |
| orderId | number | Yes | Must exist & not soft deleted |
| jumlah | number | Yes | >= 0 |
| metode | string | Yes | CASH / CARD / WALLET |

Contoh:
{
"orderId": 1,
"jumlah": 50000,
"metode": "CASH"
}

Business Rules:

1. Order harus ada dan deleted_at IS NULL

2. Order harus berstatus COMPLETED
3. orders.status_pembayaran harus UNPAID

4. Jumlah harus sama dengan orders.harga_total

5. Satu order hanya boleh memiliki satu payment

6. Jika metode = WALLET:

- Saldo user harus cukup

- Akan dibuat record di wallet_transactions

- Balance akan dikurangi

7. Setelah payment sukses:

- orders.status_pembayaran → PAID

- payments.status → SUCCESS

- waktu_pembayaran diisi

Success Response : 201 Created

Response Header:
Content-Type: application/json
Location: /payments/{id}

Response Body:
{
"id": 10,
"orderId": 1,
"jumlah": 50000,
"metode": "CASH",
"status": "SUCCESS",
"waktuPembayaran": "2026-02-12T12:10:00"
}

Error Responses
Semua error menggunakan format standar:
{
"timestamp": "2026-02-12T12:10:00",
"status": 400,
"error": "Bad Request",
"message": "Order belum selesai",
"path": "/payments"
}

- 400 Bad Request
  Kasus:
  Field kosong
  Jumlah < 0
  Metode tidak valid
  Order belum COMPLETED
  Jumlah tidak sesuai harga_total

- 401 Unauthorized
  Token tidak valid
  Token expired

- 403 Forbidden
  User bukan pemilik order
  Role tidak diizinkan membuat payment
- 404 Not Found
  Order tidak ditemukan

- 409 Conflict
  Payment sudah ada untuk order ini
  Saldo tidak cukup (metode WALLET)
  Order sudah PAID

- 500 Internal Server Error
  Kasus:
  Database failure
  Gagal update status order
  Transaction rollback

  5.2 Refund Payment

Endpoint : POST /payments/{paymentId}/refund

Description:
Melakukan refund terhadap payment yang sudah SUCCESS.
Refund bisa terjadi karena:

- Driver tidak datang
- Order dibatalkan oleh sistem
- Overcharge
- Dispute
- Admin decision

Authentication:
Required (JWT Bearer)

Authorization:
ADMIN → boleh refund semua payment
CUSTOMER → hanya boleh refund order miliknya (jika status memungkinkan)
DRIVER → tidak boleh refund

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Description |
| ------ | ------ | -------- | ------------------- |
| reason | string | Yes | Alasan refund |
| amount | number | No | Jika partial refund |

contoh:
{
"reason": "Driver tidak datang"
}

contoh partial refund:
{
"reason": "Overcharge 5rb",
"amount": 5000
}

Business Rules:

1. Payment harus ada
2. Payment.status harus SUCCESS
3. Belum pernah di-refund penuh
4. Total refund tidak boleh melebihi jumlah pembayaran
5. Jika metode WALLET:

- Tambah saldo wallet
- Buat wallet transaction type = REFUND

6. Jika metode CASH:

- System hanya mencatat refund (manual process)

7. Jika metode CARD:

- Diproses seperti CASH (manual simulation)

8. Refund harus diproses dalam database transaction untuk mencegah double refund.

Refund Flow
1️⃣ Validasi payment
2️⃣ Hitung remaining refundable amount
3️⃣ Update payment:

- refund_amount += amount
- Jika refund_amount == jumlah:
  status = REFUNDED
  refunded_at = CURRENT_TIMESTAMP

4️⃣ Jika WALLET:

- Tambah saldo wallet
- Insert wallet_transaction (type=REFUND)

5️⃣ Jika full refund:

- orders.status_pembayaran → REFUNDED

Success Response: 200 OK
{
"paymentId": 10,
"originalAmount": 50000,
"refundedAmount": 50000,
"refundStatus": "FULL",
"message": "Refund berhasil diproses"
}

Error Handling:

- 400 Bad Request
  Reason kosong
  Amount <= 0
  Amount melebihi sisa refundable

{
"status": 400,
"error": "Bad Request",
"message": "Jumlah refund melebihi sisa yang dapat direfund",
"path": "/payments/10/refund"
}

- 401 Unauthorized
  Token invalid / expired

- 403 Forbidden
  User bukan pemilik order dan bukan ADMIN

- 404 Not Found
  Payment tidak ditemukan

- 500 Internal Server Error
  Database error / rollback

6. Reviews
   6.1 Create Review

Endpoint: POST /reviews
Description:
Membuat review terhadap user lain berdasarkan order yang sudah COMPLETED.

Authentication:
Memerlukan authentication (Bearer Token).

Authorization:
CUSTOMER → boleh review driver dalam order miliknya
DRIVER → boleh review customer dalam order yang dikerjakan
ADMIN → tidak diperbolehkan membuat review

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ---------- | ------- | -------- | ---------------------------- |
| orderId | integer | Yes | harus ada & status COMPLETED |
| revieweeId | integer | Yes | harus terlibat dalam order |
| rating | integer | Yes | 1–5 |
| comment | string | No | max 1000 karakter |

contoh:
{
"orderId": 10,
"revieweeId": 5,
"rating": 5,
"comment": "Driver sangat ramah dan tepat waktu"
}
reviewerId diambil dari JWT (bukan dari request body).

Business Rules:

1. Order harus ada.
2. Order.status harus COMPLETED.
3. Reviewer harus terlibat dalam order.
4. Reviewee harus terlibat dalam order.
5. Reviewer tidak boleh mereview dirinya sendiri.
6. Satu reviewer hanya boleh membuat 1 review per order.
7. Rating harus antara 1 sampai 5.
8. Review diproses dalam database transaction.

Success Response: Status: 201 Created

Content-Type: application/json
Location: /reviews/{id}

Response Body:
{
"id": 15,
"orderId": 10,
"reviewerId": 3,
"revieweeId": 5,
"rating": 5,
"comment": "Driver sangat ramah dan tepat waktu",
"createdAt": "2026-02-12T12:30:00"
}

6.2 Get Reviews by User

Endpoint:
GET /users/{userId}/reviews

Description:
Mengambil daftar review yang diterima oleh user tertentu.

Authentication:
Optional (atau Required jika ingin proteksi).

Success Response:
{
"content": [
{
"id": 15,
"orderId": 10,
"reviewerId": 3,
"rating": 5,
"comment": "Driver sangat ramah",
"createdAt": "2026-02-12T12:30:00"
}
],
"page": 1,
"size": 10,
"totalElements": 25,
"totalPages": 3
}

Error Response (Standard Format):
{
"timestamp": "2026-02-12T12:30:00",
"status": 409,
"error": "Conflict",
"message": "Review sudah pernah dibuat untuk order ini",
"path": "/reviews"
}

- 400 Bad Request
  Kasus:
  Rating < 1 atau > 5
  orderId kosong
  revieweeId kosong
  Reviewer dan reviewee sama

- 401 Unauthorized
  Kasus:
  Token invalid / expired

- 403 Forbidden
  Kasus:
  User bukan bagian dari order
  Role tidak diizinkan membuat review

- 404 Not Found
  Kasus:
  Order tidak ditemukan
  Reviewee tidak ditemukan

- 409 Conflict
  Kasus:
  Order belum COMPLETED
  Review sudah pernah dibuat

- 404 Not Found
  Kasus:
  User tidak ditemukan

- 500 Internal Server Error
  Kasus:
  Database failure
  Unexpected server error

7. Wallet & Transactions
   7.1 Get My Wallet

Endpoint:
GET /wallet/me

Description:
Mengambil informasi wallet user yang sedang login.

Authentication:
Required (Bearer Token)

Authorization:
CUSTOMER atau DRIVER

Success Response : 200 Ok
{
"userId": 5,
"balance": 150000,
"updatedAt": "2026-02-12T13:00:00"
}

Error Response (Standard Format):
{
"timestamp": "2026-02-12T13:00:00",
"status": 404,
"error": "Not Found",
"message": "Wallet tidak ditemukan",
"path": "/wallet/me"
}

Possible Errors:

- 401 Unauthorized
  Kasus:
  Token tidak valid
  Token expired
  Tidak mengirim Authorization header

- 403 Forbidden
  Kasus:
  Role bukan CUSTOMER atau DRIVER

- 404 Not Found
  Kasus:
  Wallet belum dibuat untuk user tersebut

- 500 Internal Server Error
  Kasus:
  Database error
  Unexpected server failure

  7.2 Topup Wallet

Endpoint:
POST /wallet/topup

Description:
Melakukan topup saldo wallet user.

Authentication:
Required (Bearer Token)

Authorization:
CUSTOMER atau DRIVER

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ------ | ------ | -------- | ---------- |
| amount | number | Yes | > 0 |
Contoh:
{
"amount" : 100000
}

Business Rules:

1. Wallet harus ada.
2. Amount harus lebih dari 0.
3. Topup diproses dalam database transaction.
4. Update balance += amount.
5. Insert wallet_transaction type = TOPUP.

Success Response (201 Created):
{
"walletId": 5,
"type": "TOPUP",
"amount": 100000,
"balanceAfter": 250000,
"createdAt": "2026-02-12T13:05:00"
}

Error Response (Standard Format):
{
"timestamp": "2026-02-12T13:05:00",
"status": 400,
"error": "Bad Request",
"message": "Amount harus lebih dari 0",
"path": "/wallet/topup"
}

Possible Errors:

- 400 Bad Request
  Kasus:
  Amount kosong
  Amount <= 0
  Format number salah

- 401 Unauthorized
  Kasus:
  Token invalid / expired

- 403 Forbidden
  Kasus:
  Role tidak diizinkan

- 404 Not Found
  Kasus:
  Wallet tidak ditemukan

- 500 Internal Server Error
  Kasus:
  Database error
  Gagal commit transaction

  7.3 Get My Wallet Transactions

Endpoint:
GET /wallet/me/transactions

Description:
Mengambil daftar transaksi wallet milik user yang sedang login.

Authentication:
Required (Bearer Token)

Success Response:
{
"content": [
{
"id": 101,
"type": "TOPUP",
"amount": 100000,
"orderId": null,
"createdAt": "2026-02-12T13:05:00"
},
{
"id": 102,
"type": "PAYMENT",
"amount": 50000,
"orderId": 10,
"createdAt": "2026-02-12T13:10:00"
}
],
"page": 1,
"size": 10,
"totalElements": 12,
"totalPages": 2
}

Error Response (Standard Format):
{
"timestamp": "2026-02-12T13:10:00",
"status": 401,
"error": "Unauthorized",
"message": "Token invalid",
"path": "/wallet/me/transactions"
}

Possible Errors:

- 401 Unauthorized
  Kasus:
  Token invalid / expired

- 403 Forbidden
  Kasus:
  Role tidak diizinkan

- 404 Not Found
  Kasus:
  Wallet tidak ditemukan

- 400 Bad Request
  Kasus:
  page < 1
  size <= 0

- 500 Internal Server Error
  Kasus:
  Database error

8. User Verification

Modul ini digunakan untuk proses verifikasi identitas (KYC) user.
Lifecycle:
PENDING → APPROVED
PENDING → REJECTED
REJECTED → (boleh submit ulang → kembali PENDING)

Enum:
verification_status = ('PENDING','APPROVED','REJECTED')

8.1 Submit Verification

Endpoint:
POST /me/verification

Description:
Mengirim atau mengajukan ulang dokumen verifikasi identitas.

Authentication:
Required (Bearer Token)

Authorization:
CUSTOMER atau DRIVER

Request Header:
Content-Type: application/json
Authorization: Bearer <access_token>

Request Body:
| Field | Type | Required | Validation |
| ------------ | ------ | -------- | ---------------- |
| documentType | string | Yes | max 50 karakter |
| documentPath | string | Yes | max 255 karakter |
contoh:
{
"documentType": "KTP",
"documentPath": "/uploads/ktp.jpg"
}
userId diambil dari JWT.

Business Rules:

1. User harus ada dan tidak dalam kondisi soft delete.
2. Jika belum ada record → INSERT dengan status = PENDING.
3. Jika status = REJECTED → UPDATE dan set status = PENDING.
4. Jika status = PENDING → 409 Conflict.
5. Jika status = APPROVED → 409 Conflict.
6. Proses dilakukan dalam database transaction.

Success Response : 201 Created
{
"userId": 5,
"status": "PENDING",
"submittedAt": "2026-02-12T14:00:00"
}

8.2 Get My Verification

Endpoint: GET /me/verification
Authentication: Required

Success Response 200 OK:
{
"userId": 5,
"status": "PENDING",
"documentType": "KTP",
"documentPath": "/uploads/ktp.jpg",
"rejectedReason": null,
"verifiedAt": null,
"verifiedBy": null,
"createdAt": "2026-02-12T14:00:00",
"updatedAt": "2026-02-12T14:00:00"
}

8.3 Admin – List Verifications
Endpoint:
GET /admin/verifications

Authentication: Required

Authorization: ADMIN only

Success Response 200 OK:
{
"content": [
{
"userId": 5,
"userName": "Budi",
"status": "PENDING",
"documentType": "KTP",
"createdAt": "2026-02-12T14:00:00"
}
],
"page": 1,
"size": 10,
"totalElements": 5,
"totalPages": 1
}

8.4 Admin – Approve Verification
Endpoint:
PUT /admin/verifications/{userId}/approve

Authentication:
Required

Authorization:
ADMIN only

Business Rules:

1. Verification harus ada.
2. Status harus PENDING.
3. Set status = APPROVED.
4. Set verified_at = NOW().
5. Set verified_by = adminId (from JWT).
6. Proses dalam database transaction.

Success Response 200 OK:
{
"message": "Verification approved"
}

8.5 Admin – Reject Verification
Endpoint:
PUT /admin/verifications/{userId}/reject

Request Body:
| Field | Type | Required |
| ------ | ------ | -------- |
| reason | string | Yes |
contoh:
{
"reason": "Dokumen tidak jelas"
}

Business Rules:

1. Verification harus ada.
2. Status harus PENDING.
3. Set status = REJECTED.
4. Set rejected_reason.
5. Set verified_at dan verified_by.
6. Database transaction.

Success Response 200 Ok:
{
"message": "Verification rejected"
}

Standard Error Response Format
Semua endpoint menggunakan format berikut:
{
"timestamp": "2026-02-12T14:10:00",
"status": 409,
"error": "Conflict",
"message": "Verification sudah dalam status APPROVED",
"path": "/me/verification"
}

Error Handling Summary:

- 400 Bad Request
  Kasus:
  Field kosong
  documentType terlalu panjang
  documentPath terlalu panjang
  reason kosong (untuk reject)

- 401 Unauthorized
  Kasus:
  Token tidak valid
  Token expired
  Tidak mengirim Authorization header

- 403 Forbidden
  Kasus:
  Role bukan ADMIN untuk endpoint admin
  Role tidak diizinkan submit verification

- 404 Not Found
  Kasus:
  User tidak ditemukan
  Verification tidak ditemukan
  User sudah di-soft-delete
  Jika user belum pernah mengajukan verification.

- 409 Conflict
  Kasus:
  Verification masih PENDING
  Verification sudah APPROVED
  Status tidak valid untuk diproses

- 500 Internal Server Error
  Kasus:
  Database error
  Gagal commit transaction
  Unexpected server failure

9. App Config

Modul ini digunakan untuk menyimpan konfigurasi global sistem
(seperti limit order, komisi, timeout, dll).
Nilai config_value disimpan sebagai string dan dikonversi di service layer sesuai kebutuhan.

9.1 Get Config
Endpoint: GET /app-config/{key}

Path Variable:
| Field | Type | Required | Description |
| ----- | ------ | -------- | ----------- |
| key | string | Yes | Nama config |

Authentication:
Required (Bearer Token)

Authorization:
ADMIN atau AUTHENTICATED USER
(sesuai kebutuhan bisnis)

Business Rules:

1. Config dengan config_key = {key} harus ada.
2. config_key bersifat unique.
3. Jika tidak ditemukan → 404 Not Found.
4. Proses hanya read-only (tidak ada perubahan data).

Success Response (200 OK):
{
"key": "max_order_per_day",
"value": "10",
"description": "Maksimal order per user per hari",
"createdAt": "2026-02-12T14:00:00",
"updatedAt": "2026-02-12T14:00:00"
}

Standard Error Response Format
Semua error menggunakan format berikut (konsisten dengan modul lain):
{
"timestamp": "2026-02-12T14:10:00",
"status": 404,
"error": "Not Found",
"message": "Config key 'max_order_per_day' not found",
"path": "/app-config/max_order_per_day"
}

Error Handling Summary:

- 400 Bad Request
  Kasus:
  key kosong
  key mengandung karakter tidak valid

- 401 Unauthorized
  Kasus:
  Token tidak dikirim
  Token expired
  Token tidak valid

- 403 Forbidden
  Kasus:
  User tidak memiliki akses untuk membaca config tertentu
  (misalnya config internal hanya untuk ADMIN)

- 404 Not Found
  Kasus:
  Config key tidak ditemukan

- 500 Internal Server Error
  Kasus:
  Database error
  Query gagal
  Unexpected server failure
