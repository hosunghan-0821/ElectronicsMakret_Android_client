package com.example.electronicsmarket.Dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostAllInfo {

    @SerializedName("postInfo")
    public ArrayList<PostInfo> postInfo;

    private int viewType;
    private String imageRoute;
    private String productNum;

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
    }





    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }


    public ArrayList<PostInfo> getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(ArrayList<PostInfo> postInfo) {
        this.postInfo = postInfo;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

}
