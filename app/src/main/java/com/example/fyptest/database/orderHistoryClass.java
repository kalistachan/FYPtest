package com.example.fyptest.database;

import java.util.Date;

public class orderHistoryClass {
    private String oh_ID;
    private String oh_pro_ID;
    private String oh_cus_ID;
    private String oh_os_ID;
    private int oh_totalQuantity;
    private Date oh_checkoutDate;
    private String oh_orderedPrice;

    public void orderHistoryClass() {}

    public orderHistoryClass(String oh_ID, String oh_pro_ID, String oh_cus_ID,
                             String oh_os_ID, int oh_totalQuantity, Date oh_checkoutDate, String oh_orderedPrice) {
        this.oh_ID = oh_ID;
        this.oh_pro_ID = oh_pro_ID;
        this.oh_cus_ID = oh_cus_ID;
        this.oh_os_ID = oh_os_ID;
        this.oh_totalQuantity = oh_totalQuantity;
        this.oh_checkoutDate = oh_checkoutDate;
        this.oh_orderedPrice = oh_orderedPrice;
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

    public String getOh_os_ID() {
        return oh_os_ID;
    }

    public int getOh_totalQuantity() {
        return oh_totalQuantity;
    }

    public Date getOh_checkoutDate() {
        return oh_checkoutDate;
    }

    public String getOh_orderedPrice() {
        return oh_orderedPrice;
    }
}
