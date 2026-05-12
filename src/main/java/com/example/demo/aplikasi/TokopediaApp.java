package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class TokopediaApp implements Aplikasi {
    @Override
    public String getNama() { return "Tokopedia"; }
    @Override
    public void buka(User user) { System.out.println("Opening Tokopedia for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { }
}
