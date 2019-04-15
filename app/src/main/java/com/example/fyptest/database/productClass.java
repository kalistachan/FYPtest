package com.example.fyptest.database;

public class productClass {
    private String pro_ID;
    private String pro_mImageUrl;
    private String pro_name;
    private String pro_description;
    private String pro_retailPrice;
    private String pro_maxOrderQtySellPrice;
    private String pro_minOrderQtySellPrice;
    private String pro_maxOrderDiscount;
    private String pro_minOrderAccepted;
    private String pro_minOrderDiscount;
    private String pro_shippingCost;
    private String pro_freeShippingAt;
    private String pro_durationForGroupPurchase;
    private String pro_Status;
    private String pro_aproveBy;
    private String pro_productType;
    private String pro_s_ID;

    public void productClass() {

    }

    public productClass(String pro_ID, String pro_mImageUrl, String pro_name,
                        String pro_description, String pro_retailPrice,
                        String pro_maxOrderQtySellPrice, String pro_minOrderQtySellPrice,
                        String pro_maxOrderDiscount, String pro_minOrderAccepted,
                        String pro_minOrderDiscount, String pro_shippingCost,
                        String pro_freeShippingAt, String pro_durationForGroupPurchase,
                        String pro_Status, String pro_aproveBy, String pro_productType,
                        String pro_s_ID) {
        this.pro_ID = pro_ID;
        this.pro_mImageUrl = pro_mImageUrl;
        this.pro_name = pro_name;
        this.pro_description = pro_description;
        this.pro_retailPrice = pro_retailPrice;
        this.pro_maxOrderQtySellPrice = pro_maxOrderQtySellPrice;
        this.pro_minOrderQtySellPrice = pro_minOrderQtySellPrice;
        this.pro_maxOrderDiscount = pro_maxOrderDiscount;
        this.pro_minOrderAccepted = pro_minOrderAccepted;
        this.pro_minOrderDiscount = pro_minOrderDiscount;
        this.pro_shippingCost = pro_shippingCost;
        this.pro_freeShippingAt = pro_freeShippingAt;
        this.pro_durationForGroupPurchase = pro_durationForGroupPurchase;
        this.pro_Status = pro_Status;
        this.pro_aproveBy = pro_aproveBy;
        this.pro_productType = pro_productType;
        this.pro_s_ID = pro_s_ID;
    }

    public String getPro_ID() {
        return pro_ID;
    }

    public String getPro_mImageUrl() {
        return pro_mImageUrl;
    }

    public String getPro_name() {
        return pro_name;
    }

    public String getPro_description() {
        return pro_description;
    }

    public String getPro_retailPrice() {
        return pro_retailPrice;
    }

    public String getPro_maxOrderQtySellPrice() {
        return pro_maxOrderQtySellPrice;
    }

    public String getPro_minOrderQtySellPrice() {
        return pro_minOrderQtySellPrice;
    }

    public String getPro_maxOrderDiscount() {
        return pro_maxOrderDiscount;
    }

    public String getPro_minOrderAccepted() {
        return pro_minOrderAccepted;
    }

    public String getPro_minOrderDiscount() {
        return pro_minOrderDiscount;
    }

    public String getPro_shippingCost() {
        return pro_shippingCost;
    }

    public String getPro_freeShippingAt() {
        return pro_freeShippingAt;
    }

    public String getPro_durationForGroupPurchase() {
        return pro_durationForGroupPurchase;
    }

    public String getPro_Status() {
        return pro_Status;
    }

    public String getPro_aproveBy() {
        return pro_aproveBy;
    }

    public String getPro_productType() {
        return pro_productType;
    }

    public String getPro_s_ID() {
        return pro_s_ID;
    }
}
