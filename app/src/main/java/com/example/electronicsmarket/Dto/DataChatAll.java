package com.example.electronicsmarket.Dto;

import java.util.ArrayList;

public class DataChatAll {
    private ArrayList<DataChat> dataChatAllList;
    private String chatNum;
    public ArrayList<DataChat> getDataChatAllList() {
        return dataChatAllList;
    }

    public String getChatNum() {
        return chatNum;
    }

    public void setChatNum(String chatNum) {
        this.chatNum = chatNum;
    }

    public void setDataChatAllList(ArrayList<DataChat> dataChatAllList) {
        this.dataChatAllList = dataChatAllList;
    }
}
