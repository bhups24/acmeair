package com.acmeair.service;

import com.acmeair.model.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @InjectMocks
    private PassengerService passengerService;

    @Test
    void createPassenger_Success_GeneratesIdAndCopiesData() {
        // Arrange
        Passenger inputPassenger = new Passenger(null, "John", "Doe", "john.doe@email.com",
                "+61412345678", "A1234567", LocalDate.of(1990, 5, 15));

        // Act
        Passenger result = passengerService.createPassenger(inputPassenger);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).startsWith("P");
        assertThat(result.getId()).hasSize(9); // P + 8 characters
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@email.com");
        assertThat(result.getPhoneNumber()).isEqualTo("+61412345678");
        assertThat(result.getPassportNumber()).isEqualTo("A1234567");
        assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    void createPassenger_Success_GeneratesUniqueIds() {
        // Arrange
        Passenger passenger1 = new Passenger(null, "John", "Doe", "john@email.com",
                "+61412345678", "A1234567", LocalDate.of(1990, 5, 15));
        Passenger passenger2 = new Passenger(null, "Jane", "Smith", "jane@email.com",
                "+61423456789", "B2345678", LocalDate.of(1985, 10, 20));

        // Act
        Passenger result1 = passengerService.createPassenger(passenger1);
        Passenger result2 = passengerService.createPassenger(passenger2);

        // Assert
        assertThat(result1.getId()).isNotEqualTo(result2.getId());
        assertThat(result1.getId()).startsWith("P");
        assertThat(result2.getId()).startsWith("P");
    }

    @Test
    void createPassenger_Success_PreservesAllPassengerData() {
        // Arrange
        Passenger inputPassenger = new Passenger(null, "Maria", "Garcia", "maria.garcia@email.com",
                "+61434567890", "C3456789", LocalDate.of(1988, 12, 3));

        // Act
        Passenger result = passengerService.createPassenger(inputPassenger);

        // Assert
        assertThat(result.getFirstName()).isEqualTo(inputPassenger.getFirstName());
        assertThat(result.getLastName()).isEqualTo(inputPassenger.getLastName());
        assertThat(result.getEmail()).isEqualTo(inputPassenger.getEmail());
        assertThat(result.getPhoneNumber()).isEqualTo(inputPassenger.getPhoneNumber());
        assertThat(result.getPassportNumber()).isEqualTo(inputPassenger.getPassportNumber());
        assertThat(result.getDateOfBirth()).isEqualTo(inputPassenger.getDateOfBirth());
    }
}