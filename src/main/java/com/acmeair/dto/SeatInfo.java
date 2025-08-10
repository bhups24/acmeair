package com.acmeair.dto;

import java.math.BigDecimal;

public class SeatInfo {
    private BigDecimal price;
    private int availableSeats;
    private int totalSeats;

    public SeatInfo() {}

    public SeatInfo(BigDecimal price, int availableSeats, int totalSeats) {
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}