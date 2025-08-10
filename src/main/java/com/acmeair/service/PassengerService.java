package com.acmeair.service;

import com.acmeair.model.Passenger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PassengerService {

    public Passenger createPassenger(Passenger passengerData) {
        String passengerId = generatePassengerId();

        return new Passenger(
                passengerId,
                passengerData.getFirstName(),
                passengerData.getLastName(),
                passengerData.getEmail(),
                passengerData.getPhoneNumber(),
                passengerData.getPassportNumber(),
                passengerData.getDateOfBirth()
        );
    }

    private String generatePassengerId() {
        return "P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}