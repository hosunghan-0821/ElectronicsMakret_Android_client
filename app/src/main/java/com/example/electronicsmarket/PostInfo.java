package com.example.electronicsmarket;

import java.util.ArrayList;

public class PostInfo {

    private String nickname,postTitle,postContents,postCategory,postSellType,postImageNum,postPrice,postDelivery,memberImage;
    private ArrayList<String> imageRoute;

    public String getMemberImage() {
        return memberImage;
    }

    public void setMemberImage(String memberImage) {
        this.memberImage = memberImage;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostContents() {
        return postContents;
    }

    public void setPostContents(String postContents) {
        this.postContents = postContents;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostSellType() {
        return postSellType;
    }

    public void setPostSellType(String postSellType) {
        this.postSellType = postSellType;
    }

    public String getPostImageNum() {
        return postImageNum;
    }

    public void setPostImageNum(String postImageNum) {
        this.postImageNum = postImageNum;
    }

    public String getPostPrice() {
        return postPrice;
    }

    public void setPostPrice(String postPrice) {
        this.postPrice = postPrice;
    }

    public String getPostDelivery() {
        return postDelivery;
    }

    public void setPostDelivery(String postDelivery) {
        this.postDelivery = postDelivery;
    }

    public ArrayList<String> getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(ArrayList<String> imageRoute) {
        this.imageRoute = imageRoute;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
