package com.example.electronicsmarket;

import java.util.ArrayList;

public class PostInfo {


    private String refundRegTime;
    private String buyRegTime;
    private String tradeConfirmTime;
    private String productNum;
    private String postStatus,tradeNum;
    private boolean isSuccess;
    private String receiverName;
    private boolean isLikeList;
    private String addressDetail,deliveryRequire;
    private String memberId,nickname,postTitle,postContents,postCategory,postSellType,postImageNum,postPrice,postDelivery,memberImage,postNum;
    private String  postRegTime,postViewNum,postLikeNum,postLocationDetail;
    private Double postLocationLatitude,postLocationLongitude;
    private String postLocationName,postLocationAddress;
    private ArrayList<String> imageRoute;
    private int viewType;
    private boolean clientIsLike;
    private String clientNickname,clientPhoneNumber;

    public boolean isReview;

    public String getRefundRegTime() {
        return refundRegTime;
    }

    public void setRefundRegTime(String refundRegTime) {
        this.refundRegTime = refundRegTime;
    }

    public boolean isReview() {
        return isReview;
    }

    public void setReview(boolean review) {
        isReview = review;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getTradeConfirmTime() {
        return tradeConfirmTime;
    }

    public String getBuyRegTime() {
        return buyRegTime;
    }

    public void setBuyRegTime(String buyRegTime) {
        this.buyRegTime = buyRegTime;
    }

    public void setTradeConfirmTime(String tradeConfirmTime) {
        this.tradeConfirmTime = tradeConfirmTime;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }


    public boolean isLikeList() {
        return isLikeList;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setLikeList(boolean likeList) {
        isLikeList = likeList;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getDeliveryRequire() {
        return deliveryRequire;
    }

    public void setDeliveryRequire(String deliveryRequire) {
        this.deliveryRequire = deliveryRequire;
    }

    public String getClientPhoneNumber() {
        return clientPhoneNumber;
    }

    public void setClientPhoneNumber(String clientPhoneNumber) {
        this.clientPhoneNumber = clientPhoneNumber;
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public void setClientNickname(String clientNickname) {
        this.clientNickname = clientNickname;
    }

    public PostInfo() {
    }

    public PostInfo(int viewType) {
        this.viewType = viewType;
    }


    public boolean isClientIsLike() {
        return clientIsLike;
    }

    public void setClientIsLike(boolean clientIsLike) {
        this.clientIsLike = clientIsLike;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getPostLocationDetail() {
        return postLocationDetail;
    }

    public void setPostLocationDetail(String postLocationDetail) {
        this.postLocationDetail = postLocationDetail;
    }

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
