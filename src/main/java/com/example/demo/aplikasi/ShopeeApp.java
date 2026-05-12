package com.example.demo.aplikasi;

import com.example.demo.model.User;

public class ShopeeApp implements Aplikasi {
    @Override
    public String getNama() { return "Shopee"; }
    @Override
    public void buka(User user) { System.out.println("Opening Shopee for " + user.getName()); }
    @Override
    public void transferSaldo(User oldUser, User newUser) { }
}
