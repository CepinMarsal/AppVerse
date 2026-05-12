package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import com.example.demo.model.Produk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdukDAO {

    public void insert(String nama, double harga, int appId) throws SQLException {
        String sql = "INSERT INTO produk (nama, harga, app_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nama);
            stmt.setDouble(2, harga);
            stmt.setInt(3, appId);
            stmt.executeUpdate();
        }
    }

    public List<Produk> findByApp(int appId) throws SQLException {
        List<Produk> produkList = new ArrayList<>();
        String sql = "SELECT nama, harga FROM produk WHERE app_id = ? ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produk p = new Produk(rs.getString("nama"), rs.getDouble("harga"));
                    produkList.add(p);
                }
            }
        }
        return produkList;
    }

    public void deleteByApp(int appId) throws SQLException {
        String sql = "DELETE FROM produk WHERE app_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, appId);
            stmt.executeUpdate();
        }
    }

    public void initializeProduk() throws SQLException {
        // Daftar ID Aplikasi: Fore=1, Gojek=2, Shopee=3, Tokopedia=4, JanjiJiwa=5
        for (int i = 1; i <= 5; i++) deleteByApp(i);

        insert("Sepatu", 150000, 3);
        insert("Tas", 100000, 3);
        insert("Jaket", 200000, 3);

        insert("Laptop", 7000000, 4);
        insert("Headset", 250000, 4);
        insert("Keyboard Mechanical", 500000, 4);

        insert("Kopi Susu", 20000, 1);
        insert("Americano", 18000, 1);

        insert("Es Kopi Susu Jiwa", 18000, 5);
        insert("Jiwa Latte", 22000, 5);
        insert("Brown Sugar Milk", 20000, 5);
    }
}
