package com.example.electronicsmarket;

import java.util.ArrayList;

public class DataNotificationAllInfo {
    private ArrayList<DataNotificationInfo> notificationDataList;
    private String notificationNum;

    public String getNotificationNum() {
        return notificationNum;
    }

    public void setNotificationNum(String notificationNum) {
        this.notificationNum = notificationNum;
    }

    public ArrayList<DataNotificationInfo> getNotificationDataList() {
        return notificationDataList;
    }

    public void setNotificationDataList(ArrayList<DataNotificationInfo> notificationDataList) {
        this.notificationDataList = notificationDataList;
    }
}
