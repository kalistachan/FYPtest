package com.example.fyptest.database;

public class sellerInfoClass {
    private String s_ID;
    private String s_Name;
    private String s_userType;

    public void sellerInfoClass() {

    }

    public sellerInfoClass(String s_ID, String s_Name, String s_userType) {
        this.s_ID = s_ID;
        this.s_Name = s_Name;
        this.s_userType = s_userType;
    }

    public String getS_ID() {
        return s_ID;
    }

    public String getS_Name() {
        return s_Name;
    }

    public String getS_userType() {
        return s_userType;
    }
}
