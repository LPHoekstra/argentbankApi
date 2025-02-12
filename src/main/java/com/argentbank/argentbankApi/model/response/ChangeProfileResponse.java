package com.argentbank.argentbankApi.model.response;

public class ChangeProfileResponse {
    private String userName;

    public ChangeProfileResponse(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
