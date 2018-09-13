package com.bliblifuture.Invenger.annotation.imp;

import com.bliblifuture.Invenger.annotation.PasswordConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValdator implements ConstraintValidator<PasswordConstraint,String> {

    private final static String PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";

    @Override
    public void initialize(PasswordConstraint constraintAnnotation) {

    }

    public static boolean isValid(String s) {
        return s.matches(PATTERN);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        return s.matches(PATTERN);
    }
}
