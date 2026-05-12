package com.example.demo.controller;

import com.example.demo.dao.UserDAO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.SistemVerifikasi;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final SistemVerifikasi verifikasi = new SistemVerifikasi();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) throws SQLException {
        String email = payload.get("email");
        String fingerprint = payload.get("fingerprint");

        User user = authService.login(email, fingerprint, userDAO.findAll(), verifikasi);
        if (user != null) {
            session.setAttribute("user", user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(401).body("Login failed: Invalid email or fingerprint");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) throws SQLException {
        String name = payload.get("name");
        String email = payload.get("email");
        String fingerprint = payload.get("fingerprint");

        if (userDAO.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Fingerprint Check
        User existingUser = userDAO.findByFingerprint(fingerprint);
        if (existingUser != null) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "CONFLICT");
            resp.put("message", "Fingerprint already registered to: " + existingUser.getEmail());
            resp.put("oldEmail", existingUser.getEmail());
            return ResponseEntity.status(409).body(resp);
        }

        User newUser = userService.register(name, email, fingerprint);
        userDAO.insert(newUser);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/register-confirm")
    public ResponseEntity<?> registerConfirm(@RequestBody Map<String, String> payload) throws SQLException {
        String name = payload.get("name");
        String email = payload.get("email");
        String fingerprint = payload.get("fingerprint");
        String oldEmail = payload.get("oldEmail");
        String action = payload.get("action"); // "NEW" or "USE_OLD"

        if ("USE_OLD".equals(action)) {
            User oldUser = userDAO.findByEmail(oldEmail);
            return ResponseEntity.ok(oldUser);
        } else {
            // NEW account: Deactivate old, transfer data
            userDAO.deactivateUser(oldEmail);
            User newUser = userService.register(name, email, fingerprint);
            userDAO.insert(newUser);
            userService.transferData(oldEmail, email);
            return ResponseEntity.ok(newUser);
        }
    }

    @GetMapping("/logout")
    public void logout(HttpSession session, jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        session.invalidate();
        response.sendRedirect("/login");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }
}
