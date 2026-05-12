package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class JanjiJiwaApp implements Aplikasi {
    @Override
    public String getNama() { return "Janji Jiwa"; }
    @Override
    public void buka(User user) { System.out.println("Opening Janji Jiwa for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { }
}
