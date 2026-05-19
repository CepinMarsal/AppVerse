package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AppProfileDAO {

    public void saveProfile(String email, int appId, String phone, String address) throws SQLException {
        String sql = "INSERT INTO app_profiles (user_email, app_id, phone, address) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE phone = ?, address = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, appId);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.setString(5, phone);
            stmt.setString(6, address);
            stmt.executeUpdate();
        }
    }

    public Map<String, String> getProfile(String email, int appId) throws SQLException {
        Map<String, String> profile = new HashMap<>();
        String sql = "SELECT phone, address FROM app_profiles WHERE user_email = ? AND app_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile.put("phone", rs.getString("phone"));
                    profile.put("address", rs.getString("address"));
                }
            }
        }
        return profile;
    }
}
