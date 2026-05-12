package com.example.demo.model;

import java.util.*;

public class User {

    private String name;
    private String email;
    private String phone;
    private String address;
    private DataBiometrik biometrik;
    private boolean userBaru = true;
    private boolean active = true;

    public User(String name, String email, DataBiometrik biometrik) {
        this.name = name;
        this.email = email;
        this.biometrik = biometrik;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public DataBiometrik getBiometrik() { return biometrik; }

    public boolean isUserBaru() {
        return userBaru;
    }

    public void setSudahTransaksi() {
        userBaru = false;
    }

    public boolean isSameFingerprint(String fp) {
        return biometrik.getFingerprint().equals(fp);
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}
