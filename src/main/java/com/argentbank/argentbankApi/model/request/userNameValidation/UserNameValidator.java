package com.argentbank.argentbankApi.model.request.userNameValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserNameValidator implements ConstraintValidator<UserName, String> {

    public boolean isValid(String userName, ConstraintValidatorContext context) {
        if (userName == null) {
            return false;
        }
        return userName.length() >= 2 && userName.length() <= 20;
    }
}
