package com.acmeair.dto;

import com.acmeair.model.FlightType;
import com.acmeair.model.Passenger;
import com.acmeair.model.SeatClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidReturnFlight
public class BookingRequest {
    @NotNull(message = "Flight type is required")
    private FlightType flightType;

    @NotBlank(message = "Flight ID is required")
    private String flightId;

    private String returnFlightId;

    @NotNull(message = "Seat class is required")
    private SeatClass seatClass;

    @NotNull(message = "Passenger details are required")
    @Valid
    private Passenger passenger;

    public BookingRequest() {}

    public BookingRequest(FlightType flightType, String flightId, String returnFlightId,
                          SeatClass seatClass, Passenger passenger) {
        this.flightType = flightType;
        this.flightId = flightId;
        this.returnFlightId = returnFlightId;
        this.seatClass = seatClass;
        this.passenger = passenger;
    }

    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }

    public String getFlightId() { return flightId; }
    public void setFlightId(String flightId) { this.flightId = flightId; }

    public String getReturnFlightId() { return returnFlightId; }
    public void setReturnFlightId(String returnFlightId) { this.returnFlightId = returnFlightId; }

    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
}