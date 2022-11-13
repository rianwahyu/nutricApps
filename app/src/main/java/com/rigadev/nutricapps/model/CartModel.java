package com.rigadev.nutricapps.model;

import java.io.Serializable;

public class CartModel implements Serializable  {

    String idCart, idMenu, namaMenu, fotoMenu, jumlah, harga, totalHarga;

    public CartModel(String idCart, String idMenu, String namaMenu, String fotoMenu,
                     String jumlah, String harga, String totalHarga) {
        this.idCart = idCart;
        this.idMenu = idMenu;
        this.namaMenu = namaMenu;
        this.fotoMenu = fotoMenu;
        this.jumlah = jumlah;
        this.harga = harga;
        this.totalHarga = totalHarga;
    }

    public String getIdCart() {
        return idCart;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public String getNamaMenu() {
        return namaMenu;
    }

    public String getFotoMenu() {
        return fotoMenu;
    }

    public String getJumlah() {
        return jumlah;
    }

    public String getHarga() {
        return harga;
    }

    public String getTotalHarga() {
        return totalHarga;
    }

}
