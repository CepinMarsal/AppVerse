package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class GojekApp implements Aplikasi {
    @Override
    public String getNama() { return "Gojek"; }
    @Override
    public void buka(User user) { System.out.println("Opening Gojek for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { }
}
