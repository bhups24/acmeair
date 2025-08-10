package com.acmeair.service;

import com.acmeair.dto.BookingRequest;
import com.acmeair.model.FlightType;
import com.acmeair.model.Passenger;
import com.acmeair.model.SeatClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ValidatorServicesTest {

    @InjectMocks
    private FlightSearchValidatorService flightSearchValidatorService;

    @InjectMocks
    private BookingValidatorService bookingValidatorService;

    private Passenger testPassenger;

    @BeforeEach
    void setUp() {
        testPassenger = new Passenger(null, "John", "Doe", "john.doe@email.com",
                "+61412345678", "A1234567", LocalDate.of(1990, 5, 15));
    }

    // FlightSearchValidatorService Tests
    @Test
    void validateFlightSearchRequest_Success_ValidOneWayRequest() {
        // Act & Assert - Should not throw exception
        assertThatCode(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYD", "MEL", LocalDate.now().plusDays(1),
                null, null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateFlightSearchRequest_Success_ValidReturnRequest() {
        // Act & Assert - Should not throw exception
        assertThatCode(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.RETURN, "SYD", "MEL", LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5), new BigDecimal("100"), new BigDecimal("500")))
                .doesNotThrowAnyException();
    }

    @Test
    void validateFlightSearchRequest_Failure_MissingReturnDate() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.RETURN, "SYD", "MEL", LocalDate.now().plusDays(1),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Return date is required for return flights");
    }

    @Test
    void validateFlightSearchRequest_Failure_ReturnDateBeforeDeparture() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.RETURN, "SYD", "MEL", LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(1), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Return date must be after departure date");
    }

    @Test
    void validateFlightSearchRequest_Failure_PastDepartureDate() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYD", "MEL", LocalDate.now().minusDays(1),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Departure date cannot be in the past");
    }

    @Test
    void validateFlightSearchRequest_Failure_InvalidPriceRange() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYD", "MEL", LocalDate.now().plusDays(1),
                null, new BigDecimal("500"), new BigDecimal("100")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minimum price cannot be greater than maximum price");
    }

    @Test
    void validateFlightSearchRequest_Failure_InvalidAirportCodeLength() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYDN", "MEL", LocalDate.now().plusDays(1),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Departure airport code must be exactly 3 characters");

        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYD", "ME", LocalDate.now().plusDays(1),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arrival airport code must be exactly 3 characters");
    }

    @Test
    void validateFlightSearchRequest_Failure_SameAirports() {
        // Act & Assert
        assertThatThrownBy(() -> flightSearchValidatorService.validateFlightSearchRequest(
                FlightType.ONE_WAY, "SYD", "SYD", LocalDate.now().plusDays(1),
                null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Departure and arrival airports cannot be the same");
    }

    // BookingValidatorService Tests
    @Test
    void validateBookingRequest_Success_ValidOneWayBooking() {
        // Arrange
        BookingRequest request = new BookingRequest(FlightType.ONE_WAY, "FL001", null,
                SeatClass.ECONOMY, testPassenger);

        // Act & Assert - Should not throw exception
        assertThatCode(() -> bookingValidatorService.validateBookingRequest(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateBookingRequest_Success_ValidReturnBooking() {
        // Arrange
        BookingRequest request = new BookingRequest(FlightType.RETURN, "FL001", "FL025",
                SeatClass.ECONOMY, testPassenger);

        // Act & Assert - Should not throw exception
        assertThatCode(() -> bookingValidatorService.validateBookingRequest(request))
                .doesNotThrowAnyException();
    }

    @Test
    void validateBookingRequest_Failure_MissingReturnFlightId() {
        // Arrange
        BookingRequest request = new BookingRequest(FlightType.RETURN, "FL001", null,
                SeatClass.ECONOMY, testPassenger);

        // Act & Assert
        assertThatThrownBy(() -> bookingValidatorService.validateBookingRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Return flight ID is required for return flights");
    }

    @Test
    void validateBookingRequest_Failure_ReturnFlightIdOnOneWay() {
        // Arrange
        BookingRequest request = new BookingRequest(FlightType.ONE_WAY, "FL001", "FL025",
                SeatClass.ECONOMY, testPassenger);

        // Act & Assert
        assertThatThrownBy(() -> bookingValidatorService.validateBookingRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Return flight ID should not be provided for one-way flights");
    }

    @Test
    void validateBookingRequest_Failure_SameFlightIds() {
        // Arrange
        BookingRequest request = new BookingRequest(FlightType.RETURN, "FL001", "FL001",
                SeatClass.ECONOMY, testPassenger);

        // Act & Assert
        assertThatThrownBy(() -> bookingValidatorService.validateBookingRequest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Outbound and return flights cannot be the same");
    }
}