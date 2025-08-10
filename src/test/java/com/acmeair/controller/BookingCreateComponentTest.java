package com.acmeair.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingCreateComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String DEMO_API_KEY = "acme-air-demo-2025-secure-token-12345";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Test
    void createBooking_Success_OneWayFlight() {
        String requestBody = """
            {
              "flightType": "ONE_WAY",
              "flightId": "FL001",
              "seatClass": "ECONOMY",
              "passenger": {
                "firstName": "Test",
                "lastName": "User",
                "email": "test@email.com",
                "phoneNumber": "+61123456789",
                "passportNumber": "TEST123",
                "dateOfBirth": "1990-01-01"
              }
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, DEMO_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String url = "http://localhost:" + port + "/api/v1/bookings";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("flightId")).isEqualTo("FL001");
        assertThat(response.getBody().get("status")).isEqualTo("CONFIRMED");
    }

    @Test
    void createBooking_Failure_InvalidFlightId() {
        String requestBody = """
            {
              "flightType": "ONE_WAY",
              "flightId": "INVALID_FLIGHT",
              "seatClass": "ECONOMY",
              "passenger": {
                "firstName": "Test",
                "lastName": "User",
                "email": "test@email.com",
                "phoneNumber": "+61123456789",
                "passportNumber": "TEST123",
                "dateOfBirth": "1990-01-01"
              }
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, DEMO_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String url = "http://localhost:" + port + "/api/v1/bookings";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Flight not found");
    }
}