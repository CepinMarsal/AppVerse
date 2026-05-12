package com.example.demo.service;

import com.example.demo.model.User;

import java.util.List;

public class AuthService {
    public User login(String email, String fingerprint, List<User> users, SistemVerifikasi verifikasi) {
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                if (!u.isActive()) {
                    System.out.println("Akun Anda telah dinonaktifkan!");
                    return null;
                }
                if (verifikasi.checkAuthenticity(u, fingerprint)) {
                    return u;
                } else {
                    System.out.println("Fingerprint salah!");
                    return null;
                }
            }
        }
        System.out.println("Email tidak ditemukan!");
        return null;
    }
}