package com.example.fyptest.database;

public class creditCardClass {

    private String cc_ID;
    private String cc_Num;
    private String cc_ExpiryDate;
    private String cc_CVNum;
    private String cc_cus_ID;

    public creditCardClass() {

    }

    public creditCardClass(String cc_ID, String cc_Num, String cc_ExpiryDate, String cc_CVNum, String cc_cus_ID) {
        this.cc_ID = cc_ID;
        this.cc_Num = cc_Num;
        this.cc_ExpiryDate = cc_ExpiryDate;
        this.cc_CVNum = cc_CVNum;
        this.cc_cus_ID = cc_cus_ID;
    }

    public String getCc_ID() {
        return cc_ID;
    }

    public String getCc_Num() {
        return cc_Num;
    }

    public String getCc_ExpiryDate() {
        return cc_ExpiryDate;
    }

    public String getCc_CVNum() {
        return cc_CVNum;
    }

    public String getCc_cus_ID() {
        return cc_cus_ID;
    }
}
