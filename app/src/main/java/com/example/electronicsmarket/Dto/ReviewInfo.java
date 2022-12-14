package com.example.electronicsmarket.Dto;

public class ReviewInfo {

    private String postTitle,postNum;
    private String reviewContents,reviewWriter,reviewTime,reviewRating,reviewWriterProfile;
    private String reviewNo;
    public String getPostTitle() {
        return postTitle;
    }

    public String getReviewNo() {
        return reviewNo;
    }


    public void setReviewNo(String reviewNo) {
        this.reviewNo = reviewNo;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public String getReviewContents() {
        return reviewContents;
    }

    public void setReviewContents(String reviewContents) {
        this.reviewContents = reviewContents;
    }

    public String getReviewWriter() {
        return reviewWriter;
    }

    public void setReviewWriter(String reviewWriter) {
        this.reviewWriter = reviewWriter;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(String reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getReviewWriterProfile() {
        return reviewWriterProfile;
    }

    public void setReviewWriterProfile(String reviewWriterProfile) {
        this.reviewWriterProfile = reviewWriterProfile;
    }
}
