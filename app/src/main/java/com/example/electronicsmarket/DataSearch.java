package com.example.electronicsmarket;

import com.google.gson.annotations.SerializedName;

public class DataSearch {
    @SerializedName("address_name")
    private String addressName;

    @SerializedName("x")
    private String longitude;

    @SerializedName("y")
    private String Latitude;

    @SerializedName("road_address_name")
    private String roadAddress;

    @SerializedName("place_name")
    private String placeName;

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
