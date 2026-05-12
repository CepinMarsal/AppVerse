package com.example.demo.model;

public class DataBiometrik {
    private String fingerprint;

    public DataBiometrik(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public boolean verify(String input) {
        return fingerprint.equals(input);
    }
}