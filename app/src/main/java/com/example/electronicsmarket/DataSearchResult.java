package com.example.electronicsmarket;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataSearchResult {

    @SerializedName("documents")
    ArrayList<DataSearch> placeAllInfo;

    public ArrayList<DataSearch> getPlaceAllInfo() {
        return placeAllInfo;
    }

    public void setPlaceAllInfo(ArrayList<DataSearch> placeAllInfo) {
        this.placeAllInfo = placeAllInfo;
    }
}
