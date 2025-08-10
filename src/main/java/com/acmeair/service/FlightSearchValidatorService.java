package com.acmeair.service;

import com.acmeair.model.FlightType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class FlightSearchValidatorService {

    public void validateFlightSearchRequest(FlightType flightType, String departureAirport,
                                            String arrivalAirport, LocalDate departureDate,
                                            LocalDate returnDate, BigDecimal minPrice,
                                            BigDecimal maxPrice) {

        validateReturnFlightRequirements(flightType, returnDate, departureDate);
        validateDepartureDate(departureDate);
        validatePriceRange(minPrice, maxPrice);
        validateAirportCodes(departureAirport, arrivalAirport);
    }

    private void validateReturnFlightRequirements(FlightType flightType, LocalDate returnDate, LocalDate departureDate) {
        if (flightType == FlightType.RETURN && returnDate == null) {
            throw new IllegalArgumentException("Return date is required for return flights");
        }

        if (flightType == FlightType.RETURN && returnDate != null && !returnDate.isAfter(departureDate)) {
            throw new IllegalArgumentException("Return date must be after departure date");
        }
    }

    private void validateDepartureDate(LocalDate departureDate) {
        if (departureDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Departure date cannot be in the past");
        }
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
    }

    private void validateAirportCodes(String departureAirport, String arrivalAirport) {
        if (departureAirport.length() != 3) {
            throw new IllegalArgumentException("Departure airport code must be exactly 3 characters");
        }

        if (arrivalAirport.length() != 3) {
            throw new IllegalArgumentException("Arrival airport code must be exactly 3 characters");
        }

        if (departureAirport.equalsIgnoreCase(arrivalAirport)) {
            throw new IllegalArgumentException("Departure and arrival airports cannot be the same");
        }
    }
}