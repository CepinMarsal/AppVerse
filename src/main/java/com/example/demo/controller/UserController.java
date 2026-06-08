package com.example.demo.controller;

import com.example.demo.dao.RiwayatDAO;
import com.example.demo.dao.AppProfileDAO;
import com.example.demo.model.User;
import com.example.demo.service.AppSaldoDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final RiwayatDAO riwayatDAO = new RiwayatDAO();
    private final AppSaldoDAO saldoDAO = new AppSaldoDAO();

    @GetMapping("/riwayat")
    public ResponseEntity<List<String>> getRiwayat(HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(riwayatDAO.findByUser(user.getEmail()));
    }

    @PostMapping("/init-saldo")
    public ResponseEntity<?> initSaldo(@RequestBody Map<String, Object> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        int appId = (Integer) payload.get("appId");
        double amount = Double.parseDouble(payload.get("amount").toString());
        
        saldoDAO.insertOrUpdate(user.getEmail(), appId, amount);
        riwayatDAO.insert(user.getEmail(), appId, "Top Up Saldo", amount);
        
        return ResponseEntity.ok("Saldo updated");
    }

    private final AppProfileDAO profileDAO = new AppProfileDAO();

    @GetMapping("/profile/{appId}")
    public ResponseEntity<Map<String, String>> getAppProfile(@PathVariable int appId, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(profileDAO.getProfile(user.getEmail(), appId));
    }

    @PostMapping("/profile/{appId}/update")
    public ResponseEntity<?> updateAppProfile(@PathVariable int appId, @RequestBody Map<String, String> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        String phone = payload.get("phone");
        String address = payload.get("address");
        profileDAO.saveProfile(user.getEmail(), appId, phone, address);
        
        return ResponseEntity.ok("Profile updated");
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateGlobalProfile(@RequestBody Map<String, String> payload, HttpSession session) throws SQLException {
        User user = SessionUtil.getUser(session);
        if (user == null) return ResponseEntity.status(401).build();

        String newName = payload.get("name");
        com.example.demo.dao.UserDAO userDAO = new com.example.demo.dao.UserDAO();
        userDAO.updateProfile(user.getEmail(), newName, null, null);
        
        User updatedUser = userDAO.findByEmail(user.getEmail());
        session.setAttribute("user", updatedUser);
        session.setAttribute("user_" + updatedUser.getEmail(), updatedUser);
        return ResponseEntity.ok(updatedUser);
    }
}
