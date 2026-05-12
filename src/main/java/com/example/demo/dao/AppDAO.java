package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import com.example.demo.model.App;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppDAO {
    
    public List<App> findAll() throws SQLException {
        List<App> apps = new ArrayList<>();
        String sql = "SELECT id, nama FROM apps";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                apps.add(new App(rs.getInt("id"), rs.getString("nama")));
            }
        }
        return apps;
    }

    public int findIdByName(String nama) throws SQLException {
        String sql = "SELECT id FROM apps WHERE nama = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }
    public void initializeApps() throws SQLException {
        String[] appNames = {"Shopee", "Tokopedia", "Gojek", "Fore", "Janji Jiwa", "Instagram"};
        String sql = "INSERT INTO apps (id, nama) VALUES (?, ?) ON DUPLICATE KEY UPDATE nama = VALUES(nama)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < appNames.length; i++) {
                stmt.setInt(1, i + 1);
                stmt.setString(2, appNames[i]);
                stmt.executeUpdate();
            }
        }
    }

    public void setupDatabase() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS apps (id INT PRIMARY KEY, nama VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS users (email VARCHAR(255) PRIMARY KEY, name VARCHAR(255), fingerprint VARCHAR(255), user_baru BOOLEAN, active BOOLEAN)");
            stmt.execute("CREATE TABLE IF NOT EXISTS app_saldo (user_email VARCHAR(255), app_id INT, saldo DOUBLE, PRIMARY KEY(user_email, app_id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS vouchers (id INT AUTO_INCREMENT PRIMARY KEY, user_email VARCHAR(255), app_id INT, nama VARCHAR(255), tipe VARCHAR(50), nilai DOUBLE, min_transaksi DOUBLE, is_used BOOLEAN, fingerprint VARCHAR(255), expiry_date DATE)");
            
            try { stmt.execute("ALTER TABLE users ADD COLUMN phone VARCHAR(20)"); } catch (SQLException e) {}
            try { stmt.execute("ALTER TABLE users ADD COLUMN address TEXT"); } catch (SQLException e) {}

            stmt.execute("CREATE TABLE IF NOT EXISTS products (id INT AUTO_INCREMENT PRIMARY KEY, app_id INT, nama VARCHAR(255), harga DOUBLE, kategori VARCHAR(255), image_url VARCHAR(255))");
            stmt.execute("CREATE TABLE IF NOT EXISTS riwayat_transaksi (id INT AUTO_INCREMENT PRIMARY KEY, user_email VARCHAR(255), app_id INT, aktivitas VARCHAR(255), nominal DOUBLE, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            stmt.execute("CREATE TABLE IF NOT EXISTS posts (id INT AUTO_INCREMENT PRIMARY KEY, user_email VARCHAR(255), content TEXT, image_url VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS app_profiles (user_email VARCHAR(255), app_id INT, phone VARCHAR(20), address TEXT, PRIMARY KEY(user_email, app_id))");

            initializeApps(conn);
            initializeProducts(conn);
        }
    }

    public void initializeApps(Connection conn) throws SQLException {
        String[] appNames = {"Shopee", "Tokopedia", "Gojek", "Fore", "Janji Jiwa", "Instagram"};
        String sql = "INSERT IGNORE INTO apps (id, nama) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < appNames.length; i++) {
                stmt.setInt(1, i + 1);
                stmt.setString(2, appNames[i]);
                stmt.executeUpdate();
            }
        }
    }

    public void initializeProducts(Connection conn) throws SQLException {
        // Only insert if empty to avoid duplicates across restarts
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        }

        String sql = "INSERT INTO products (app_id, nama, harga, kategori) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            Object[][] products = {
                // Shopee (1)
                {1, "Samsung Galaxy S23 Ultra", 18000000.0, "Electronics"},
                {1, "Shopee T-Shirt Premium", 75000.0, "Fashion"},
                {1, "Sunscreen SPF 50", 95000.0, "Beauty"},
                {1, "Mechanical Keyboard RGB", 650000.0, "Gaming"},
                {1, "Tas Backpack Waterproof", 250000.0, "Accessories"},
                {1, "Headset Bluetooth Bass", 450000.0, "Electronics"},
                {1, "Botol Minum Aesthetic", 35000.0, "Lifestyle"},
                // Tokopedia (2)
                {2, "MacBook Pro M2", 22000000.0, "Electronics"},
                {2, "Logitech G Pro X Mouse", 1500000.0, "Gaming"},
                {2, "Meja Belajar Kayu Jati", 1200000.0, "Furniture"},
                {2, "Monitor LG 24 Inch 144Hz", 2800000.0, "Electronics"},
                {2, "Kursi Kantor Ergonomis", 1800000.0, "Furniture"},
                {2, "Webcam 4K Ultra HD", 900000.0, "Electronics"},
                {2, "Rak Buku Minimalis", 550000.0, "Furniture"},
                // Fore (4)
                {4, "Fore Pandan Latte", 35000.0, "Coffee"},
                {4, "Butterscotch Sea Salt", 42000.0, "Coffee"},
                {4, "Iced Americano Grande", 28000.0, "Coffee"},
                {4, "Caramel Macchiato", 38000.0, "Coffee"},
                {4, "Hibiscus Berry Tea", 32000.0, "Tea"},
                {4, "Fore Nutty Oat Latte", 45000.0, "Coffee"},
                {4, "Croissant Cheese", 25000.0, "Food"},
                // Janji Jiwa (5)
                {5, "Jiwa Kopi Susu", 22000.0, "Coffee"},
                {5, "Jiwa Toast Egg & Cheese", 32000.0, "Food"},
                {5, "Kopi Aren Original", 20000.0, "Coffee"},
                {5, "Jiwa Matcha Latte", 25000.0, "Non-Coffee"},
                {5, "Jiwa Toast Curry Chicken", 38000.0, "Food"},
                {5, "Jiwa Earl Grey Milk Tea", 24000.0, "Tea"},
                {5, "Kopi Hitam Manis", 15000.0, "Coffee"}
            };
            for (Object[] p : products) {
                stmt.setInt(1, (int) p[0]);
                stmt.setString(2, (String) p[1]);
                stmt.setDouble(3, (double) p[2]);
                stmt.setString(4, (String) p[3]);
                stmt.executeUpdate();
            }
        }
    }
}
