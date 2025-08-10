package com.acmeair.repository;

import com.acmeair.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("BookingRepository Tests")
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private Flight testFlight;
    private Flight returnFlight;
    private Passenger testPassenger;
    private Booking oneWayBooking;
    private Booking returnBooking;
    private Booking cancelledBooking;

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

        returnFlight = new Flight(
                "FL025", "AC107", "MEL", "SYD",
                LocalDateTime.of(2025, 8, 16, 9, 0),
                LocalDateTime.of(2025, 8, 16, 10, 30),
                "Boeing 737",
                new BigDecimal("209.99"), new BigDecimal("309.99"),
                new BigDecimal("609.99"), new BigDecimal("1009.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                true, 0
        );

        testPassenger = new Passenger(
                "P12345678", "John", "Doe", "john.doe@email.com",
                "+61412345678", "A1234567", LocalDate.of(1990, 5, 15)
        );

        Passenger returnPassenger = new Passenger(
                "P87654321", "Jane", "Smith", "jane.smith@email.com",
                "+61487654321", "B2345678", LocalDate.of(1985, 12, 20)
        );

        Passenger cancelledPassenger = new Passenger(
                "P11111111", "Bob", "Johnson", "bob.johnson@email.com",
                "+61411111111", "C3456789", LocalDate.of(1988, 3, 10)
        );

        oneWayBooking = new Booking(
                "BK12345678", "FL001", null, testPassenger,
                LocalDateTime.of(2025, 8, 10, 14, 30, 0),
                BookingStatus.CONFIRMED, SeatClass.ECONOMY, FlightType.ONE_WAY,
                "12A", null, new BigDecimal("199.99")
        );

        returnBooking = new Booking(
                "BK87654321", "FL001", "FL025", returnPassenger,
                LocalDateTime.of(2025, 8, 10, 15, 45, 0),
                BookingStatus.CONFIRMED, SeatClass.BUSINESS, FlightType.RETURN,
                "3A", "4B", new BigDecimal("1209.98")
        );

        cancelledBooking = new Booking(
                "BK11111111", "FL001", null, cancelledPassenger,
                LocalDateTime.of(2025, 8, 9, 10, 15, 0),
                BookingStatus.CANCELLED, SeatClass.PREMIUM_ECONOMY, FlightType.ONE_WAY,
                "8C", null, new BigDecimal("299.99")
        );

        entityManager.persistAndFlush(testFlight);
        entityManager.persistAndFlush(returnFlight);
        entityManager.persistAndFlush(oneWayBooking);
        entityManager.persistAndFlush(returnBooking);
        entityManager.persistAndFlush(cancelledBooking);
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save a new booking successfully")
        void shouldSaveNewBookingSuccessfully() {
            Passenger newPassenger = new Passenger(
                    "P99999999", "Jane", "Smith", "jane.smith@email.com",
                    "+61487654321", "B9876543", LocalDate.of(1985, 12, 20)
            );
            entityManager.persistAndFlush(newPassenger);

            Booking newBooking = new Booking(
                    "BK99999999", "FL001", null, newPassenger,
                    LocalDateTime.now(), BookingStatus.CONFIRMED, SeatClass.FIRST_CLASS,
                    FlightType.ONE_WAY, "1A", null, new BigDecimal("999.99")
            );

            Booking savedBooking = bookingRepository.save(newBooking);

            assertThat(savedBooking).isNotNull();
            assertThat(savedBooking.getId()).isEqualTo("BK99999999");
            assertThat(savedBooking.getFlightId()).isEqualTo("FL001");
            assertThat(savedBooking.getPassenger().getFirstName()).isEqualTo("Jane");
            assertThat(savedBooking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
            assertThat(savedBooking.getTotalPrice()).isEqualTo(new BigDecimal("999.99"));
        }

        @Test
        @DisplayName("Should find booking by ID when it exists")
        void shouldFindBookingByIdWhenExists() {
            Optional<Booking> result = bookingRepository.findById("BK12345678");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("BK12345678");
            assertThat(result.get().getFlightId()).isEqualTo("FL001");
            assertThat(result.get().getFlightType()).isEqualTo(FlightType.ONE_WAY);
            assertThat(result.get().getSeatClass()).isEqualTo(SeatClass.ECONOMY);
            assertThat(result.get().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
            assertThat(result.get().getSeatNumber()).isEqualTo("12A");
            assertThat(result.get().getReturnFlightId()).isNull();
            assertThat(result.get().getReturnSeatNumber()).isNull();
        }

        @Test
        @DisplayName("Should return empty when booking ID does not exist")
        void shouldReturnEmptyWhenBookingIdDoesNotExist() {
            Optional<Booking> result = bookingRepository.findById("NONEXISTENT");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should update existing booking successfully")
        void shouldUpdateExistingBookingSuccessfully() {
            Booking booking = bookingRepository.findById("BK12345678").orElseThrow();
            booking.setStatus(BookingStatus.CANCELLED);
            booking.getPassenger().setEmail("updated.email@example.com");

            Booking updatedBooking = bookingRepository.save(booking);

            assertThat(updatedBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
            assertThat(updatedBooking.getPassenger().getEmail()).isEqualTo("updated.email@example.com");

            Optional<Booking> retrieved = bookingRepository.findById("BK12345678");
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().getStatus()).isEqualTo(BookingStatus.CANCELLED);
            assertThat(retrieved.get().getPassenger().getEmail()).isEqualTo("updated.email@example.com");
        }

        @Test
        @DisplayName("Should delete booking by ID successfully")
        void shouldDeleteBookingByIdSuccessfully() {
            assertThat(bookingRepository.existsById("BK12345678")).isTrue();

            bookingRepository.deleteById("BK12345678");

            assertThat(bookingRepository.existsById("BK12345678")).isFalse();
            assertThat(bookingRepository.findById("BK12345678")).isEmpty();
        }

        @Test
        @DisplayName("Should delete booking entity successfully")
        void shouldDeleteBookingEntitySuccessfully() {
            Booking booking = bookingRepository.findById("BK87654321").orElseThrow();

            bookingRepository.delete(booking);

            assertThat(bookingRepository.existsById("BK87654321")).isFalse();
            assertThat(bookingRepository.count()).isEqualTo(2);
        }
    }
}