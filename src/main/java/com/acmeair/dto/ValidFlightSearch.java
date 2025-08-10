package com.acmeair.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FlightSearchValidator.class)
@Documented
public @interface ValidFlightSearch {
    String message() default "Invalid flight search parameters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}