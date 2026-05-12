package com.example.demo.service;

import com.example.demo.model.User;

import java.util.List;

public class SistemVerifikasi {

    public boolean checkAuthenticity(User user, String fingerprint) {
        return user.getBiometrik().verify(fingerprint);
    }

    public boolean detectDuplicate(String fingerprint, List<User> users) {
        int count = 0;
        for (User u : users) {
            if (u.getBiometrik().getFingerprint().equals(fingerprint)) {
                count++;
            }
        }
        return count > 1;
    }
}