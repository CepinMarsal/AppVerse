AppVerse - Sistem Identitas Digital dan Integrasi Aplikasi
AppVerse merupakan aplikasi berbasis website yang dikembangkan menggunakan Spring Boot dan MySQL untuk mengelola identitas pengguna, integrasi aplikasi, saldo, transaksi, produk, dan voucher dalam satu sistem terpusat.

Sistem ini mendukung beberapa fitur utama seperti:
- Login dan registrasi pengguna
- Integrasi multi aplikasi
- Riwayat transaksi pengguna
- Pengelolaan saldo aplikasi
- Pengelolaan produk
- Sistem voucher
- Validasi akun dan voucher pengguna

Tools dan Teknologi
- Java: bahasa pemrograman utama
- Spring Boot: backend framework
- Maven: dependency management
- MySQL: database
- HTML & CSS: ampilan website
- JDBC: oneksi database

Dependencies
- Spring Web
- MySQL Driver
- Lombok
- Spring Data JPA

Cara Menjalankan Aplikasi

1. Clone Repository
Clone project dari GitHub

2. Buka Project
Pastikan project terbaca sebagai project Maven.

3. Setup Database MySQL
Buka MySQL/phpMyAdmin lalu buat database baru dengan nama: sistemidentitas_db;
Setelah database dibuat, import file `.sql` yang terdapat pada project.

4. Konfigurasi Database
Buka file: src/main/resources/application.properties
Pastikan konfigurasi database sesuai dengan MySQL lokal:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemidentitas_db
spring.datasource.username=root
spring.datasource.password=
```
Sesuaikan username dan password MySQL jika diperlukan.

5. Install Dependencies Maven
Buka terminal pada folder project lalu jalankan: mvn clean install
Command tersebut digunakan untuk mengunduh seluruh dependency yang dibutuhkan project.

6. Jalankan Aplikasi
Jalankan aplikasi menggunakan command berikut: mvnw.cmd spring-boot:run

7. Akses Website
Jika aplikasi berhasil dijalankan, website dapat diakses melalui browser:http://localhost:8080

Catatan
Pastikan MySQL dalam keadaan aktif sebelum menjalankan aplikasi.
Pastikan database `sistemidentitas_db` sudah dibuat dan berhasil diimport.
Pastikan port `8080` tidak digunakan aplikasi lain.
