package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class InstagramApp implements Aplikasi {
    @Override
    public String getNama() { return "Instagram"; }
    @Override
    public void buka(User user) { System.out.println("Opening Instagram for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { /* Logic here */ }
}
