package com.example.electronicsmarket;

public class RefundInfo {

    private boolean isSuccess;
    private String kakaoTid;

    private String tradeType;

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getKakaoTid() {
        return kakaoTid;
    }

    public void setKakaoTid(String kakaoTid) {
        this.kakaoTid = kakaoTid;
    }
}
