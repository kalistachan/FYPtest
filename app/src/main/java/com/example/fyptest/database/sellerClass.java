package com.example.fyptest.database;

public class sellerClass {
    private String s_ID;
    private String s_name;
    private String s_email;
    private String s_password;
    private String s_contactNum;
    private String s_ut_ID;

    public void sellerClass() {

    }

    public sellerClass(String s_ID, String s_name,
                       String s_email, String s_password,
                       String s_contactNum, String s_ut_ID) {
        this.s_ID = s_ID;
        this.s_name = s_name;
        this.s_email = s_email;
        this.s_password = s_password;
        this.s_contactNum = s_contactNum;
        this.s_ut_ID = s_ut_ID;
    }

    public String getS_ID() {
        return s_ID;
    }

    public String getS_name() {
        return s_name;
    }

    public String getS_email() {
        return s_email;
    }

    public String getS_password() {
        return s_password;
    }

    public String getS_contactNum() {
        return s_contactNum;
    }

    public String getS_ut_ID() {
        return s_ut_ID;
    }
}
