package com.acmeair.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    private String id;

    @NotBlank(message = "Flight ID is required")
    @Column(name = "flight_id")
    private String flightId;

    @Column(name = "return_flight_id")
    private String returnFlightId;

    @NotNull(message = "Passenger details are required")
    @Valid
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;

    @Column(name = "booking_time")
    private LocalDateTime bookingTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class")
    private SeatClass seatClass;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "flight_type")
    private FlightType flightType;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "return_seat_number")
    private String returnSeatNumber;

    @NotNull
    @Positive
    @Column(name = "total_price")
    private BigDecimal totalPrice;

    public Booking() {}

    public Booking(String id, String flightId, String returnFlightId, Passenger passenger,
                   LocalDateTime bookingTime, BookingStatus status, SeatClass seatClass,
                   FlightType flightType, String seatNumber, String returnSeatNumber,
                   BigDecimal totalPrice) {
        this.id = id;
        this.flightId = flightId;
        this.returnFlightId = returnFlightId;
        this.passenger = passenger;
        this.bookingTime = bookingTime;
        this.status = status;
        this.seatClass = seatClass;
        this.flightType = flightType;
        this.seatNumber = seatNumber;
        this.returnSeatNumber = returnSeatNumber;
        this.totalPrice = totalPrice;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getReturnFlightId() { return returnFlightId; }
    public void setReturnFlightId(String returnFlightId) { this.returnFlightId = returnFlightId; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }

    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getReturnSeatNumber() { return returnSeatNumber; }
    public void setReturnSeatNumber(String returnSeatNumber) { this.returnSeatNumber = returnSeatNumber; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}