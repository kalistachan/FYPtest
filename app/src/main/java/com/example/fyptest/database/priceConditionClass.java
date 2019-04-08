package com.example.fyptest.database;

public class priceConditionClass {
    private String apc_ID;
    private float apc_discountRate;

    public void priceConditionClass() {

    }

    public priceConditionClass(String apc_ID, float apc_discountRate) {
        this.apc_ID = apc_ID;
        this.apc_discountRate = apc_discountRate;
    }

    public String getApc_ID() {
        return apc_ID;
    }

    public float getApc_discountRate() {
        return apc_discountRate;
    }
}
