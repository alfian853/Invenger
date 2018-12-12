package com.bliblifuture.invenger.annotation;

import com.bliblifuture.invenger.annotation.imp.PasswordValdator;

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
