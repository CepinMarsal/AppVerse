package com.example.demo.aplikasi;

import com.example.demo.model.User;

public interface Aplikasi {
    String getNama();
    void buka(User user);
    void transferSaldo(User oldUser, User newUser);
}
