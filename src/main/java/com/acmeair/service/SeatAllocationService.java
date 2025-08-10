package com.acmeair.service;

import com.acmeair.model.Flight;
import com.acmeair.model.SeatClass;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SeatAllocationService {
    private final Random random = new Random();

    public String allocateSeat(Flight flight, SeatClass seatClass) {
        return switch (seatClass) {
            case ECONOMY -> allocateEconomySeat();
            case PREMIUM_ECONOMY -> allocatePremiumEconomySeat();
            case BUSINESS -> allocateBusinessSeat();
            case FIRST_CLASS -> allocateFirstClassSeat();
        };
    }

    private String allocateEconomySeat() {
        int row = random.nextInt(26) + 10;
        char seat = (char) ('A' + random.nextInt(6));
        return row + String.valueOf(seat);
    }

    private String allocatePremiumEconomySeat() {
        int row = random.nextInt(4) + 6;
        char seat = (char) ('A' + random.nextInt(6));
        return row + String.valueOf(seat);
    }

    private String allocateBusinessSeat() {
        int row = random.nextInt(3) + 3;
        char seat = (char) ('A' + random.nextInt(4));
        return row + String.valueOf(seat);
    }

    private String allocateFirstClassSeat() {
        int row = random.nextInt(2) + 1;
        char seat = (char) ('A' + random.nextInt(2));
        return row + String.valueOf(seat);
    }
}