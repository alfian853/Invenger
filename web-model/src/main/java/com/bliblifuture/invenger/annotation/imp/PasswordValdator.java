package com.bliblifuture.invenger.annotation.imp;

import com.bliblifuture.invenger.annotation.PasswordConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValdator implements ConstraintValidator<PasswordConstraint,String> {

    private final static String PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {

    }

    public static String getErrorMessage(){
        return "Weak password, your password must contain atleast 1 Uppercase, 1 lowercase and 1 number";
    }

    public static boolean isValid(String s) {
        return s.matches(PATTERN);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return s.matches(PATTERN);
    }
}
