package com.example.fyptest.database;

public class orderStatusClass {
    private String os_ID;
    private String os_Name;

    public void orderStatusClass() {

    }

    public orderStatusClass(String os_ID, String os_Name) {
        this.os_ID = os_ID;
        this.os_Name = os_Name;
    }

    public String getOs_ID() {
        return os_ID;
    }

    public String getOs_Name() {
        return os_Name;
    }
}


