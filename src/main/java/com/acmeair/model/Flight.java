package com.acmeair.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    @Column(name = "flight_number")
    private String flightNumber;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @NotNull
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @NotBlank
    private String aircraft;

    @NotNull
    @Positive
    @Column(name = "economy_price")
    private BigDecimal economyPrice;

    @NotNull
    @Positive
    @Column(name = "premium_economy_price")
    private BigDecimal premiumEconomyPrice;

    @NotNull
    @Positive
    @Column(name = "business_price")
    private BigDecimal businessPrice;

    @NotNull
    @Positive
    @Column(name = "first_class_price")
    private BigDecimal firstClassPrice;

    @PositiveOrZero
    @Column(name = "economy_available")
    private int economyAvailable;

    @PositiveOrZero
    @Column(name = "premium_economy_available")
    private int premiumEconomyAvailable;

    @PositiveOrZero
    @Column(name = "business_available")
    private int businessAvailable;

    @PositiveOrZero
    @Column(name = "first_class_available")
    private int firstClassAvailable;

    @Positive
    @Column(name = "economy_total")
    private int economyTotal;

    @Positive
    @Column(name = "premium_economy_total")
    private int premiumEconomyTotal;

    @Positive
    @Column(name = "business_total")
    private int businessTotal;

    @Positive
    @Column(name = "first_class_total")
    private int firstClassTotal;

    @Column(name = "is_direct")
    private boolean isDirect = true;

    @Column(name = "stops")
    private int stops = 0;

    public Flight() {}

    public Flight(String id, String flightNumber, String origin, String destination,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, String aircraft,
                  BigDecimal economyPrice, BigDecimal premiumEconomyPrice, BigDecimal businessPrice, BigDecimal firstClassPrice,
                  int economyAvailable, int premiumEconomyAvailable, int businessAvailable, int firstClassAvailable,
                  int economyTotal, int premiumEconomyTotal, int businessTotal, int firstClassTotal,
                  boolean isDirect, int stops) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = aircraft;
        this.economyPrice = economyPrice;
        this.premiumEconomyPrice = premiumEconomyPrice;
        this.businessPrice = businessPrice;
        this.firstClassPrice = firstClassPrice;
        this.economyAvailable = economyAvailable;
        this.premiumEconomyAvailable = premiumEconomyAvailable;
        this.businessAvailable = businessAvailable;
        this.firstClassAvailable = firstClassAvailable;
        this.economyTotal = economyTotal;
        this.premiumEconomyTotal = premiumEconomyTotal;
        this.businessTotal = businessTotal;
        this.firstClassTotal = firstClassTotal;
        this.isDirect = isDirect;
        this.stops = stops;
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

    public BigDecimal getEconomyPrice() { return economyPrice; }
    public void setEconomyPrice(BigDecimal economyPrice) { this.economyPrice = economyPrice; }

    public BigDecimal getPremiumEconomyPrice() { return premiumEconomyPrice; }
    public void setPremiumEconomyPrice(BigDecimal premiumEconomyPrice) { this.premiumEconomyPrice = premiumEconomyPrice; }

    public BigDecimal getBusinessPrice() { return businessPrice; }
    public void setBusinessPrice(BigDecimal businessPrice) { this.businessPrice = businessPrice; }

    public BigDecimal getFirstClassPrice() { return firstClassPrice; }
    public void setFirstClassPrice(BigDecimal firstClassPrice) { this.firstClassPrice = firstClassPrice; }

    public int getEconomyAvailable() { return economyAvailable; }
    public void setEconomyAvailable(int economyAvailable) { this.economyAvailable = economyAvailable; }

    public int getPremiumEconomyAvailable() { return premiumEconomyAvailable; }
    public void setPremiumEconomyAvailable(int premiumEconomyAvailable) { this.premiumEconomyAvailable = premiumEconomyAvailable; }

    public int getBusinessAvailable() { return businessAvailable; }
    public void setBusinessAvailable(int businessAvailable) { this.businessAvailable = businessAvailable; }

    public int getFirstClassAvailable() { return firstClassAvailable; }
    public void setFirstClassAvailable(int firstClassAvailable) { this.firstClassAvailable = firstClassAvailable; }

    public int getEconomyTotal() { return economyTotal; }
    public void setEconomyTotal(int economyTotal) { this.economyTotal = economyTotal; }

    public int getPremiumEconomyTotal() { return premiumEconomyTotal; }
    public void setPremiumEconomyTotal(int premiumEconomyTotal) { this.premiumEconomyTotal = premiumEconomyTotal; }

    public int getBusinessTotal() { return businessTotal; }
    public void setBusinessTotal(int businessTotal) { this.businessTotal = businessTotal; }

    public int getFirstClassTotal() { return firstClassTotal; }
    public void setFirstClassTotal(int firstClassTotal) { this.firstClassTotal = firstClassTotal; }

    public boolean isDirect() { return isDirect; }
    public void setDirect(boolean direct) { isDirect = direct; }

    public int getStops() { return stops; }
    public void setStops(int stops) { this.stops = stops; }

    public int getTotalAvailableSeats() {
        return economyAvailable + premiumEconomyAvailable + businessAvailable + firstClassAvailable;
    }

    public int getTotalSeats() {
        return economyTotal + premiumEconomyTotal + businessTotal + firstClassTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return Objects.equals(id, flight.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}