package com.example.demo.controller;

import com.example.demo.dao.AppDAO;
import com.example.demo.dao.VoucherDAO;
import com.example.demo.model.App;
import com.example.demo.model.User;
import com.example.demo.model.Voucher;
import com.example.demo.service.AppSaldoDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/apps")
public class AppController {

    private final AppDAO appDAO = new AppDAO();
    private final AppSaldoDAO saldoDAO = new AppSaldoDAO();
    private final VoucherDAO voucherDAO = new VoucherDAO();

    @GetMapping
    public ResponseEntity<List<App>> getAllApps() throws SQLException {
        appDAO.setupDatabase(); // Ensure schema and apps are ready
        return ResponseEntity.ok(appDAO.findAll());
    }

    @PostMapping("/{appId}/claim")
    public ResponseEntity<?> claimVouchers(@PathVariable int appId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        
        voucherDAO.initializeVouchersForNewUser(user.getEmail(), user.getBiometrik().getFingerprint(), appId);
        return ResponseEntity.ok("Voucher claimed successfully (if eligible)");
    }

    @GetMapping("/saldo")
    public ResponseEntity<Map<Integer, Double>> getMySaldo(HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(saldoDAO.getAllSaldoByUser(user.getEmail()));
    }

    @PostMapping("/{appId}/init")
    public ResponseEntity<?> initApp(@PathVariable int appId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        
        voucherDAO.initializeVouchersForNewUser(user.getEmail(), user.getBiometrik().getFingerprint(), appId);
        return ResponseEntity.ok("App initialized");
    }

    @GetMapping("/{appId}/vouchers")
    public ResponseEntity<List<Voucher>> getMyVouchers(@PathVariable int appId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        
        List<Voucher> list = voucherDAO.findByUserAndApp(user.getEmail(), appId);
        if (list.isEmpty()) {
            // Self-repair: Try to initialize if missing
            voucherDAO.initializeVouchersForNewUser(user.getEmail(), user.getBiometrik().getFingerprint(), appId);
            list = voucherDAO.findByUserAndApp(user.getEmail(), appId);
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferSaldo(@RequestBody Map<String, Object> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        String targetEmail = (String) payload.get("targetEmail");
        int appId = (Integer) payload.get("appId");
        double amount = Double.parseDouble(payload.get("amount").toString());
        String activity = payload.containsKey("activity") ? payload.get("activity").toString() : "Transfer";

        boolean success = saldoDAO.transferSaldo(user.getEmail(), targetEmail, appId, amount);
        if (!success) {
            return ResponseEntity.badRequest().body("Saldo Anda tidak cukup untuk melakukan transaksi ini.");
        }
        new com.example.demo.dao.RiwayatDAO().insert(user.getEmail(), appId, activity, amount);
        
        return ResponseEntity.ok("Transfer successful");
    }

    @GetMapping("/{appId}/products")
    public ResponseEntity<List<Map<String, Object>>> getProducts(@PathVariable int appId) throws SQLException {
        List<Map<String, Object>> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE app_id = ?";
        try (java.sql.Connection conn = com.example.demo.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("id", rs.getInt("id"));
                    p.put("nama", rs.getString("nama"));
                    p.put("harga", rs.getDouble("harga"));
                    p.put("kategori", rs.getString("kategori"));
                    products.add(p);
                }
            }
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<Map<String, Object>>> getPosts() throws SQLException {
        List<Map<String, Object>> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY created_at DESC";
        try (java.sql.Connection conn = com.example.demo.config.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> p = new HashMap<>();
                p.put("userEmail", rs.getString("user_email"));
                p.put("content", rs.getString("content"));
                p.put("createdAt", rs.getTimestamp("created_at"));
                posts.add(p);
            }
        }
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody Map<String, String> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        String content = payload.get("content");
        String sql = "INSERT INTO posts (user_email, content) VALUES (?, ?)";
        try (java.sql.Connection conn = com.example.demo.config.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, content);
            stmt.executeUpdate();
        }
        return ResponseEntity.ok("Post created");
    }

    @PostMapping("/vouchers/{id}/use")
    public ResponseEntity<?> useVoucher(@PathVariable int id) throws SQLException {
        voucherDAO.useVoucher(id);
        return ResponseEntity.ok("Voucher marked as used");
    }
}
