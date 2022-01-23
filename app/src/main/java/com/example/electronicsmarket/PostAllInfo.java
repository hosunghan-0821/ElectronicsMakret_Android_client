package com.example.electronicsmarket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostAllInfo {

    @SerializedName("postInfo")
    ArrayList<PostInfo> postInfo;

    public ArrayList<PostInfo> getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(ArrayList<PostInfo> postInfo) {
        this.postInfo = postInfo;
    }
}
