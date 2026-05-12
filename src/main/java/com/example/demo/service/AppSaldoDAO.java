package com.example.demo.service;

import com.example.demo.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AppSaldoDAO {

    public void insertOrUpdate(String userEmail, int appId, double saldo) throws SQLException {
        String sql = "INSERT INTO app_saldo (user_email, app_id, saldo) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE saldo = saldo + ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            stmt.setDouble(3, saldo);
            stmt.setDouble(4, saldo);
            stmt.executeUpdate();
        }
    }

    public double getSaldo(String userEmail, int appId) throws SQLException {
        String sql = "SELECT saldo FROM app_saldo WHERE user_email = ? AND app_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        }
        return 0.0;
    }

    public Map<Integer, Double> getAllSaldoByUser(String userEmail) throws SQLException {
        Map<Integer, Double> saldoMap = new HashMap<>();
        String sql = "SELECT app_id, saldo FROM app_saldo WHERE user_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    saldoMap.put(rs.getInt("app_id"), rs.getDouble("saldo"));
                }
            }
        }
        return saldoMap;
    }

    public void initializeSaldo(String userEmail, int appId) throws SQLException {
        String sql = "INSERT IGNORE INTO app_saldo (user_email, app_id, saldo) VALUES (?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            stmt.executeUpdate();
        }
    }

    public void transferSaldo(String oldUserEmail, String newUserEmail, int appId, double amount) throws SQLException {
        double currentSaldo = getSaldo(oldUserEmail, appId);
        if (currentSaldo >= amount) {
            updateSaldo(oldUserEmail, appId, currentSaldo - amount);
            double targetSaldo = getSaldo(newUserEmail, appId);
            updateSaldo(newUserEmail, appId, targetSaldo + amount);
        }
    }

    public void updateSaldo(String userEmail, int appId, double newSaldo) throws SQLException {
        String sql = "UPDATE app_saldo SET saldo = ? WHERE user_email = ? AND app_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newSaldo);
            stmt.setString(2, userEmail);
            stmt.setInt(3, appId);
            stmt.executeUpdate();
        }
    }
}
