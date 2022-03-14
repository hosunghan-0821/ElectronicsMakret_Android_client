package com.example.electronicsmarket;

public class DataChat {

    private int viewType;
    private String chat,chatTime;
    private String profileImageRoute;
    private String nickname;
    private String isReadChat;
    private String chatNum;
    private String chatType;



    public DataChat(int viewType, String chat, String chatTime, String nickname,String profileImageRoute) {
        this.viewType = viewType;
        this.chat = chat;
        this.chatTime = chatTime;
        this.nickname = nickname;
        this.profileImageRoute=profileImageRoute;
        this.chatNum=chatNum;
    }


    public DataChat( String chat,int viewType, String chatTime, String nickname , String isReadChat) {
        this.viewType = viewType;
        this.chat = chat;
        this.chatTime = chatTime;
        this.nickname = nickname;
        this.isReadChat=isReadChat;
        this.chatNum=chatNum;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getIsReadChat() {
        return isReadChat;
    }

    public String getChatNum() {
        return chatNum;
    }

    public void setChatNum(String chatNum) {
        this.chatNum = chatNum;
    }

    public void setIsReadChat(String isReadChat) {
        this.isReadChat = isReadChat;
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
