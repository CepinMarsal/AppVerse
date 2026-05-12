package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import com.example.demo.model.User;
import com.example.demo.model.DataBiometrik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (nama, email, fingerprint, active, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getBiometrik().getFingerprint());
            stmt.setBoolean(4, user.isActive());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.executeUpdate();

            return user;
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = mapRow(rs);
                users.add(user);
            }
        }
        return users;
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public User findByFingerprint(String fingerprint) throws SQLException {
        String sql = "SELECT * FROM users WHERE fingerprint = ? AND active = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fingerprint);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean updateProfile(String email, String name, String phone, String address) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE users SET email = email ");
        if (name != null) sql.append(", nama = ? ");
        if (phone != null) sql.append(", phone = ? ");
        if (address != null) sql.append(", address = ? ");
        sql.append(" WHERE email = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (name != null) stmt.setString(idx++, name);
            if (phone != null) stmt.setString(idx++, phone);
            if (address != null) stmt.setString(idx++, address);
            stmt.setString(idx, email);
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deactivateUser(String email) throws SQLException {
        String sql = "UPDATE users SET active = 0 WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String name = rs.getString("nama");
        String email = rs.getString("email");
        String fingerprint = rs.getString("fingerprint");
        boolean active = rs.getBoolean("active");
        String phone = rs.getString("phone");
        String address = rs.getString("address");

        User user = new User(name, email, new DataBiometrik(fingerprint));
        user.setPhone(phone);
        user.setAddress(address);
        if (!active) {
            user.deactivate();
        }
        return user;
    }
}
