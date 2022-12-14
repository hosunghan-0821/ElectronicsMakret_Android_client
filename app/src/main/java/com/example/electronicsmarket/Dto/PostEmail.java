package com.example.electronicsmarket.Dto;

public class PostEmail {


    private String mail;
    private String verifyNumber;
    private String isSuccess;

    public PostEmail(String mail, String verifyNumber, String isSuccess) {
        this.mail = mail;
        this.verifyNumber = verifyNumber;
        this.isSuccess = isSuccess;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getVerifyNumber() {
        return verifyNumber;
    }

    public void setVerifyNumber(String verifyNumber) {
        this.verifyNumber = verifyNumber;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
}
