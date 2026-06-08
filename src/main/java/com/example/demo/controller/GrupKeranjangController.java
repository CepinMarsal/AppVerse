package com.example.demo.controller;

import com.example.demo.dao.GrupKeranjangDAO;
import com.example.demo.dao.RiwayatDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.GrupKeranjang;
import com.example.demo.model.GrupKeranjang.GrupItem;
import com.example.demo.model.GrupKeranjang.GrupMember;
import com.example.demo.model.User;
import com.example.demo.service.AppSaldoDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grup-keranjang")
public class GrupKeranjangController {

    private final GrupKeranjangDAO grupDAO = new GrupKeranjangDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AppSaldoDAO saldoDAO = new AppSaldoDAO();
    private final RiwayatDAO riwayatDAO = new RiwayatDAO();

    // Buat grup baru
    @PostMapping("/create")
    public ResponseEntity<?> createGrup(@RequestBody Map<String, Object> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        int appId = (Integer) payload.get("appId");
        GrupKeranjang grup = grupDAO.createGrup(user.getEmail(), appId);
        return ResponseEntity.ok(grup);
    }

    // Invite user lain ke grup (bisa lebih dari satu)
    @PostMapping("/{grupId}/invite")
    public ResponseEntity<?> inviteUser(@PathVariable int grupId,
                                        @RequestBody Map<String, String> payload,
                                        HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();
        if (!grup.getOwnerEmail().equals(user.getEmail()))
            return ResponseEntity.status(403).body("Hanya owner yang bisa mengundang");

        String targetEmail = payload.get("email");
        User target = userDAO.findByEmail(targetEmail);
        if (target == null) return ResponseEntity.badRequest().body("User tidak ditemukan");
        if (!target.isActive()) return ResponseEntity.badRequest().body("User tidak aktif");

        // Cek apakah sudah diundang
        List<GrupMember> members = grupDAO.findMembers(grupId);
        boolean alreadyInvited = members.stream().anyMatch(m -> m.getUserEmail().equals(targetEmail));
        if (alreadyInvited) return ResponseEntity.badRequest().body("User sudah diundang");

        grupDAO.addMember(grupId, targetEmail, "PENDING");

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Undangan terkirim ke " + targetEmail);
        resp.put("grupId", grupId);
        return ResponseEntity.ok(resp);
    }

    // Lihat undangan pending milik user yang login
    @GetMapping("/invites")
    public ResponseEntity<List<GrupKeranjang>> getPendingInvites(HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(grupDAO.findPendingInvites(user.getEmail()));
    }

    // Accept atau reject undangan
    @PostMapping("/{grupId}/respond")
    public ResponseEntity<?> respondInvite(@PathVariable int grupId,
                                           @RequestBody Map<String, String> payload,
                                           HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        String action = payload.get("action"); // "ACCEPT" atau "REJECT"
        if (!"ACCEPT".equals(action) && !"REJECT".equals(action))
            return ResponseEntity.badRequest().body("Action harus ACCEPT atau REJECT");

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();

        String newStatus = "ACCEPT".equals(action) ? "ACCEPTED" : "REJECTED";
        grupDAO.updateMemberStatus(grupId, user.getEmail(), newStatus);

        return ResponseEntity.ok("Status diperbarui: " + newStatus);
    }

    // Lihat semua grup aktif user di app tertentu
    @GetMapping("/active")
    public ResponseEntity<List<GrupKeranjang>> getActiveGrups(@RequestParam int appId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(grupDAO.findActiveByUser(user.getEmail(), appId));
    }

    // Detail grup (termasuk member dan item)
    @GetMapping("/{grupId}")
    public ResponseEntity<?> getGrupDetail(@PathVariable int grupId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();

        // Owner selalu boleh akses, member yang accepted juga boleh
        boolean isOwner = grup.getOwnerEmail().equals(user.getEmail());
        boolean isMember = grupDAO.isMemberAccepted(grupId, user.getEmail());
        if (!isOwner && !isMember)
            return ResponseEntity.status(403).body("Anda bukan member grup ini");

        grup.setMembers(grupDAO.findMembers(grupId));
        grup.setItems(grupDAO.findItems(grupId));
        return ResponseEntity.ok(grup);
    }

