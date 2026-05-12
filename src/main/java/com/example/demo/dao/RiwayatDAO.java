package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RiwayatDAO {
    
    public void insert(String userEmail, int appId, String aktivitas, double nominal) throws SQLException {
        String sql = "INSERT INTO riwayat_transaksi (user_email, app_id, aktivitas, nominal) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            stmt.setString(3, aktivitas);
            stmt.setDouble(4, nominal);
            stmt.executeUpdate();
        }
    }

    public List<String> findByUserAndApp(String userEmail, int appId) throws SQLException {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT aktivitas, nominal, created_at FROM riwayat_transaksi " +
                     "WHERE user_email = ? AND app_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String log = "[" + rs.getTimestamp("created_at") + "] " + 
                                 rs.getString("aktivitas") + " : Rp " + rs.getDouble("nominal");
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    public void transferRiwayat(String oldEmail, String newEmail) throws SQLException {
        String sql = "UPDATE riwayat_transaksi SET user_email = ? WHERE user_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newEmail);
            stmt.setString(2, oldEmail);
            stmt.executeUpdate();
        }
    }

    public List<String> findByUser(String userEmail) throws SQLException {
        List<String> logs = new ArrayList<>();
        String sql = "SELECT a.nama as app_name, r.aktivitas, r.nominal, r.created_at " +
                     "FROM riwayat_transaksi r JOIN apps a ON r.app_id = a.id " +
                     "WHERE r.user_email = ? ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String log = "[" + rs.getTimestamp("created_at") + "] [" + 
                                 rs.getString("app_name") + "] " + 
                                 rs.getString("aktivitas") + " : Rp " + rs.getDouble("nominal");
                    logs.add(log);
                }
            }
        }
        return logs;
    }
}
