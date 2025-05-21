package com.argentbank.argentbankApi.model.request;

import com.argentbank.argentbankApi.model.request.userNameValidation.UserName;

public class ChangeProfileRequest {

    @UserName
    private String userName;

    public String getUserName() {
        return userName;
    }
}
