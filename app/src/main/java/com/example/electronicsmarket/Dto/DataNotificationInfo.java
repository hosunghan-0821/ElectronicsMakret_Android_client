package com.example.electronicsmarket.Dto;

public class DataNotificationInfo {
    private String type,message,postNum,notificationNum,notificationRegTime;
    private String notificationIsRead;

    private boolean isNotification;
    public boolean isReview;

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public boolean isReview() {
        return isReview;
    }

    public String getNotificationIsRead() {
        return notificationIsRead;
    }

    public void setNotificationIsRead(String notificationIsRead) {
        this.notificationIsRead = notificationIsRead;
    }

    public void setReview(boolean review) {
        isReview = review;
    }

    public String getNotificationRegTime() {
        return notificationRegTime;
    }

    public void setNotificationRegTime(String notificationRegTime) {
        this.notificationRegTime = notificationRegTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public String getNotificationNum() {
        return notificationNum;
    }

    public void setNotificationNum(String notificationNum) {
        this.notificationNum = notificationNum;
    }
}
