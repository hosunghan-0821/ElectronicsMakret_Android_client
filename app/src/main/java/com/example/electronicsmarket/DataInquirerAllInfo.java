package com.example.electronicsmarket;

import java.util.ArrayList;

public class DataInquirerAllInfo {

    private ArrayList<DataInquirerInfo> inquirerList;
    private String postNum,postTitle,postPrice,productImageRoute;

    public ArrayList<DataInquirerInfo> getInquirerList() {
        return inquirerList;
    }

    public void setInquirerList(ArrayList<DataInquirerInfo> inquirerList) {
        this.inquirerList = inquirerList;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(String postPrice) {
        this.postPrice = postPrice;
    }

    public String getProductImageRoute() {
        return productImageRoute;
    }

    public void setProductImageRoute(String productImageRoute) {
        this.productImageRoute = productImageRoute;
    }
}
