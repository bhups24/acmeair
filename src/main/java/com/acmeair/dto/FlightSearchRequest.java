package com.acmeair.dto;

import com.acmeair.model.FlightType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@ValidFlightSearch
public class FlightSearchRequest {
    @NotNull(message = "Flight type is required")
    private FlightType flightType;

    @NotBlank(message = "Departure airport is required")
    @Size(min = 3, max = 3, message = "Departure airport code must be exactly 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Departure airport code must contain only uppercase letters")
    private String departureAirport;

    @NotBlank(message = "Arrival airport is required")
    @Size(min = 3, max = 3, message = "Arrival airport code must be exactly 3 characters")
    @Pattern(regexp = "[A-Z]{3}", message = "Arrival airport code must contain only uppercase letters")
    private String arrivalAirport;

    @NotNull(message = "Departure date is required")
    @Future(message = "Departure date must be in the future")
    private LocalDate departureDate;

    private LocalDate returnDate;

    @DecimalMin(value = "0.01", message = "Minimum price must be greater than 0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.01", message = "Maximum price must be greater than 0")
    private BigDecimal maxPrice;

    private Boolean directFlightsOnly;

    public FlightSearchRequest() {}

    public FlightSearchRequest(FlightType flightType, String departureAirport, String arrivalAirport,
                               LocalDate departureDate, LocalDate returnDate,
                               BigDecimal minPrice, BigDecimal maxPrice, Boolean directFlightsOnly) {
        this.flightType = flightType;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.directFlightsOnly = directFlightsOnly;
    }

    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }

    public String getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }

    public String getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getDirectFlightsOnly() { return directFlightsOnly; }
    public void setDirectFlightsOnly(Boolean directFlightsOnly) { this.directFlightsOnly = directFlightsOnly; }
}