    // Tambah item ke grup keranjang
    @PostMapping("/{grupId}/items")
    public ResponseEntity<?> addItem(@PathVariable int grupId,
                                     @RequestBody Map<String, Object> payload,
                                     HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();
        if (!"ACTIVE".equals(grup.getStatus()))
            return ResponseEntity.badRequest().body("Grup tidak aktif");

        boolean isOwner = grup.getOwnerEmail().equals(user.getEmail());
        boolean isMember = grupDAO.isMemberAccepted(grupId, user.getEmail());
        if (!isOwner && !isMember)
            return ResponseEntity.status(403).body("Anda bukan member grup ini");

        int productId = (Integer) payload.get("productId");
        String productNama = (String) payload.get("productNama");
        double productHarga = Double.parseDouble(payload.get("productHarga").toString());
        int qty = payload.containsKey("qty") ? (Integer) payload.get("qty") : 1;

        GrupItem item = grupDAO.addItem(grupId, user.getEmail(), productId, productNama, productHarga, qty);
        return ResponseEntity.ok(item);
    }

    // Hapus item dari grup keranjang (hanya yang menambahkan)
    @DeleteMapping("/{grupId}/items/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable int grupId,
                                        @PathVariable int itemId,
                                        HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();

        boolean isOwner = grup.getOwnerEmail().equals(user.getEmail());
        boolean isMember = grupDAO.isMemberAccepted(grupId, user.getEmail());
        if (!isOwner && !isMember)
            return ResponseEntity.status(403).body("Anda bukan member grup ini");

        grupDAO.removeItem(itemId, user.getEmail());
        return ResponseEntity.ok("Item dihapus");
    }

    // Checkout grup — semua dibayar oleh owner
    @PostMapping("/{grupId}/checkout")
    public ResponseEntity<?> checkout(@PathVariable int grupId,
                                      @RequestBody(required = false) Map<String, Object> payload,
                                      HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        GrupKeranjang grup = grupDAO.findById(grupId);
        if (grup == null) return ResponseEntity.notFound().build();
        if (!grup.getOwnerEmail().equals(user.getEmail()))
            return ResponseEntity.status(403).body("Hanya owner yang bisa checkout");
        if (!"ACTIVE".equals(grup.getStatus()))
            return ResponseEntity.badRequest().body("Grup sudah di-checkout");

        List<GrupItem> items = grupDAO.findItems(grupId);
        if (items.isEmpty()) return ResponseEntity.badRequest().body("Keranjang grup kosong");

        // Hitung total semua item
        double grandTotal = 0;
        for (GrupItem item : items) {
            grandTotal += item.getProductHarga() * item.getQty();
        }

        // Validasi saldo owner
        int appId = grup.getAppId();
        String ownerEmail = grup.getOwnerEmail();
        double saldoOwner = saldoDAO.getSaldo(ownerEmail, appId);
        if (saldoOwner < grandTotal) {
            return ResponseEntity.badRequest().body(
                "Saldo Anda (Owner) tidak cukup. Butuh Rp " + grandTotal + ", saldo Rp " + saldoOwner
            );
        }

        // Proses pembayaran oleh owner
        saldoDAO.transferSaldo(ownerEmail, "system@company.com", appId, grandTotal);
        riwayatDAO.insert(ownerEmail, appId, "Group Cart Checkout (Traktir Grup #" + grupId + ")", grandTotal);

        grupDAO.closeGrup(grupId);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Checkout berhasil, Anda telah mentraktir semua item di grup ini!");
        result.put("grandTotal", grandTotal);
        return ResponseEntity.ok(result);
    }
}
