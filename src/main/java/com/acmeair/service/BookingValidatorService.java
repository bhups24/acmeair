package com.acmeair.service;

import com.acmeair.dto.BookingRequest;
import com.acmeair.model.FlightType;
import org.springframework.stereotype.Service;

@Service
public class BookingValidatorService {

    public void validateBookingRequest(BookingRequest bookingRequest) {
        validateReturnFlightRequirements(bookingRequest);
        validateOneWayFlightRequirements(bookingRequest);
        validateFlightDifference(bookingRequest);
    }

    private void validateReturnFlightRequirements(BookingRequest bookingRequest) {
        if (bookingRequest.getFlightType() == FlightType.RETURN) {
            if (bookingRequest.getReturnFlightId() == null || bookingRequest.getReturnFlightId().trim().isEmpty()) {
                throw new IllegalArgumentException("Return flight ID is required for return flights");
            }
        }
    }

    private void validateOneWayFlightRequirements(BookingRequest bookingRequest) {
        if (bookingRequest.getFlightType() == FlightType.ONE_WAY &&
                bookingRequest.getReturnFlightId() != null && !bookingRequest.getReturnFlightId().trim().isEmpty()) {
            throw new IllegalArgumentException("Return flight ID should not be provided for one-way flights");
        }
    }

    private void validateFlightDifference(BookingRequest bookingRequest) {
        if (bookingRequest.getFlightType() == FlightType.RETURN &&
                bookingRequest.getFlightId().equals(bookingRequest.getReturnFlightId())) {
            throw new IllegalArgumentException("Outbound and return flights cannot be the same");
        }
    }
}