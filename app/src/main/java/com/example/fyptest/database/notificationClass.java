package com.example.fyptest.database;

public class notificationClass {
    private String noti_ID;
    private String noti_Title;
    private String noti_Description;
    private String noti_date;
    private String noti_prodID;

    public notificationClass() {

    }

    public notificationClass(String noti_ID, String noti_Title, String noti_Description, String noti_date, String noti_prodID) {
        this.noti_ID = noti_ID;
        this.noti_Title = noti_Title;
        this.noti_Description = noti_Description;
        this.noti_date = noti_date;
        this.noti_prodID = noti_prodID;
    }

    public String getNoti_ID() {
        return noti_ID;
    }

    public String getNoti_Title() {
        return noti_Title;
    }

    public String getNoti_Description() {
        return noti_Description;
    }

    public String getNoti_date() {
        return noti_date;
    }

    public String getNoti_prodID() {
        return noti_prodID;
    }
}
