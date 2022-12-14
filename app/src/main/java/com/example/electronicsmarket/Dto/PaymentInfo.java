package com.example.electronicsmarket.Dto;

public class PaymentInfo {
    public boolean isSuccess;

    private String deliveryStatus;
    private String tradeBuyer;
    private String postNum;
    private String tradeNum,tradeTitle,tradePrice,tradeImageRoute,tradeType;
    private String tradeRegTime,tradeSeller,tradePayType,tradeReceiver,tradeReceiverPhoneNum;
    private String tradeAddress,tradeDeliveryStatus,tradeDeliveryRequire,tradeDeliveryCompany,tradeDeliveryNum;


    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getTradeBuyer() {
        return tradeBuyer;
    }

    public void setTradeBuyer(String tradeBuyer) {
        this.tradeBuyer = tradeBuyer;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getTradeTitle() {
        return tradeTitle;
    }

    public String getPostNum() {
        return postNum;
    }

    public void setPostNum(String postNum) {
        this.postNum = postNum;
    }

    public void setTradeTitle(String tradeTitle) {
        this.tradeTitle = tradeTitle;
    }

    public String getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(String tradePrice) {
        this.tradePrice = tradePrice;
    }

    public String getTradeImageRoute() {
        return tradeImageRoute;
    }

    public void setTradeImageRoute(String tradeImageRoute) {
        this.tradeImageRoute = tradeImageRoute;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTradeRegTime() {
        return tradeRegTime;
    }

    public void setTradeRegTime(String tradeRegTime) {
        this.tradeRegTime = tradeRegTime;
    }

    public String getTradeSeller() {
        return tradeSeller;
    }

    public void setTradeSeller(String tradeSeller) {
        this.tradeSeller = tradeSeller;
    }

    public String getTradePayType() {
        return tradePayType;
    }

    public void setTradePayType(String tradePayType) {
        this.tradePayType = tradePayType;
    }

    public String getTradeReceiver() {
        return tradeReceiver;
    }

    public void setTradeReceiver(String tradeReceiver) {
        this.tradeReceiver = tradeReceiver;
    }

    public String getTradeReceiverPhoneNum() {
        return tradeReceiverPhoneNum;
    }

    public void setTradeReceiverPhoneNum(String tradeReceiverPhoneNum) {
        this.tradeReceiverPhoneNum = tradeReceiverPhoneNum;
    }

    public String getTradeAddress() {
        return tradeAddress;
    }

    public void setTradeAddress(String tradeAddress) {
        this.tradeAddress = tradeAddress;
    }

    public String getTradeDeliveryStatus() {
        return tradeDeliveryStatus;
    }

    public void setTradeDeliveryStatus(String tradeDeliveryStatus) {
        this.tradeDeliveryStatus = tradeDeliveryStatus;
    }

    public String getTradeDeliveryRequire() {
        return tradeDeliveryRequire;
    }

    public void setTradeDeliveryRequire(String tradeDeliveryRequire) {
        this.tradeDeliveryRequire = tradeDeliveryRequire;
    }

    public String getTradeDeliveryCompany() {
        return tradeDeliveryCompany;
    }

    public void setTradeDeliveryCompany(String tradeDeliveryCompany) {
        this.tradeDeliveryCompany = tradeDeliveryCompany;
    }

    public String getTradeDeliveryNum() {
        return tradeDeliveryNum;
    }

    public void setTradeDeliveryNum(String tradeDeliveryNum) {
        this.tradeDeliveryNum = tradeDeliveryNum;
    }
}
