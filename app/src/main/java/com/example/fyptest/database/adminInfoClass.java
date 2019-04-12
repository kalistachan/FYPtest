package com.example.fyptest.database;

public class adminInfoClass {
    private String ad_ID;
    private String ad_fName;
    private String ad_lName;
    private String ad_userType;

    public void adminInfoClass() {

    }

    public adminInfoClass(String ad_ID, String ad_fName, String ad_lName, String ad_userType) {
        this.ad_ID = ad_ID;
        this.ad_fName = ad_fName;
        this.ad_lName = ad_lName;
        this.ad_userType = ad_userType;
    }

    public String getAd_ID() {
        return ad_ID;
    }

    public String getAd_fName() {
        return ad_fName;
    }

    public String getAd_lName() {
        return ad_lName;
    }

    public String getAd_userType() {
        return ad_userType;
    }
}
