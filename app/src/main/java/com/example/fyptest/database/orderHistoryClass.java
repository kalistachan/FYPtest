package com.example.fyptest.database;

import java.util.Date;

public class orderHistoryClass {
    private String oh_ID;
    private String oh_co_ID;
    private String oh_cus_ID;
    private String oh_os_ID;
    private int oh_totalQuantity;
    private float oh_totalPrice;
    private Date oh_checkoutDate;

    public void orderHistoryClass() {

    }

    public orderHistoryClass(String oh_ID, String oh_co_ID, String oh_cus_ID,
                             String oh_os_ID, int oh_totalQuantity,
                             float oh_totalPrice, Date oh_checkoutDate) {
        this.oh_ID = oh_ID;
        this.oh_co_ID = oh_co_ID;
        this.oh_cus_ID = oh_cus_ID;
        this.oh_os_ID = oh_os_ID;
        this.oh_totalQuantity = oh_totalQuantity;
        this.oh_totalPrice = oh_totalPrice;
        this.oh_checkoutDate = oh_checkoutDate;
    }

    public String getOh_ID() {
        return oh_ID;
    }

    public String getOh_co_ID() {
        return oh_co_ID;
    }

    public String getOh_cus_ID() {
        return oh_cus_ID;
    }

    public String getOh_os_ID() {
        return oh_os_ID;
    }

    public int getOh_totalQuantity() {
        return oh_totalQuantity;
    }

    public float getOh_totalPrice() {
        return oh_totalPrice;
    }

    public Date getOh_checkoutDate() {
        return oh_checkoutDate;
    }
}
