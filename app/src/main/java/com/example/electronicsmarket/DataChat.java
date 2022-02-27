package com.example.electronicsmarket;

public class DataChat {

    private int viewType;
    private String chat,chatTime;
    private String profileImageRoute;
    private String nickname;


    public DataChat(int viewType, String chat, String chatTime, String nickname) {
        this.viewType = viewType;
        this.chat = chat;
        this.chatTime = chatTime;
        this.nickname = nickname;
    }

    public DataChat(int viewType, String chat, String chatTime, String profileImageRoute, String nickname) {
        this.viewType = viewType;
        this.chat = chat;
        this.chatTime = chatTime;
        this.profileImageRoute = profileImageRoute;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getProfileImageRoute() {
        return profileImageRoute;
    }

    public void setProfileImageRoute(String profileImageRoute) {
        this.profileImageRoute = profileImageRoute;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
