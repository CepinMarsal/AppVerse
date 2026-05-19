package com.example.demo.model;

import java.util.List;

public class GrupKeranjang {
    private int id;
    private String kodeGrup;
    private String ownerEmail;
    private int appId;
    private String status; // ACTIVE, CHECKED_OUT

    private List<GrupMember> members;
    private List<GrupItem> items;

    public GrupKeranjang() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getKodeGrup() { return kodeGrup; }
    public void setKodeGrup(String kodeGrup) { this.kodeGrup = kodeGrup; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public int getAppId() { return appId; }
    public void setAppId(int appId) { this.appId = appId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<GrupMember> getMembers() { return members; }
    public void setMembers(List<GrupMember> members) { this.members = members; }

    public List<GrupItem> getItems() { return items; }
    public void setItems(List<GrupItem> items) { this.items = items; }

    // ---- Inner classes ----

    public static class GrupMember {
        private int id;
        private int grupId;
        private String userEmail;
        private String status; // PENDING, ACCEPTED, REJECTED

        public GrupMember() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getGrupId() { return grupId; }
        public void setGrupId(int grupId) { this.grupId = grupId; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class GrupItem {
        private int id;
        private int grupId;
        private String addedByEmail;
        private int productId;
        private String productNama;
        private double productHarga;
        private int qty;

        public GrupItem() {}

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public int getGrupId() { return grupId; }
        public void setGrupId(int grupId) { this.grupId = grupId; }

        public String getAddedByEmail() { return addedByEmail; }
        public void setAddedByEmail(String addedByEmail) { this.addedByEmail = addedByEmail; }

        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }

        public String getProductNama() { return productNama; }
        public void setProductNama(String productNama) { this.productNama = productNama; }

        public double getProductHarga() { return productHarga; }
        public void setProductHarga(double productHarga) { this.productHarga = productHarga; }

        public int getQty() { return qty; }
        public void setQty(int qty) { this.qty = qty; }
    }
}
