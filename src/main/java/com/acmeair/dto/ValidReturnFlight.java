package com.acmeair.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReturnFlightValidator.class)
@Documented
public @interface ValidReturnFlight {
    String message() default "Invalid return flight configuration";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}