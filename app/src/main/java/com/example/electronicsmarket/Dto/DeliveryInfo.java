package com.example.electronicsmarket.Dto;

import java.util.ArrayList;

public class DeliveryInfo {

    private String deliveryStatus;
    private ArrayList<String> timeArr,placeArr,detailArr;
    private boolean isSuccess;

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public ArrayList<String> getTimeArr() {
        return timeArr;
    }

    public void setTimeArr(ArrayList<String> timeArr) {
        this.timeArr = timeArr;
    }

    public ArrayList<String> getPlaceArr() {
        return placeArr;
    }

    public void setPlaceArr(ArrayList<String> placeArr) {
        this.placeArr = placeArr;
    }

    public ArrayList<String> getDetailArr() {
        return detailArr;
    }

    public void setDetailArr(ArrayList<String> detailArr) {
        this.detailArr = detailArr;
    }
}
