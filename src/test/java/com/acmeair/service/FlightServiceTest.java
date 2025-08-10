package com.acmeair.service;

import com.acmeair.exception.FlightNotFoundException;
import com.acmeair.model.Flight;
import com.acmeair.model.SeatClass;
import com.acmeair.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlight = new Flight(
                "FL001", "AC101", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 6, 0),
                LocalDateTime.of(2025, 8, 15, 7, 30),
                "Boeing 737",
                new BigDecimal("199.99"), new BigDecimal("299.99"),
                new BigDecimal("599.99"), new BigDecimal("999.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                true, 0
        );
    }

    @Test
    void getFlightById_Success_ReturnsFlightWhenExists() {
        // Arrange
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(testFlight));

        // Act
        Flight result = flightService.getFlightById("FL001");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("FL001");
        assertThat(result.getFlightNumber()).isEqualTo("AC101");
        assertThat(result.getOrigin()).isEqualTo("SYD");
        assertThat(result.getDestination()).isEqualTo("MEL");
        verify(flightRepository).findById("FL001");
    }

    @Test
    void getFlightById_Failure_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(flightRepository.findById("INVALID_ID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> flightService.getFlightById("INVALID_ID"))
                .isInstanceOf(FlightNotFoundException.class)
                .hasMessage("Flight with ID INVALID_ID does not exist");
        verify(flightRepository).findById("INVALID_ID");
    }

    @Test
    void hasAvailableSeats_Success_ReturnsTrueWhenSeatsAvailable() {
        // Arrange
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(testFlight));

        // Act & Assert
        assertThat(flightService.hasAvailableSeats("FL001", SeatClass.ECONOMY)).isTrue();
        assertThat(flightService.hasAvailableSeats("FL001", SeatClass.PREMIUM_ECONOMY)).isTrue();
        assertThat(flightService.hasAvailableSeats("FL001", SeatClass.BUSINESS)).isTrue();
        assertThat(flightService.hasAvailableSeats("FL001", SeatClass.FIRST_CLASS)).isTrue();
    }

    @Test
    void hasAvailableSeats_Failure_ReturnsFalseWhenNoSeatsAvailable() {
        // Arrange - Create flight with no available seats
        Flight noSeatsFlight = new Flight(
                "FL004", "AC104", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 15, 0),
                LocalDateTime.of(2025, 8, 15, 16, 30),
                "Boeing 737",
                new BigDecimal("279.99"), new BigDecimal("379.99"),
                new BigDecimal("679.99"), new BigDecimal("1149.99"),
                0, 0, 8, 1,  // No economy or premium economy seats available
                120, 24, 16, 4,
                true, 0
        );

        when(flightRepository.findById("FL004")).thenReturn(Optional.of(noSeatsFlight));

        // Act & Assert
        assertThat(flightService.hasAvailableSeats("FL004", SeatClass.ECONOMY)).isFalse();
        assertThat(flightService.hasAvailableSeats("FL004", SeatClass.PREMIUM_ECONOMY)).isFalse();
        assertThat(flightService.hasAvailableSeats("FL004", SeatClass.BUSINESS)).isTrue();
        assertThat(flightService.hasAvailableSeats("FL004", SeatClass.FIRST_CLASS)).isTrue();
    }

    @Test
    void getPrice_Success_ReturnsCorrectPriceForEachSeatClass() {
        // Arrange
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(testFlight));

        // Act & Assert
        assertThat(flightService.getPrice("FL001", SeatClass.ECONOMY))
                .isEqualTo(new BigDecimal("199.99"));
        assertThat(flightService.getPrice("FL001", SeatClass.PREMIUM_ECONOMY))
                .isEqualTo(new BigDecimal("299.99"));
        assertThat(flightService.getPrice("FL001", SeatClass.BUSINESS))
                .isEqualTo(new BigDecimal("599.99"));
        assertThat(flightService.getPrice("FL001", SeatClass.FIRST_CLASS))
                .isEqualTo(new BigDecimal("999.99"));
    }

    @Test
    void updateAvailableSeats_Success_ReducesAvailableSeats() {
        // Arrange
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(testFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        // Act
        flightService.updateAvailableSeats("FL001", SeatClass.ECONOMY, 2);

        // Assert
        assertThat(testFlight.getEconomyAvailable()).isEqualTo(118); // 120 - 2
        verify(flightRepository).save(testFlight);
    }

    @Test
    void increaseAvailableSeats_Success_IncreasesAvailableSeats() {
        // Arrange
        testFlight.setEconomyAvailable(118); // Start with reduced seats
        when(flightRepository.findById("FL001")).thenReturn(Optional.of(testFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        // Act
        flightService.increaseAvailableSeats("FL001", SeatClass.ECONOMY, 1);

        // Assert
        assertThat(testFlight.getEconomyAvailable()).isEqualTo(119); // 118 + 1
        verify(flightRepository).save(testFlight);
    }
}