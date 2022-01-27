package com.example.electronicsmarket;

import java.util.ArrayList;

public class PostInfo {


    private String productNum;


    private String memberId,nickname,postTitle,postContents,postCategory,postSellType,postImageNum,postPrice,postDelivery,memberImage,postNum;
    private String  postRegTime,postViewNum,postLikeNum;
    private Double postLocationLatitude,postLocationLongitude;
    private String postLocationName,postLocationAddress;
    private ArrayList<String> imageRoute;


    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getProductNum() {
        return productNum;
    }

    public void setProductNum(String productNum) {
        this.productNum = productNum;
    }

    public Double getPostLocationLatitude() {
        return postLocationLatitude;
    }

    public void setPostLocationLatitude(Double postLocationLatitude) {
        this.postLocationLatitude = postLocationLatitude;
    }

    public Double getPostLocationLongitude() {
        return postLocationLongitude;
    }

    public void setPostLocationLongitude(Double postLocationLongitude) {
        this.postLocationLongitude = postLocationLongitude;
    }

    public String getPostLocationName() {
        return postLocationName;
    }

    public void setPostLocationName(String postLocationName) {
        this.postLocationName = postLocationName;
    }

    public String getPostLocationAddress() {
        return postLocationAddress;
    }

    public void setPostLocationAddress(String postLocationAddress) {
        this.postLocationAddress = postLocationAddress;
    }

    public String getPostViewNum() {
        return postViewNum;
    }

    public void setPostViewNum(String postViewNum) {
        this.postViewNum = postViewNum;
    }

    public String getPostLikeNum() {
        return postLikeNum;
    }

    public void setPostLikeNum(String postLikeNum) {
        this.postLikeNum = postLikeNum;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public String getMemberImage() {
        return memberImage;
    }

    public void setMemberImage(String memberImage) {
        this.memberImage = memberImage;
    }


    public String getPostRegTime() {
        return postRegTime;
    }

    public void setPostRegTime(String postRegTime) {
        this.postRegTime = postRegTime;
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
