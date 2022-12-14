package com.example.electronicsmarket.Dto;

public class DataChat {

    private int viewType;
    private String chat,chatTime;
    private String profileImageRoute;
    private String nickname;
    private String isReadChat;
    private String chatNum;
    private String chatType;
    private int networkStatus;
    private int identifyNum;
    private String chatRoomNum;

    public int getIdentifyNum() {
        return identifyNum;
    }

    public void setIdentifyNum(int identifyNum) {
        this.identifyNum = identifyNum;
    }

    public String getChatRoomNum() {
        return chatRoomNum;
    }

    public void setChatRoomNum(String chatRoomNum) {
        this.chatRoomNum = chatRoomNum;
    }

    public DataChat(){}
    public DataChat(int viewType, String chat, String chatTime, String nickname, String profileImageRoute) {
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
    public DataChat( String chat,int viewType, String chatTime, String nickname , String isReadChat,int networkStatus,String chatType,int identifyNum,String chatRoomNum) {
        this.viewType = viewType;
        this.chat = chat;
        this.chatTime = chatTime;
        this.nickname = nickname;
        this.isReadChat=isReadChat;
        this.networkStatus=networkStatus;
        this.chatType=chatType;
        this.identifyNum=identifyNum;
        this.chatRoomNum=chatRoomNum;
    }



    public int getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(int networkStatus) {
        this.networkStatus = networkStatus;
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
