package com.bliblifuture.Invenger.annotation.imp;

import com.bliblifuture.Invenger.annotation.PhoneConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<PhoneConstraint,String> {
    @Override
    public void initialize(PhoneConstraint phoneConstraint) {

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.matches("^\\+?\\d+$");
    }
}
