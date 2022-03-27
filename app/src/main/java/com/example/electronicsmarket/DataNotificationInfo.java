package com.example.electronicsmarket;

import java.util.ArrayList;

public class DataNotificationInfo {
    String type,message,postNum,notificationNum,notificationRegTime;
    boolean isReview;

    public boolean isReview() {
        return isReview;
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
