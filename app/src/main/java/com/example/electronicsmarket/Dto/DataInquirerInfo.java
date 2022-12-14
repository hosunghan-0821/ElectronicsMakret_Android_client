package com.example.electronicsmarket.Dto;

public class DataInquirerInfo {
    private String nickname;
    private String imageRoute;
    private String finalChatTime;

    public String getFinalChatTime() {
        return finalChatTime;
    }

    public void setFinalChatTime(String finalChatTime) {
        this.finalChatTime = finalChatTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
    }
}
