package com.example.electronicsmarket.Dto;

import java.util.ArrayList;

public class DataChatRoomAll {

    private boolean notification;
    private String roomCount;
    private ArrayList<DataChatRoom> roomList;

    public ArrayList<DataChatRoom> getRoomList() {
        return roomList;
    }

    public String getRoomCount() {
        return roomCount;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public void setRoomCount(String roomCount) {
        this.roomCount = roomCount;
    }

    public void setRoomList(ArrayList<DataChatRoom> roomList) {
        this.roomList = roomList;
    }
}
