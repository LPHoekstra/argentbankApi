package com.argentbank.argentbankApi.model.request;

public class ChangeProfileRequest {
    private String userName;

    ChangeProfileRequest() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
