package com.example.electronicsmarket.Dto;

import java.util.ArrayList;

public class ReviewAllInfo {
    private ArrayList<ReviewInfo> reviewInfo;
    private String reviewNum;

    private String totalReviewNum;
    private String totalReviewScore;



    public String getTotalReviewScore() {
        return totalReviewScore;
    }

    public void setTotalReviewScore(String totalReviewScore) {
        this.totalReviewScore = totalReviewScore;
    }

    public String getTotalReviewNum() {
        return totalReviewNum;
    }

    public void setTotalReviewNum(String totalReviewNum) {
        this.totalReviewNum = totalReviewNum;
    }

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
