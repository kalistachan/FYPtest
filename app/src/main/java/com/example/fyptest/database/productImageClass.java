package com.example.fyptest.database;

public class productImageClass {
    private String productID;
    private String imageName;
    private String mImageUrl;

    public void productImageClass() {

    }

    public productImageClass(String productID, String imageName, String mImageUrl) {
        this.productID = productID;
        this.imageName = imageName;
        this.mImageUrl = mImageUrl;
    }

    public String getProductID() {
        return productID;
    }

    public String getImageName() {
        return imageName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }
}
