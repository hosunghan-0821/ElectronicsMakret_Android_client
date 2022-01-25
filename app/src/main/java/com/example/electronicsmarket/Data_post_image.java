package com.example.electronicsmarket;

import android.net.Uri;

public class Data_post_image {

    private Uri imguri;
    private String imgUrl;

    public Data_post_image(Uri imguri, String imgUrl) {
        this.imguri = imguri;
        this.imgUrl = imgUrl;
    }

    public Data_post_image() {
    }

    public Data_post_image(Uri imguri) {
        this.imguri = imguri;
    }

    public Data_post_image(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Uri getImguri() {
        return imguri;
    }

    public void setImguri(Uri imguri) {
        this.imguri = imguri;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
