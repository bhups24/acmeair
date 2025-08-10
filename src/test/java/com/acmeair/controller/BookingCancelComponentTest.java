package com.acmeair.controller;

import com.acmeair.dto.BookingRequest;
import com.acmeair.model.FlightType;
import com.acmeair.model.Passenger;
import com.acmeair.model.SeatClass;
import com.acmeair.service.ApiTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingCancelComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApiTokenService apiTokenService;

    private static final String VALID_API_KEY = "test-api-key";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Test
    void cancelBooking_Success_CancelsBookingAndReturnsDetails() throws Exception {
        when(apiTokenService.isValidToken(VALID_API_KEY)).thenReturn(true);

        Passenger passenger = new Passenger(null, "Cancel", "Test", "cancel.test@email.com",
                "+61456789012", "F6789012", LocalDate.of(1985, 8, 20));

        BookingRequest bookingRequest = new BookingRequest(
                FlightType.ONE_WAY, "FL001", null, SeatClass.PREMIUM_ECONOMY, passenger);

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, VALID_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> createEntity = new HttpEntity<>(objectMapper.writeValueAsString(bookingRequest), headers);

        String createUrl = "http://localhost:" + port + "/api/v1/bookings";
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createEntity, Map.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String bookingId = (String) createResponse.getBody().get("id");

        HttpEntity<String> deleteEntity = new HttpEntity<>(headers);
        String deleteUrl = "http://localhost:" + port + "/api/v1/bookings/" + bookingId;

        ResponseEntity<Map> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteEntity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("message")).asString().contains(bookingId);
        assertThat(response.getBody().get("bookingId")).isEqualTo(bookingId);
        assertThat(response.getBody().get("refundAmount")).isNotNull();
    }

    @Test
    void cancelBooking_Failure_BookingNotFound() {
        when(apiTokenService.isValidToken(VALID_API_KEY)).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, VALID_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/api/v1/bookings/INVALID_BOOKING_ID";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Booking not found");
        assertThat(response.getBody().get("message")).isEqualTo("Booking with ID INVALID_BOOKING_ID does not exist");
    }
}