package com.example.fyptest.database;

public class productType {
    private String pt_ID;
    private String pt_Name;

    public void productType() {

    }

    public productType(String pt_ID, String pt_Name) {
        this.pt_ID = pt_ID;
        this.pt_Name = pt_Name;
    }

    public String getPt_ID() {
        return pt_ID;
    }

    public String getPt_Name() {
        return pt_Name;
    }
}
