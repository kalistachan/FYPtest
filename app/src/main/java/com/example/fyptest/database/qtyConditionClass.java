package com.example.fyptest.database;

public class qtyConditionClass {
    private String aqc_ID;
    private float aqc_qtyPercentage;

    public void qtyConditionClass() {

    }

    public qtyConditionClass(String aqc_ID, float aqc_qtyPercentage) {
        this.aqc_ID = aqc_ID;
        this.aqc_qtyPercentage = aqc_qtyPercentage;
    }

    public String getAqc_ID() {
        return aqc_ID;
    }

    public float getAqc_qtyPercentage() {
        return aqc_qtyPercentage;
    }
}
