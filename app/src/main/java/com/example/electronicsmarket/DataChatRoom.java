package com.example.electronicsmarket;

public class DataChatRoom {
    private String postNum,postTitle,postPrice,postStatus,imageRoute;
    private String postLocationName,postLocationAddress;
    private Double postLocationLatitude,postLocationLongitude;
    private String roomNum;
    private String otherUserNickname;
    private String otherUserImageRoute;

    public String getOtherUserNickname() {
        return otherUserNickname;
    }

    public void setOtherUserNickname(String otherUserNickname) {
        this.otherUserNickname = otherUserNickname;
    }

    public String getOtherUserImageRoute() {
        return otherUserImageRoute;
    }

    public void setOtherUserImageRoute(String otherUserImageRoute) {
        this.otherUserImageRoute = otherUserImageRoute;
    }

    public String getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(String roomNum) {
        this.roomNum = roomNum;
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

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
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
}
