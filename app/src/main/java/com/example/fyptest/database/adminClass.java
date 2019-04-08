package com.example.fyptest.database;

public class adminClass {
    private String ad_ID;
    private String ad_email;
    private String ad_contactNum;
    private String ad_firstName;
    private String ad_lastName;
    private String ad_password;
    private String ad_gender;
    private String ad_ut_ID;

    public void adminClass() {

    }

    public adminClass(String ad_ID, String ad_email, String ad_contactNum,
                      String ad_firstName, String ad_lastName, String ad_password,
                      String ad_gender, String ad_ut_ID) {
        this.ad_ID = ad_ID;
        this.ad_email = ad_email;
        this.ad_contactNum = ad_contactNum;
        this.ad_firstName = ad_firstName;
        this.ad_lastName = ad_lastName;
        this.ad_password = ad_password;
        this.ad_gender = ad_gender;
        this.ad_ut_ID = ad_ut_ID;
    }

    public String getAd_ID() {
        return ad_ID;
    }

    public String getAd_email() {
        return ad_email;
    }

    public String getAd_contactNum() {
        return ad_contactNum;
    }

    public String getAd_firstName() {
        return ad_firstName;
    }

    public String getAd_lastName() {
        return ad_lastName;
    }

    public String getAd_password() {
        return ad_password;
    }

    public String getAd_gender() {
        return ad_gender;
    }

    public String getAd_ut_ID() {
        return ad_ut_ID;
    }
}
