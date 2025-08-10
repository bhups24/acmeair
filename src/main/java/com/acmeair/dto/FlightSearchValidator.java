package com.acmeair.dto;

import com.acmeair.model.FlightType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FlightSearchValidator implements ConstraintValidator<ValidFlightSearch, FlightSearchRequest> {

    @Override
    public boolean isValid(FlightSearchRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (request.getFlightType() == FlightType.RETURN) {
            if (request.getReturnDate() == null) {
                context.buildConstraintViolationWithTemplate("Return date is required for return flights")
                        .addPropertyNode("returnDate")
                        .addConstraintViolation();
                isValid = false;
            } else if (request.getDepartureDate() != null && !request.getReturnDate().isAfter(request.getDepartureDate())) {
                context.buildConstraintViolationWithTemplate("Return date must be after departure date")
                        .addPropertyNode("returnDate")
                        .addConstraintViolation();
                isValid = false;
            }
        }

        if (request.getDepartureAirport() != null && request.getArrivalAirport() != null &&
                request.getDepartureAirport().equalsIgnoreCase(request.getArrivalAirport())) {
            context.buildConstraintViolationWithTemplate("Departure and arrival airports cannot be the same")
                    .addPropertyNode("arrivalAirport")
                    .addConstraintViolation();
            isValid = false;
        }

        if (request.getMinPrice() != null && request.getMaxPrice() != null &&
                request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            context.buildConstraintViolationWithTemplate("Minimum price cannot be greater than maximum price")
                    .addPropertyNode("maxPrice")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}