package com.bliblifuture.invenger.annotation.imp;

import com.bliblifuture.invenger.annotation.PhoneConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint,String> {

    private final static String PATTERN = "^\\+?\\d+$";

    @Override
    public void initialize(PhoneConstraint constraintAnnotation) {
    }

    public static boolean isValid(String s){
        return s.matches(PATTERN);
    }


    public static String getErrorMessage(){
        return "Invalid phone number";
    }


    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches(PATTERN);
    }
}
