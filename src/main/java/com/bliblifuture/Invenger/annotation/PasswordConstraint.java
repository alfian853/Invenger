package com.bliblifuture.Invenger.annotation;

import com.bliblifuture.Invenger.annotation.imp.PasswordValdator;
import com.bliblifuture.Invenger.annotation.imp.PhoneValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValdator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {
    String message() default "Weak password, your password must contain atleast 1 Uppercase, 1 lowercase and 1 number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
