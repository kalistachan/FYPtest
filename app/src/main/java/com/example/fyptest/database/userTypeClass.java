package com.example.fyptest.database;

public class userTypeClass {

    private String ut_ID;
    private String ut_name;

    public void userTypeClass() {

    }

    public userTypeClass(String ut_ID, String ut_name) {
        this.ut_ID = ut_ID;
        this.ut_name = ut_name;
    }

    public String getUt_ID() {
        return ut_ID;
    }

    public String getUt_name() {
        return ut_name;
    }
}
