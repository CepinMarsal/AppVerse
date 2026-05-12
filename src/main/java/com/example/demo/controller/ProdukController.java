package com.example.demo.controller;

import com.example.demo.dao.ProdukDAO;
import com.example.demo.model.Produk;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/produk")
public class ProdukController {

    private final ProdukDAO produkDAO = new ProdukDAO();

    @GetMapping("/{appId}")
    public ResponseEntity<List<Produk>> getProdukByApp(@PathVariable int appId) throws SQLException {
        return ResponseEntity.ok(produkDAO.findByApp(appId));
    }

    @PostMapping("/init")
    public ResponseEntity<String> initProduk() throws SQLException {
        produkDAO.initializeProduk();
        return ResponseEntity.ok("Products initialized successfully");
    }
}
