package com.example.fyptest.database;

import java.util.Date;

public class orderHistoryClass {
    private String oh_ID;
    private String oh_pro_ID;
    private String oh_cus_ID;
    private String oh_os;
    private int oh_totalQuantity;
    private String oh_checkoutDate;
    private String oh_orderedPrice;
    private String oh_shippingFee;

    public void orderHistoryClass() {}

    public orderHistoryClass(String oh_ID, String oh_pro_ID, String oh_cus_ID, String oh_os,
                             int oh_totalQuantity, String oh_checkoutDate, String oh_orderedPrice, String oh_shippingFee) {
        this.oh_ID = oh_ID;
        this.oh_pro_ID = oh_pro_ID;
        this.oh_cus_ID = oh_cus_ID;
        this.oh_os = oh_os;
        this.oh_totalQuantity = oh_totalQuantity;
        this.oh_checkoutDate = oh_checkoutDate;
        this.oh_orderedPrice = oh_orderedPrice;
        this.oh_shippingFee = oh_shippingFee;
    }

    public String getOh_ID() {
        return oh_ID;
    }

    public String getOh_pro_ID() {
        return oh_pro_ID;
    }

    public String getOh_cus_ID() {
        return oh_cus_ID;
    }

    public String getOh_os() {
        return oh_os;
    }

    public int getOh_totalQuantity() {
        return oh_totalQuantity;
    }

    public String getOh_checkoutDate() {
        return oh_checkoutDate;
    }

    public String getOh_orderedPrice() {
        return oh_orderedPrice;
    }

    public String getOh_shippingFee() {
        return oh_shippingFee;
    }
}
