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
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookingDetailsComponentTest {

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
    void getBookingDetails_Failure_BookingNotFound() {
        when(apiTokenService.isValidToken(VALID_API_KEY)).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, VALID_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/api/v1/bookings/INVALID_BOOKING_ID";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Booking not found");
        assertThat(response.getBody().get("message")).isEqualTo("Booking with ID INVALID_BOOKING_ID does not exist");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }
}