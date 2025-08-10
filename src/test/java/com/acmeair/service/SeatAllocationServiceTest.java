package com.acmeair.service;

import com.acmeair.model.Flight;
import com.acmeair.model.SeatClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeatAllocationServiceTest {

    @InjectMocks
    private SeatAllocationService seatAllocationService;

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
    void allocateSeat_Success_EconomySeatFormat() {
        // Act
        String seatNumber = seatAllocationService.allocateSeat(testFlight, SeatClass.ECONOMY);

        // Assert
        assertThat(seatNumber).isNotNull();
        assertThat(seatNumber).matches("^(1[0-9]|2[0-9]|3[0-5])[A-F]$"); // Rows 10-35, Seats A-F
        assertThat(seatNumber.length()).isBetween(3, 4); // e.g., "10A" or "35F"
    }

    @Test
    void allocateSeat_Success_PremiumEconomySeatFormat() {
        // Act
        String seatNumber = seatAllocationService.allocateSeat(testFlight, SeatClass.PREMIUM_ECONOMY);

        // Assert
        assertThat(seatNumber).isNotNull();
        assertThat(seatNumber).matches("^[6-9][A-F]$"); // Rows 6-9, Seats A-F
        assertThat(seatNumber.length()).isEqualTo(2); // e.g., "6A" or "9F"
    }

    @Test
    void allocateSeat_Success_BusinessSeatFormat() {
        // Act
        String seatNumber = seatAllocationService.allocateSeat(testFlight, SeatClass.BUSINESS);

        // Assert
        assertThat(seatNumber).isNotNull();
        assertThat(seatNumber).matches("^[3-5][A-D]$"); // Rows 3-5, Seats A-D
        assertThat(seatNumber.length()).isEqualTo(2); // e.g., "3A" or "5D"
    }

    @Test
    void allocateSeat_Success_FirstClassSeatFormat() {
        // Act
        String seatNumber = seatAllocationService.allocateSeat(testFlight, SeatClass.FIRST_CLASS);

        // Assert
        assertThat(seatNumber).isNotNull();
        assertThat(seatNumber).matches("^[1-2][A-B]$"); // Rows 1-2, Seats A-B
        assertThat(seatNumber.length()).isEqualTo(2); // e.g., "1A" or "2B"
    }

    @Test
    void allocateSeat_Success_DifferentSeatsForDifferentClasses() {
        // Act
        String economySeat = seatAllocationService.allocateSeat(testFlight, SeatClass.ECONOMY);
        String businessSeat = seatAllocationService.allocateSeat(testFlight, SeatClass.BUSINESS);
        String firstClassSeat = seatAllocationService.allocateSeat(testFlight, SeatClass.FIRST_CLASS);

        // Assert
        assertThat(economySeat).isNotEqualTo(businessSeat);
        assertThat(economySeat).isNotEqualTo(firstClassSeat);
        assertThat(businessSeat).isNotEqualTo(firstClassSeat);

        // Verify seat ranges don't overlap
        assertThat(economySeat.charAt(0)).isIn('1', '2', '3'); // Economy starts with 1, 2, or 3 (10-35)
        assertThat(businessSeat.charAt(0)).isIn('3', '4', '5'); // Business is 3-5
        assertThat(firstClassSeat.charAt(0)).isIn('1', '2'); // First class is 1-2
    }
}