package com.acmeair.controller;

import com.acmeair.dto.BookingRequest;
import com.acmeair.model.Booking;
import com.acmeair.model.Passenger;
import com.acmeair.service.BookingService;
import com.acmeair.service.BookingValidatorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final BookingValidatorService bookingValidatorService;

    public BookingController(BookingService bookingService, BookingValidatorService bookingValidatorService) {
        this.bookingService = bookingService;
        this.bookingValidatorService = bookingValidatorService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        bookingValidatorService.validateBookingRequest(bookingRequest);

        Booking booking = bookingService.createBooking(
                bookingRequest.getFlightType(),
                bookingRequest.getFlightId(),
                bookingRequest.getReturnFlightId(),
                bookingRequest.getSeatClass(),
                bookingRequest.getPassenger()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{bookingId}/passenger")
    public ResponseEntity<Booking> updatePassengerDetails(
            @PathVariable @NotBlank(message = "Booking ID is required") String bookingId,
            @Valid @RequestBody Passenger passenger) {

        Booking updatedBooking = bookingService.updatePassengerDetails(bookingId, passenger);
        return ResponseEntity.ok(updatedBooking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            @PathVariable @NotBlank(message = "Booking ID is required") String bookingId) {

        Booking booking = bookingService.getBookingById(bookingId);
        bookingService.cancelBooking(bookingId);

        Map<String, Object> response = Map.of(
                "message", "Booking " + bookingId + " has been successfully cancelled",
                "bookingId", bookingId,
                "flightType", booking.getFlightType().getDisplayName(),
                "seatClass", booking.getSeatClass().getDisplayName(),
                "refundAmount", booking.getTotalPrice()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingDetails(
            @PathVariable @NotBlank(message = "Booking ID is required") String bookingId) {

        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(booking);
    }
}