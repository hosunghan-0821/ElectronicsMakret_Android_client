package com.example.electronicsmarket.Dto;

public class DataMemberSignup {
    private String nickname;
    private String message;
    public String imageRoute;
    private boolean isSuccess;
    private boolean likeNotification;


    public boolean isLikeNotification() {
        return likeNotification;
    }

    public void setLikeNotification(boolean likeNotification) {
        this.likeNotification = likeNotification;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
