package com.example.fyptest.database;

public class customerInfoClass {
    private String cus_ID;
    private String cus_fName;
    private String cus_lName;
    private String cus_shippingAddress;
    private String cus_postalCode;
    private int cus_loyaltyPoint;
    private String cus_userType;

    public void customerInfoClass() {

    }

    public customerInfoClass(String cus_ID, String cus_fName, String cus_lName, String cus_shippingAddress, String cus_postalCode, int cus_loyaltyPoint, String cus_userType) {
        this.cus_ID = cus_ID;
        this.cus_fName = cus_fName;
        this.cus_lName = cus_lName;
        this.cus_shippingAddress = cus_shippingAddress;
        this.cus_postalCode = cus_postalCode;
        this.cus_loyaltyPoint = cus_loyaltyPoint;
        this.cus_userType = cus_userType;
    }

    public String getCus_ID() {
        return cus_ID;
    }

    public String getCus_fName() {
        return cus_fName;
    }

    public String getCus_lName() {
        return cus_lName;
    }

    public String getCus_shippingAddress() {
        return cus_shippingAddress;
    }

    public String getCus_postalCode() {
        return cus_postalCode;
    }

    public int getCus_loyaltyPoint() {
        return cus_loyaltyPoint;
    }

    public String getCus_userType() {
        return cus_userType;
    }
}
