package com.acmeair.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightDetailsComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String DEMO_API_KEY = "acme-air-demo-2025-secure-token-12345";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Test
    void getFlightDetails_Success_ReturnsFlightDetails() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, DEMO_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/api/v1/flights/FL001";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isEqualTo("FL001");
        assertThat(response.getBody().get("flightNumber")).isEqualTo("AC101");
        assertThat(response.getBody().get("origin")).isEqualTo("SYD");
        assertThat(response.getBody().get("destination")).isEqualTo("MEL");
    }

    @Test
    void getFlightDetails_Failure_FlightNotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, DEMO_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/api/v1/flights/NONEXISTENT";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Flight not found");
    }
}