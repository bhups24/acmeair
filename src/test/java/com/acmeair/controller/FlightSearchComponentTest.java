package com.acmeair.controller;

import com.acmeair.service.ApiTokenService;
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
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightSearchComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private ApiTokenService apiTokenService;

    private static final String VALID_API_KEY = "test-api-key";
    private static final String API_KEY_HEADER = "X-API-Key";

    @Test
    void searchFlights_Success_ReturnsFlightSearchResponse() {
        when(apiTokenService.isValidToken(VALID_API_KEY)).thenReturn(true);

        HttpHeaders headers = new HttpHeaders();
        headers.set(API_KEY_HEADER, VALID_API_KEY);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/api/v1/flights/search" +
                "?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("flightType")).isEqualTo("ONE_WAY");
        assertThat(response.getBody().get("outboundFlights")).isNotNull();
        assertThat(response.getBody().get("totalResults")).isNotNull();
        assertThat(response.getBody().get("currentPage")).isEqualTo(0);
        assertThat(response.getBody().get("pageSize")).isEqualTo(10);
    }

    @Test
    void searchFlights_Failure_MissingApiKey() {
        String url = "http://localhost:" + port + "/api/v1/flights/search" +
                "?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15";

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("error")).isEqualTo("Unauthorized");
        assertThat(response.getBody().get("message")).isEqualTo("API key is required");
    }
}