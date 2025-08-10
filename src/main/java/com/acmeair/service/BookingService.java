package com.acmeair.service;

import com.acmeair.exception.BookingNotFoundException;
import com.acmeair.exception.NoSeatsAvailableException;
import com.acmeair.model.*;
import com.acmeair.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Transactional
public class BookingService {
    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final PassengerService passengerService;
    private final SeatAllocationService seatAllocationService;

    public BookingService(BookingRepository bookingRepository, FlightService flightService,
                          PassengerService passengerService, SeatAllocationService seatAllocationService) {
        this.bookingRepository = bookingRepository;
        this.flightService = flightService;
        this.passengerService = passengerService;
        this.seatAllocationService = seatAllocationService;
    }

    public Booking createBooking(FlightType flightType, String flightId, String returnFlightId,
                                 SeatClass seatClass, Passenger passengerData) {

        if (!flightService.hasAvailableSeats(flightId, seatClass)) {
            throw new NoSeatsAvailableException("No available " + seatClass.getDisplayName() + " seats on flight " + flightId);
        }

        if (flightType == FlightType.RETURN) {
            if (returnFlightId == null || returnFlightId.trim().isEmpty()) {
                throw new IllegalArgumentException("Return flight ID is required for return flights");
            }
            if (!flightService.hasAvailableSeats(returnFlightId, seatClass)) {
                throw new NoSeatsAvailableException("No available " + seatClass.getDisplayName() + " seats on return flight " + returnFlightId);
            }
        }

        Passenger passenger = passengerService.createPassenger(passengerData);
        Flight outboundFlight = flightService.getFlightById(flightId);
        Flight returnFlight = (returnFlightId != null) ? flightService.getFlightById(returnFlightId) : null;
        String seatNumber = seatAllocationService.allocateSeat(outboundFlight, seatClass);
        String returnSeatNumber = (returnFlight != null) ?
                seatAllocationService.allocateSeat(returnFlight, seatClass) : null;
        BigDecimal outboundPrice = flightService.getPrice(flightId, seatClass);
        BigDecimal totalPrice = outboundPrice;

        if (returnFlight != null) {
            BigDecimal returnPrice = flightService.getPrice(returnFlightId, seatClass);
            totalPrice = totalPrice.add(returnPrice);
        }

        Booking booking = new Booking(
                generateBookingId(),
                flightId,
                returnFlightId,
                passenger,
                ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime(),
                BookingStatus.CONFIRMED,
                seatClass,
                flightType,
                seatNumber,
                returnSeatNumber,
                totalPrice
        );

        flightService.updateAvailableSeats(flightId, seatClass, 1);
        if (returnFlightId != null) {
            flightService.updateAvailableSeats(returnFlightId, seatClass, 1);
        }

        return bookingRepository.save(booking);
    }

    public Booking updatePassengerDetails(String bookingId, Passenger updatedPassenger) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " does not exist"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update passenger details on a cancelled booking");
        }

        Passenger passenger = booking.getPassenger();
        passenger.setFirstName(updatedPassenger.getFirstName());
        passenger.setLastName(updatedPassenger.getLastName());
        passenger.setEmail(updatedPassenger.getEmail());
        passenger.setPhoneNumber(updatedPassenger.getPhoneNumber());
        passenger.setPassportNumber(updatedPassenger.getPassportNumber());
        passenger.setDateOfBirth(updatedPassenger.getDateOfBirth());

        return bookingRepository.save(booking);
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + bookingId + " does not exist"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        flightService.increaseAvailableSeats(booking.getFlightId(), booking.getSeatClass(), 1);
        if (booking.getReturnFlightId() != null) {
            flightService.increaseAvailableSeats(booking.getReturnFlightId(), booking.getSeatClass(), 1);
        }
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking with ID " + id + " does not exist"));
    }

    private String generateBookingId() {
        return "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}