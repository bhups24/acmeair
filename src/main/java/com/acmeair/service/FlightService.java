package com.acmeair.service;

import com.acmeair.dto.FlightSearchResponse;
import com.acmeair.exception.FlightNotFoundException;
import com.acmeair.model.Flight;
import com.acmeair.model.FlightType;
import com.acmeair.model.SeatClass;
import com.acmeair.repository.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class FlightService {
    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightSearchResponse searchFlights(FlightType flightType, String departureAirport, String arrivalAirport,
                                              LocalDate departureDate, LocalDate returnDate,
                                              BigDecimal minPrice, BigDecimal maxPrice, Boolean directFlightsOnly,
                                              int page, int size, String sortBy, String sortDirection) {

        if (flightType == FlightType.RETURN && returnDate == null) {
            throw new IllegalArgumentException("Return date is required for return flights");
        }

        if (flightType == FlightType.RETURN && !returnDate.isAfter(departureDate)) {
            throw new IllegalArgumentException("Return date must be after departure date");
        }

        Page<Flight> outboundPage = searchFlightsForDate(departureAirport, arrivalAirport, departureDate,
                minPrice, maxPrice, directFlightsOnly,
                page, size, sortBy, sortDirection);

        Page<Flight> returnPage = null;
        if (flightType == FlightType.RETURN) {
            returnPage = searchFlightsForDate(arrivalAirport, departureAirport, returnDate,
                    minPrice, maxPrice, directFlightsOnly,
                    page, size, sortBy, sortDirection);
        }

        return new FlightSearchResponse(
                flightType,
                outboundPage.getContent(),
                returnPage != null ? returnPage.getContent() : null,
                (int) outboundPage.getTotalElements(),
                outboundPage.getNumber(),
                outboundPage.getSize(),
                outboundPage.getTotalPages(),
                outboundPage.isFirst(),
                outboundPage.isLast()
        );
    }

    private Page<Flight> searchFlightsForDate(String departureAirport, String arrivalAirport, LocalDate date,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              Boolean directFlightsOnly, int page, int size,
                                              String sortBy, String sortDirection) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        Sort sort = createSort(sortBy, sortDirection);
        Pageable pageable = PageRequest.of(page, size, sort);

        return flightRepository.findFlights(departureAirport, arrivalAirport, startOfDay, endOfDay,
                minPrice, maxPrice, directFlightsOnly, pageable);
    }

    private Sort createSort(String sortBy, String sortDirection) {
        if ("price".equals(sortBy)) {
            sortBy = "economyPrice";
        }
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "departureTime";
        }

        if (!isValidSortField(sortBy)) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy + ". Valid fields are: price, departureTime, origin, destination");
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, sortBy);
    }

    private boolean isValidSortField(String sortBy) {
        return sortBy.equals("economyPrice") ||
                sortBy.equals("departureTime") ||
                sortBy.equals("origin") ||
                sortBy.equals("destination");
    }

    public Flight getFlightById(String id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new FlightNotFoundException("Flight with ID " + id + " does not exist"));
    }

    @Transactional
    public void updateAvailableSeats(String flightId, SeatClass seatClass, int seatsToReduce) {
        Flight flight = getFlightById(flightId);

        switch (seatClass) {
            case ECONOMY -> flight.setEconomyAvailable(flight.getEconomyAvailable() - seatsToReduce);
            case PREMIUM_ECONOMY -> flight.setPremiumEconomyAvailable(flight.getPremiumEconomyAvailable() - seatsToReduce);
            case BUSINESS -> flight.setBusinessAvailable(flight.getBusinessAvailable() - seatsToReduce);
            case FIRST_CLASS -> flight.setFirstClassAvailable(flight.getFirstClassAvailable() - seatsToReduce);
        }

        flightRepository.save(flight);
    }

    @Transactional
    public void increaseAvailableSeats(String flightId, SeatClass seatClass, int seatsToIncrease) {
        Flight flight = getFlightById(flightId);

        switch (seatClass) {
            case ECONOMY -> flight.setEconomyAvailable(flight.getEconomyAvailable() + seatsToIncrease);
            case PREMIUM_ECONOMY -> flight.setPremiumEconomyAvailable(flight.getPremiumEconomyAvailable() + seatsToIncrease);
            case BUSINESS -> flight.setBusinessAvailable(flight.getBusinessAvailable() + seatsToIncrease);
            case FIRST_CLASS -> flight.setFirstClassAvailable(flight.getFirstClassAvailable() + seatsToIncrease);
        }

        flightRepository.save(flight);
    }

    public boolean hasAvailableSeats(String flightId, SeatClass seatClass) {
        Flight flight = getFlightById(flightId);

        return switch (seatClass) {
            case ECONOMY -> flight.getEconomyAvailable() > 0;
            case PREMIUM_ECONOMY -> flight.getPremiumEconomyAvailable() > 0;
            case BUSINESS -> flight.getBusinessAvailable() > 0;
            case FIRST_CLASS -> flight.getFirstClassAvailable() > 0;
        };
    }

    public BigDecimal getPrice(String flightId, SeatClass seatClass) {
        Flight flight = getFlightById(flightId);

        return switch (seatClass) {
            case ECONOMY -> flight.getEconomyPrice();
            case PREMIUM_ECONOMY -> flight.getPremiumEconomyPrice();
            case BUSINESS -> flight.getBusinessPrice();
            case FIRST_CLASS -> flight.getFirstClassPrice();
        };
    }
}