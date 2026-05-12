package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class ForeApp implements Aplikasi {
    @Override
    public String getNama() { return "Fore Coffee"; }
    @Override
    public void buka(User user) { System.out.println("Opening Fore Coffee for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { }
}
