package com.acmeair.controller;

import com.acmeair.dto.FlightResponseDto;
import com.acmeair.dto.FlightSearchResponse;
import com.acmeair.model.Flight;
import com.acmeair.model.FlightType;
import com.acmeair.service.FlightService;
import com.acmeair.service.FlightSearchValidatorService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/flights")
@Validated
public class FlightController {
    private final FlightService flightService;
    private final FlightSearchValidatorService flightSearchValidatorService;

    public FlightController(FlightService flightService, FlightSearchValidatorService flightSearchValidatorService) {
        this.flightService = flightService;
        this.flightSearchValidatorService = flightSearchValidatorService;
    }

    @GetMapping("/search")
    public ResponseEntity<FlightSearchResponse> searchFlights(
            @RequestParam @NotNull(message = "Flight type is required") FlightType flightType,
            @RequestParam @NotBlank(message = "Departure airport is required") String departureAirport,
            @RequestParam @NotBlank(message = "Arrival airport is required") String arrivalAirport,
            @RequestParam @NotNull(message = "Departure date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate,
            @RequestParam(required = false) @Positive(message = "Minimum price must be greater than 0") BigDecimal minPrice,
            @RequestParam(required = false) @Positive(message = "Maximum price must be greater than 0") BigDecimal maxPrice,
            @RequestParam(required = false) Boolean directFlightsOnly,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be 0 or greater") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 100, message = "Page size cannot exceed 100") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        flightSearchValidatorService.validateFlightSearchRequest(
                flightType, departureAirport, arrivalAirport, departureDate,
                returnDate, minPrice, maxPrice);

        FlightSearchResponse response = flightService.searchFlights(
                flightType, departureAirport, arrivalAirport, departureDate, returnDate,
                minPrice, maxPrice, directFlightsOnly,
                page, size, sortBy, sortDirection
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightResponseDto> getFlightDetails(
            @PathVariable @NotBlank(message = "Flight ID is required") String flightId) {

        Flight flight = flightService.getFlightById(flightId);
        FlightResponseDto flightDto = new FlightResponseDto(flight);
        return ResponseEntity.ok(flightDto);
    }
}