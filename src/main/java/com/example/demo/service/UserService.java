package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.DataBiometrik;

public class UserService {

    public User register(String name, String email, String fingerprint) {
        DataBiometrik bio = new DataBiometrik(fingerprint);
        return new User(name, email, bio);
    }

    public void transferData(String oldEmail, String newEmail) throws java.sql.SQLException {
        com.example.demo.service.AppSaldoDAO saldoDAO = new com.example.demo.service.AppSaldoDAO();
        com.example.demo.dao.VoucherDAO voucherDAO = new com.example.demo.dao.VoucherDAO();
        com.example.demo.dao.RiwayatDAO riwayatDAO = new com.example.demo.dao.RiwayatDAO();

        // Transfer Saldo
        java.util.Map<Integer, Double> saldos = saldoDAO.getAllSaldoByUser(oldEmail);
        for (java.util.Map.Entry<Integer, Double> entry : saldos.entrySet()) {
            saldoDAO.insertOrUpdate(newEmail, entry.getKey(), entry.getValue());
            // Clear old saldo
            saldoDAO.updateSaldo(oldEmail, entry.getKey(), 0);
        }

        // Transfer Vouchers
        voucherDAO.transferVouchers(oldEmail, newEmail);

        // Transfer History
        riwayatDAO.transferRiwayat(oldEmail, newEmail);
    }
}