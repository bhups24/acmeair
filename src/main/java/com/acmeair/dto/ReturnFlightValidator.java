package com.acmeair.dto;

import com.acmeair.model.FlightType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReturnFlightValidator implements ConstraintValidator<ValidReturnFlight, BookingRequest> {

    @Override
    public boolean isValid(BookingRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getFlightType() == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (request.getFlightType() == FlightType.RETURN) {
            if (request.getReturnFlightId() == null || request.getReturnFlightId().trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Return flight ID is required for return flights")
                        .addPropertyNode("returnFlightId")
                        .addConstraintViolation();
                isValid = false;
            } else if (request.getFlightId() != null && request.getFlightId().equals(request.getReturnFlightId())) {
                context.buildConstraintViolationWithTemplate("Outbound and return flights cannot be the same")
                        .addPropertyNode("returnFlightId")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (request.getFlightType() == FlightType.ONE_WAY) {
            if (request.getReturnFlightId() != null && !request.getReturnFlightId().trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("Return flight ID should not be provided for one-way flights")
                        .addPropertyNode("returnFlightId")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}