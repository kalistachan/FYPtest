package com.example.fyptest.database;

import java.util.Date;

public class groupDetailClass {
    private String gd_ID;
    private String gd_joinDate;
    private int gd_qty;
    private String gd_pg_pro_ID;
    private String gd_cus_ID;

    public void groupDetailClass() {

    }

    public groupDetailClass(String gd_ID, String gd_joinDate, int gd_qty, String gd_pg_pro_ID, String gd_cus_ID) {
        this.gd_ID = gd_ID;
        this.gd_joinDate = gd_joinDate;
        this.gd_qty = gd_qty;
        this.gd_pg_pro_ID = gd_pg_pro_ID;
        this.gd_cus_ID = gd_cus_ID;
    }

    public String getGd_ID() {
        return gd_ID;
    }

    public String getGd_joinDate() {
        return gd_joinDate;
    }

    public int getGd_qty() {
        return gd_qty;
    }

    public String getGd_pg_pro_ID() {
        return gd_pg_pro_ID;
    }

    public String getGd_cus_ID() {
        return gd_cus_ID;
    }
}


