package com.acmeair.dto;

import com.acmeair.model.Flight;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class FlightResponseDto {
    private String id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String aircraft;
    private SeatClasses seats;
    private int stops;

    @JsonProperty("direct")
    private boolean isDirect;

    private int totalSeats;
    private int totalAvailableSeats;

    public FlightResponseDto() {}

    public FlightResponseDto(Flight flight) {
        this.id = flight.getId();
        this.flightNumber = flight.getFlightNumber();
        this.origin = flight.getOrigin();
        this.destination = flight.getDestination();
        this.departureTime = flight.getDepartureTime();
        this.arrivalTime = flight.getArrivalTime();
        this.aircraft = flight.getAircraft();
        this.stops = flight.getStops();
        this.isDirect = flight.isDirect();

        this.seats = new SeatClasses(
                new SeatInfo(flight.getEconomyPrice(), flight.getEconomyAvailable(), flight.getEconomyTotal()),
                new SeatInfo(flight.getPremiumEconomyPrice(), flight.getPremiumEconomyAvailable(), flight.getPremiumEconomyTotal()),
                new SeatInfo(flight.getBusinessPrice(), flight.getBusinessAvailable(), flight.getBusinessTotal()),
                new SeatInfo(flight.getFirstClassPrice(), flight.getFirstClassAvailable(), flight.getFirstClassTotal())
        );

        this.totalSeats = flight.getTotalSeats();
        this.totalAvailableSeats = flight.getTotalAvailableSeats();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getAircraft() { return aircraft; }
    public void setAircraft(String aircraft) { this.aircraft = aircraft; }

    public SeatClasses getSeats() { return seats; }
    public void setSeats(SeatClasses seats) { this.seats = seats; }

    public int getStops() { return stops; }
    public void setStops(int stops) { this.stops = stops; }

    public boolean isDirect() { return isDirect; }
    public void setDirect(boolean direct) { isDirect = direct; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getTotalAvailableSeats() { return totalAvailableSeats; }
    public void setTotalAvailableSeats(int totalAvailableSeats) { this.totalAvailableSeats = totalAvailableSeats; }
}