package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

public class Voucher {
    private static final Set<String> usedFingerprint = new HashSet<>();

    private int id;
    private String nama;
    private String tipe;
    private double nilai;
    private double min;

    private String expiryDate;
    private String fingerprint;

    public Voucher(String nama, String tipe, double nilai, double min) {
        this.nama = nama;
        this.tipe = tipe;
        this.nilai = nilai;
        this.min = min;
    }

    public Voucher(String nama, String tipe, double nilai, double min, String expiryDate, String fingerprint) {
        this.nama = nama;
        this.tipe = tipe;
        this.nilai = nilai;
        this.min = min;
        this.expiryDate = expiryDate;
        this.fingerprint = fingerprint;
    }

    public String getNama() {
        return nama;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipe() {
        return tipe;
    }

    public double getNilai() {
        return nilai;
    }

    public double getMin() {
        return min;
    }

    public boolean isValid(double value) {
        return value >= min;
    }

    public double apply(double value) {
        if (tipe.equals("PERSEN")) {
            return value * (1 - nilai);
        } 
        else if (tipe.equals("NOMINAL")) {
            return value - nilai;
        }
        return value;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public void show() {
        System.out.println(nama + " (min: " + min + ")");
    }

    // ================= FITUR VOUCHER SYSTEM =================
    public static boolean claimVoucher(User user) {
        String fp = user.getBiometrik().getFingerprint();

        if (usedFingerprint.contains(fp)) {
            System.out.println("Voucher sudah digunakan sebelumnya!");
            return false;
        }

        usedFingerprint.add(fp);
        System.out.println("Voucher berhasil diklaim!");
        return true;
    }
}