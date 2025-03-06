package com.argentbank.argentbankApi.model.request.userNameValidation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UserNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UserName {
    String message() default "User name must be between 2 and 20 characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
