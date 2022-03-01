package com.example.electronicsmarket;

import java.util.ArrayList;

public class DataChatRoomAll {

    private String roomCount;
    private ArrayList<DataChatRoom> roomList;

    public ArrayList<DataChatRoom> getRoomList() {
        return roomList;
    }

    public String getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(String roomCount) {
        this.roomCount = roomCount;
    }

    public void setRoomList(ArrayList<DataChatRoom> roomList) {
        this.roomList = roomList;
    }
}
