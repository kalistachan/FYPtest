package com.example.fyptest.database;

public class blacklistedCreditCardClass {
    private String bcc_ID;
    private String bcc_dateAdded;
    private String bcc_cc_Num;

    public void blacklistedCreditCardClass() {

    }

    public blacklistedCreditCardClass(String bcc_ID, String bcc_dateAdded, String bcc_cc_Num) {
        this.bcc_ID = bcc_ID;
        this.bcc_dateAdded = bcc_dateAdded;
        this.bcc_cc_Num = bcc_cc_Num;
    }

    public String getBcc_ID() {
        return bcc_ID;
    }

    public String getBcc_dateAdded() {
        return bcc_dateAdded;
    }

    public String getBcc_cc_Num() {
        return bcc_cc_Num;
    }
}
