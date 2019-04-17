package com.example.fyptest.database;

public class watchlistClass {
    private String wl_cus_ID;
    private String wl_pro_ID;

    public watchlistClass() {
    }

    public watchlistClass(String wl_cus_ID, String wl_pro_ID) {
        this.wl_cus_ID = wl_cus_ID;
        this.wl_pro_ID = wl_pro_ID;
    }

    public String getWl_cus_ID() {
        return wl_cus_ID;
    }

    public String getWl_pro_ID() {
        return wl_pro_ID;
    }
}
