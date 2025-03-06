package com.argentbank.argentbankApi.model.request;

import com.argentbank.argentbankApi.model.request.userNameValidation.UserName;

public class ChangeProfileRequest {

    @UserName
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
