package com.example.fyptest.database;

public class userClass {
    private String userID;
    private String email;
    private String password;
    private String contactNum;
    private String userType;

    public void userClass() {

    }

    public userClass(String userID, String email, String password, String contactNum, String userType) {
        this.userID = userID;
        this.email = email;
        this.password = password;
        this.contactNum = contactNum;
        this.userType = userType;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNum() {
        return contactNum;
    }

    public String getUserType() {
        return userType;
    }
}
