package com.example.fyptest.database;

public class customerClass {
    private String cus_ID;
    private String cus_email;
    private String cus_contactNum;
    private String cus_firstName;
    private String cus_LastName;
    private String cus_password;
    private String cus_address;
    private String cus_postalCode;
    private int cus_loyaltyPoint;
    private String cus_ut_ID;

    public void customerClass() {

    }

    public customerClass(String cus_ID, String cus_email, String cus_contactNum, String cus_firstName,
                         String cus_LastName, String cus_password, String cus_address, String cus_postalCode,
                         int cus_loyaltyPoint, String cus_ut_ID) {
        this.cus_ID = cus_ID;
        this.cus_email = cus_email;
        this.cus_contactNum = cus_contactNum;
        this.cus_firstName = cus_firstName;
        this.cus_LastName = cus_LastName;
        this.cus_password = cus_password;
        this.cus_address = cus_address;
        this.cus_postalCode = cus_postalCode;
        this.cus_loyaltyPoint = cus_loyaltyPoint;
        this.cus_ut_ID = cus_ut_ID;
    }

    public String getCus_ID() {
        return cus_ID;
    }

    public String getCus_email() {
        return cus_email;
    }

    public String getCus_contactNum() {
        return cus_contactNum;
    }

    public String getCus_firstName() {
        return cus_firstName;
    }

    public String getCus_LastName() {
        return cus_LastName;
    }

    public String getCus_password() {
        return cus_password;
    }

    public String getCus_address() {
        return cus_address;
    }

    public String getCus_postalCode() {
        return cus_postalCode;
    }

    public int getCus_loyaltyPoint() {
        return cus_loyaltyPoint;
    }

    public String getCus_ut_ID() {
        return cus_ut_ID;
    }
}
