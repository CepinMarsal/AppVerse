package com.example.demo.dao;

import com.example.demo.config.DatabaseConnection;
import com.example.demo.model.GrupKeranjang;
import com.example.demo.model.GrupKeranjang.GrupItem;
import com.example.demo.model.GrupKeranjang.GrupMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrupKeranjangDAO {

    // ===================== GRUP =====================

    public GrupKeranjang createGrup(String ownerEmail, int appId) throws SQLException {
        String kode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String sql = "INSERT INTO grup_keranjang (kode_grup, owner_email, app_id, status) VALUES (?, ?, ?, 'ACTIVE')";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int grupId;
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, kode);
                    stmt.setString(2, ownerEmail);
                    stmt.setInt(3, appId);
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Gagal mendapatkan generated key");
                        grupId = rs.getInt(1);
                    }
                }
                // Auto-add owner sebagai ACCEPTED member dalam koneksi yang sama
                String memberSql = "INSERT IGNORE INTO grup_keranjang_member (grup_id, user_email, status) VALUES (?, ?, 'ACCEPTED')";
                try (PreparedStatement mStmt = conn.prepareStatement(memberSql)) {
                    mStmt.setInt(1, grupId);
                    mStmt.setString(2, ownerEmail);
                    mStmt.executeUpdate();
                }
                conn.commit();

                GrupKeranjang grup = new GrupKeranjang();
                grup.setId(grupId);
                grup.setKodeGrup(kode);
                grup.setOwnerEmail(ownerEmail);
                grup.setAppId(appId);
                grup.setStatus("ACTIVE");
                return grup;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public GrupKeranjang findById(int grupId) throws SQLException {
        String sql = "SELECT * FROM grup_keranjang WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapGrup(rs);
            }
        }
        return null;
    }

    public GrupKeranjang findByKode(String kode) throws SQLException {
        String sql = "SELECT * FROM grup_keranjang WHERE kode_grup = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapGrup(rs);
            }
        }
        return null;
    }

    // Semua grup aktif milik user (sebagai owner atau member accepted)
    public List<GrupKeranjang> findActiveByUser(String userEmail, int appId) throws SQLException {
        String sql = "SELECT DISTINCT g.* FROM grup_keranjang g " +
                     "JOIN grup_keranjang_member m ON g.id = m.grup_id " +
                     "WHERE m.user_email = ? AND m.status = 'ACCEPTED' AND g.app_id = ? AND g.status = 'ACTIVE'";
        List<GrupKeranjang> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            stmt.setInt(2, appId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapGrup(rs));
            }
        }
        return list;
    }

    // Undangan pending untuk user
    public List<GrupKeranjang> findPendingInvites(String userEmail) throws SQLException {
        String sql = "SELECT g.* FROM grup_keranjang g " +
                     "JOIN grup_keranjang_member m ON g.id = m.grup_id " +
                     "WHERE m.user_email = ? AND m.status = 'PENDING'";
        List<GrupKeranjang> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapGrup(rs));
            }
        }
        return list;
    }

    // ===================== MEMBER =====================

    public void addMember(int grupId, String userEmail, String status) throws SQLException {
        String sql = "INSERT IGNORE INTO grup_keranjang_member (grup_id, user_email, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            stmt.setString(2, userEmail);
            stmt.setString(3, status);
            stmt.executeUpdate();
        }
    }

    public void updateMemberStatus(int grupId, String userEmail, String status) throws SQLException {
        String sql = "UPDATE grup_keranjang_member SET status = ? WHERE grup_id = ? AND user_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, grupId);
            stmt.setString(3, userEmail);
            stmt.executeUpdate();
        }
    }

    public List<GrupMember> findMembers(int grupId) throws SQLException {
        String sql = "SELECT * FROM grup_keranjang_member WHERE grup_id = ?";
        List<GrupMember> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GrupMember m = new GrupMember();
                    m.setId(rs.getInt("id"));
                    m.setGrupId(rs.getInt("grup_id"));
                    m.setUserEmail(rs.getString("user_email"));
                    m.setStatus(rs.getString("status"));
                    list.add(m);
                }
            }
        }
        return list;
    }

    public boolean isMemberAccepted(int grupId, String userEmail) throws SQLException {
        String sql = "SELECT 1 FROM grup_keranjang_member WHERE grup_id = ? AND user_email = ? AND status = 'ACCEPTED'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            stmt.setString(2, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ===================== ITEM =====================

    public GrupItem addItem(int grupId, String addedByEmail, int productId, String productNama, double productHarga, int qty) throws SQLException {
        // Jika produk sudah ada dari user yang sama, tambah qty
        String checkSql = "SELECT id, qty FROM grup_keranjang_item WHERE grup_id = ? AND added_by_email = ? AND product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, grupId);
            check.setString(2, addedByEmail);
            check.setInt(3, productId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    int newQty = rs.getInt("qty") + qty;
                    int existingId = rs.getInt("id");
                    String upd = "UPDATE grup_keranjang_item SET qty = ? WHERE id = ?";
                    try (PreparedStatement updStmt = conn.prepareStatement(upd)) {
                        updStmt.setInt(1, newQty);
                        updStmt.setInt(2, existingId);
                        updStmt.executeUpdate();
                    }
                    GrupItem item = new GrupItem();
                    item.setId(existingId);
                    item.setGrupId(grupId);
                    item.setAddedByEmail(addedByEmail);
                    item.setProductId(productId);
                    item.setProductNama(productNama);
                    item.setProductHarga(productHarga);
                    item.setQty(newQty);
                    return item;
                }
            }
        }

        String sql = "INSERT INTO grup_keranjang_item (grup_id, added_by_email, product_id, product_nama, product_harga, qty) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, grupId);
            stmt.setString(2, addedByEmail);
            stmt.setInt(3, productId);
            stmt.setString(4, productNama);
            stmt.setDouble(5, productHarga);
            stmt.setInt(6, qty);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    GrupItem item = new GrupItem();
                    item.setId(rs.getInt(1));
                    item.setGrupId(grupId);
                    item.setAddedByEmail(addedByEmail);
                    item.setProductId(productId);
                    item.setProductNama(productNama);
                    item.setProductHarga(productHarga);
                    item.setQty(qty);
                    return item;
                }
            }
        }
        return null;
    }

    public void removeItem(int itemId, String requestingEmail) throws SQLException {
        // Hanya yang menambahkan item bisa menghapus
        String sql = "DELETE FROM grup_keranjang_item WHERE id = ? AND added_by_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.setString(2, requestingEmail);
            stmt.executeUpdate();
        }
    }

    public List<GrupItem> findItems(int grupId) throws SQLException {
        String sql = "SELECT * FROM grup_keranjang_item WHERE grup_id = ?";
        List<GrupItem> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GrupItem item = new GrupItem();
                    item.setId(rs.getInt("id"));
                    item.setGrupId(rs.getInt("grup_id"));
                    item.setAddedByEmail(rs.getString("added_by_email"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductNama(rs.getString("product_nama"));
                    item.setProductHarga(rs.getDouble("product_harga"));
                    item.setQty(rs.getInt("qty"));
                    list.add(item);
                }
            }
        }
        return list;
    }

    public void deleteAllItems(int grupId) throws SQLException {
        String sql = "DELETE FROM grup_keranjang_item WHERE grup_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            stmt.executeUpdate();
        }
    }

    public void closeGrup(int grupId) throws SQLException {
        String sql = "UPDATE grup_keranjang SET status = 'CHECKED_OUT' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grupId);
            stmt.executeUpdate();
        }
    }

    // ===================== HELPER =====================

    private GrupKeranjang mapGrup(ResultSet rs) throws SQLException {
        GrupKeranjang g = new GrupKeranjang();
        g.setId(rs.getInt("id"));
        g.setKodeGrup(rs.getString("kode_grup"));
        g.setOwnerEmail(rs.getString("owner_email"));
        g.setAppId(rs.getInt("app_id"));
        g.setStatus(rs.getString("status"));
        return g;
    }
}
