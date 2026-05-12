package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.aplikasi.*;
import java.util.*;

public class AppManager {

    private List<Aplikasi> apps = new ArrayList<>();

    public AppManager() {
        apps.add(new InstagramApp());
        apps.add(new ForeApp());
        apps.add(new JanjiJiwaApp());
        apps.add(new TokopediaApp());
        apps.add(new GojekApp());
        apps.add(new ShopeeApp());
    }

    public void showApps() {
        for (int i = 0; i < apps.size(); i++) {
            System.out.println((i + 1) + ". " + apps.get(i).getNama());
        }
    }

    public void openApp(int pilihan, User user) {
        apps.get(pilihan - 1).buka(user);
    }

    public Aplikasi getApp(int pilihan) {
        return apps.get(pilihan - 1);
    }

    public void transferAllSaldo(User oldUser, User newUser) {
        for (Aplikasi app : apps) {
            app.transferSaldo(oldUser, newUser);
        }
    }
}