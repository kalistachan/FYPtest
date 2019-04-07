package com.example.fyptest.database;

public class Product {
    String prodID;
    String prodName;
    String prodPrice;

    public Product() {

    }

    public Product(String prodID, String prodName, String prodPrice) {
        this.prodID = prodID;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
    }

    public String getProdID() {
        return prodID;
    }

    public String getProdName() {
        return prodName;
    }

    public String getProdPrice() {
        return prodPrice;
    }

}
