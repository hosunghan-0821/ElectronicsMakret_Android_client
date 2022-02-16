package com.example.electronicsmarket;

import java.util.ArrayList;

public class ReviewAllInfo {
    private ArrayList<ReviewInfo> reviewInfo;
    private String reviewNum;

    public ArrayList<ReviewInfo> getReviewInfo() {
        return reviewInfo;
    }

    public void setReviewInfo(ArrayList<ReviewInfo> reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    public String getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(String reviewNum) {
        this.reviewNum = reviewNum;
    }
}
