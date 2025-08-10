package com.acmeair.service;

import com.acmeair.exception.BookingNotFoundException;
import com.acmeair.exception.NoSeatsAvailableException;
import com.acmeair.model.*;
import com.acmeair.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private PassengerService passengerService;

    @Mock
    private SeatAllocationService seatAllocationService;

    @InjectMocks
    private BookingService bookingService;

    private Passenger testPassenger;
    private Flight testFlight;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        testPassenger = new Passenger("P12345678", "John", "Doe", "john.doe@email.com",
                "+61412345678", "A1234567", LocalDate.of(1990, 5, 15));

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

        testBooking = new Booking("BK12345678", "FL001", null, testPassenger,
                LocalDateTime.now(), BookingStatus.CONFIRMED, SeatClass.ECONOMY,
                FlightType.ONE_WAY, "12A", null, new BigDecimal("199.99"));
    }

    @Test
    void createBooking_Success_OneWayFlight() {
        // Arrange
        when(flightService.hasAvailableSeats("FL001", SeatClass.ECONOMY)).thenReturn(true);
        when(passengerService.createPassenger(any(Passenger.class))).thenReturn(testPassenger);
        when(flightService.getFlightById("FL001")).thenReturn(testFlight);
        when(seatAllocationService.allocateSeat(testFlight, SeatClass.ECONOMY)).thenReturn("12A");
        when(flightService.getPrice("FL001", SeatClass.ECONOMY)).thenReturn(new BigDecimal("199.99"));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        Booking result = bookingService.createBooking(FlightType.ONE_WAY, "FL001", null,
                SeatClass.ECONOMY, testPassenger);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFlightId()).isEqualTo("FL001");
        assertThat(result.getFlightType()).isEqualTo(FlightType.ONE_WAY);
        assertThat(result.getSeatClass()).isEqualTo(SeatClass.ECONOMY);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);

        verify(flightService).updateAvailableSeats("FL001", SeatClass.ECONOMY, 1);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_Success_ReturnFlight() {
        // Arrange
        when(flightService.hasAvailableSeats("FL001", SeatClass.ECONOMY)).thenReturn(true);
        when(flightService.hasAvailableSeats("FL025", SeatClass.ECONOMY)).thenReturn(true);
        when(passengerService.createPassenger(any(Passenger.class))).thenReturn(testPassenger);
        when(flightService.getFlightById("FL001")).thenReturn(testFlight);
        when(flightService.getFlightById("FL025")).thenReturn(testFlight);
        when(seatAllocationService.allocateSeat(any(Flight.class), eq(SeatClass.ECONOMY)))
                .thenReturn("12A").thenReturn("15B");
        when(flightService.getPrice("FL001", SeatClass.ECONOMY)).thenReturn(new BigDecimal("199.99"));
        when(flightService.getPrice("FL025", SeatClass.ECONOMY)).thenReturn(new BigDecimal("209.99"));

        Booking returnBooking = new Booking("BK87654321", "FL001", "FL025", testPassenger,
                LocalDateTime.now(), BookingStatus.CONFIRMED, SeatClass.ECONOMY,
                FlightType.RETURN, "12A", "15B", new BigDecimal("409.98"));
        when(bookingRepository.save(any(Booking.class))).thenReturn(returnBooking);

        // Act
        Booking result = bookingService.createBooking(FlightType.RETURN, "FL001", "FL025",
                SeatClass.ECONOMY, testPassenger);

        // Assert
        assertThat(result.getFlightType()).isEqualTo(FlightType.RETURN);
        assertThat(result.getReturnFlightId()).isEqualTo("FL025");
        assertThat(result.getTotalPrice()).isEqualTo(new BigDecimal("409.98"));

        verify(flightService).updateAvailableSeats("FL001", SeatClass.ECONOMY, 1);
        verify(flightService).updateAvailableSeats("FL025", SeatClass.ECONOMY, 1);
    }

    @Test
    void createBooking_Failure_NoSeatsAvailable() {
        // Arrange
        when(flightService.hasAvailableSeats("FL001", SeatClass.ECONOMY)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> bookingService.createBooking(FlightType.ONE_WAY, "FL001", null,
                SeatClass.ECONOMY, testPassenger))
                .isInstanceOf(NoSeatsAvailableException.class)
                .hasMessage("No available Economy seats on flight FL001");

        verify(flightService, never()).updateAvailableSeats(anyString(), any(SeatClass.class), anyInt());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void cancelBooking_Success_CancelsBookingAndRestoresSeats() {
        // Arrange
        when(bookingRepository.findById("BK12345678")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        bookingService.cancelBooking("BK12345678");

        // Assert
        assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(flightService).increaseAvailableSeats("FL001", SeatClass.ECONOMY, 1);
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void cancelBooking_Failure_BookingNotFound() {
        // Arrange
        when(bookingRepository.findById("INVALID_ID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.cancelBooking("INVALID_ID"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("Booking with ID INVALID_ID does not exist");

        verify(flightService, never()).increaseAvailableSeats(anyString(), any(SeatClass.class), anyInt());
    }

    @Test
    void cancelBooking_Failure_AlreadyCancelled() {
        // Arrange
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById("BK12345678")).thenReturn(Optional.of(testBooking));

        // Act & Assert
        assertThatThrownBy(() -> bookingService.cancelBooking("BK12345678"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking is already cancelled");

        verify(flightService, never()).increaseAvailableSeats(anyString(), any(SeatClass.class), anyInt());
    }

    @Test
    void getBookingById_Success_ReturnsBookingWhenExists() {
        // Arrange
        when(bookingRepository.findById("BK12345678")).thenReturn(Optional.of(testBooking));

        // Act
        Booking result = bookingService.getBookingById("BK12345678");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("BK12345678");
        assertThat(result.getFlightId()).isEqualTo("FL001");
        verify(bookingRepository).findById("BK12345678");
    }

    @Test
    void getBookingById_Failure_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(bookingRepository.findById("INVALID_ID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> bookingService.getBookingById("INVALID_ID"))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("Booking with ID INVALID_ID does not exist");
        verify(bookingRepository).findById("INVALID_ID");
    }

    @Test
    void updatePassengerDetails_Success_UpdatesPassengerInformation() {
        // Arrange
        Passenger updatedPassengerData = new Passenger(null, "John", "Smith", "john.smith@email.com",
                "+61423456789", "B2345678", LocalDate.of(1990, 5, 15));

        when(bookingRepository.findById("BK12345678")).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        Booking result = bookingService.updatePassengerDetails("BK12345678", updatedPassengerData);

        // Assert
        assertThat(result.getPassenger().getLastName()).isEqualTo("Smith");
        assertThat(result.getPassenger().getEmail()).isEqualTo("john.smith@email.com");
        assertThat(result.getPassenger().getPhoneNumber()).isEqualTo("+61423456789");
        assertThat(result.getPassenger().getPassportNumber()).isEqualTo("B2345678");
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void updatePassengerDetails_Failure_CancelledBooking() {
        // Arrange
        testBooking.setStatus(BookingStatus.CANCELLED);
        when(bookingRepository.findById("BK12345678")).thenReturn(Optional.of(testBooking));

        Passenger updatedPassengerData = new Passenger(null, "John", "Smith", "john.smith@email.com",
                "+61423456789", "B2345678", LocalDate.of(1990, 5, 15));

        // Act & Assert
        assertThatThrownBy(() -> bookingService.updatePassengerDetails("BK12345678", updatedPassengerData))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update passenger details on a cancelled booking");

        verify(bookingRepository, never()).save(any(Booking.class));
    }
}