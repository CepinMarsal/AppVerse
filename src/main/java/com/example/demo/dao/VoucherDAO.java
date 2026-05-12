package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import com.example.demo.model.Voucher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {

    public void insert(String userEmail, int appId, Voucher voucher) throws SQLException {
        String sql = "INSERT INTO vouchers (user_email, app_id, nama, tipe, nilai, min_transaksi, is_used, expiry_date, fingerprint) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            stmt.setString(3, voucher.getNama());
            stmt.setString(4, voucher.getTipe());
            stmt.setDouble(5, voucher.getNilai());
            stmt.setDouble(6, voucher.getMin());
            stmt.setBoolean(7, false);
            stmt.setString(8, voucher.getExpiryDate());
            stmt.setString(9, voucher.getFingerprint());
            stmt.executeUpdate();
        }
    }

    public List<Voucher> findByUserAndApp(String userEmail, int appId) throws SQLException {
        List<Voucher> vouchers = new ArrayList<>();
        // Only return vouchers that are not used and not expired
        String sql = "SELECT id, nama, tipe, nilai, min_transaksi, expiry_date, fingerprint FROM vouchers " +
                     "WHERE user_email = ? AND app_id = ? AND is_used = 0 " +
                     "AND (expiry_date IS NULL OR expiry_date >= CURDATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Voucher v = new Voucher(
                        rs.getString("nama"),
                        rs.getString("tipe"),
                        rs.getDouble("nilai"),
                        rs.getDouble("min_transaksi"),
                        rs.getString("expiry_date"),
                        rs.getString("fingerprint")
                    );
                    v.setId(rs.getInt("id"));
                    vouchers.add(v);
                }
            }
        }
        return vouchers;
    }

    public boolean isFingerprintAlreadyUsedForNewUserVoucher(String fingerprint, int appId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM vouchers WHERE fingerprint = ? AND app_id = ? AND nama LIKE '%User Baru%'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fingerprint);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void initializeVouchersForNewUser(String userEmail, String fingerprint, int appId) throws SQLException {
        // Prevent duplicate initialization for general vouchers
        String checkSql = "SELECT COUNT(*) FROM vouchers WHERE user_email = ? AND app_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }

        String appName = "";
        try (java.sql.Connection conn = com.example.demo.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT nama FROM apps WHERE id = ?")) {
            stmt.setInt(1, appId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) appName = rs.getString("nama");
            }
        }

        if (appName.isEmpty() || appName.equals("Instagram")) return;

        // New User Voucher (Common for all apps except Insta)
        // Valid for 7 days
        String expiry = java.time.LocalDate.now().plusDays(7).toString();
        insert(userEmail, appId, new Voucher("Voucher Pengguna Baru", "PERSEN", 0.5, 0, expiry, fingerprint));

        // App-Specific Vouchers
        if (appName.equals("Shopee")) {
            insert(userEmail, appId, new Voucher("Voucher Shopee Mantap", "NOMINAL", 50000, 100000, null, fingerprint));
            insert(userEmail, appId, new Voucher("Gratis Ongkir Shopee", "NOMINAL", 20000, 0, null, fingerprint));
        } else if (appName.equals("Tokopedia")) {
            insert(userEmail, appId, new Voucher("Kupon Tokped Hemat", "NOMINAL", 30000, 80000, null, fingerprint));
            insert(userEmail, appId, new Voucher("Diskon Toped", "PERSEN", 0.1, 0, null, fingerprint));
        } else if (appName.equals("Fore")) {
            insert(userEmail, appId, new Voucher("Fore Free Coffee", "NOMINAL", 35000, 35000, null, fingerprint));
            insert(userEmail, appId, new Voucher("Diskon Fore 50%", "PERSEN", 0.5, 0, null, fingerprint));
        } else if (appName.equals("Janji Jiwa")) {
            insert(userEmail, appId, new Voucher("Kopi Jiwa Gratis", "NOMINAL", 22000, 22000, null, fingerprint));
            insert(userEmail, appId, new Voucher("Diskon Jiwa", "NOMINAL", 10000, 40000, null, fingerprint));
        } else if (appName.equals("Gojek")) {
            insert(userEmail, appId, new Voucher("Gojek Hemat Perjalanan", "NOMINAL", 5000, 10000, null, fingerprint));
            insert(userEmail, appId, new Voucher("Voucher GoFood", "NOMINAL", 15000, 40000, null, fingerprint));
        }
    }

    public void useVoucher(int voucherId) throws SQLException {
        String sql = "UPDATE vouchers SET is_used = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voucherId);
            stmt.executeUpdate();
        }
    }

    public void transferVouchers(String oldUserEmail, String newUserEmail) throws SQLException {
        String sql = "UPDATE vouchers SET user_email = ? WHERE user_email = ? AND is_used = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newUserEmail);
            stmt.setString(2, oldUserEmail);
            stmt.executeUpdate();
        }
    }
}
