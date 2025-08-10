package com.acmeair.repository;

import com.acmeair.model.Flight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("FlightRepository Tests")
class FlightRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightRepository flightRepository;

    private Flight sydMelDirectFlight;
    private Flight sydMelConnectingFlight;
    private Flight sydBneFlight;
    private Flight melSydFlight;
    private Flight expensiveFlight;
    private Flight cheapFlight;

    @BeforeEach
    void setUp() {
        // SYD to MEL Direct Flight
        sydMelDirectFlight = new Flight(
                "FL001", "AC101", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 6, 0),
                LocalDateTime.of(2025, 8, 15, 7, 30),
                "Boeing 737",
                new BigDecimal("199.99"), new BigDecimal("299.99"),
                new BigDecimal("599.99"), new BigDecimal("999.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                true, 0
        );

        // SYD to MEL Connecting Flight (with stop)
        sydMelConnectingFlight = new Flight(
                "FL006", "AC106", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 21, 0),
                LocalDateTime.of(2025, 8, 16, 1, 0),
                "Boeing 737",
                new BigDecimal("179.99"), new BigDecimal("279.99"),
                new BigDecimal("579.99"), new BigDecimal("979.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                false, 1
        );

        // SYD to BNE Flight (different destination)
        sydBneFlight = new Flight(
                "FL010", "AC301", "SYD", "BNE",
                LocalDateTime.of(2025, 8, 15, 8, 0),
                LocalDateTime.of(2025, 8, 15, 10, 20),
                "Airbus A320",
                new BigDecimal("159.99"), new BigDecimal("239.99"),
                new BigDecimal("489.99"), new BigDecimal("799.99"),
                100, 20, 12, 2,
                100, 20, 12, 2,
                true, 0
        );

        // MEL to SYD Flight (reverse route)
        melSydFlight = new Flight(
                "FL007", "AC201", "MEL", "SYD",
                LocalDateTime.of(2025, 8, 15, 7, 0),
                LocalDateTime.of(2025, 8, 15, 8, 30),
                "Boeing 737",
                new BigDecimal("189.99"), new BigDecimal("289.99"),
                new BigDecimal("589.99"), new BigDecimal("989.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                true, 0
        );

        // Expensive SYD to MEL Flight
        expensiveFlight = new Flight(
                "FL999", "AC999", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 18, 0),
                LocalDateTime.of(2025, 8, 15, 19, 30),
                "Boeing 787",
                new BigDecimal("899.99"), new BigDecimal("1299.99"),
                new BigDecimal("2599.99"), new BigDecimal("4999.99"),
                200, 40, 32, 8,
                200, 40, 32, 8,
                true, 0
        );

        // Very Cheap SYD to MEL Flight
        cheapFlight = new Flight(
                "FL028", "AC109", "SYD", "MEL",
                LocalDateTime.of(2025, 8, 15, 22, 0),
                LocalDateTime.of(2025, 8, 15, 23, 30),
                "Boeing 737",
                new BigDecimal("129.99"), new BigDecimal("229.99"),
                new BigDecimal("529.99"), new BigDecimal("929.99"),
                120, 24, 16, 4,
                120, 24, 16, 4,
                true, 0
        );

        // Persist all flights
        entityManager.persistAndFlush(sydMelDirectFlight);
        entityManager.persistAndFlush(sydMelConnectingFlight);
        entityManager.persistAndFlush(sydBneFlight);
        entityManager.persistAndFlush(melSydFlight);
        entityManager.persistAndFlush(expensiveFlight);
        entityManager.persistAndFlush(cheapFlight);
    }

    @Nested
    @DisplayName("Basic Flight Search Tests")
    class BasicFlightSearchTests {

        @Test
        @DisplayName("Should find flights by origin and destination")
        void shouldFindFlightsByOriginAndDestination() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then
            assertThat(results.getContent()).hasSize(4); // All SYD->MEL flights
            assertThat(results.getContent()).extracting(Flight::getOrigin).containsOnly("SYD");
            assertThat(results.getContent()).extracting(Flight::getDestination).containsOnly("MEL");
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL006", "FL999", "FL028");
        }

        @Test
        @DisplayName("Should handle case-insensitive airport codes")
        void shouldHandleCaseInsensitiveAirportCodes() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Using lowercase airport codes
            Page<Flight> results = flightRepository.findFlights(
                    "syd", "mel", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then
            assertThat(results.getContent()).hasSize(4);
            assertThat(results.getContent()).extracting(Flight::getOrigin).containsOnly("SYD");
            assertThat(results.getContent()).extracting(Flight::getDestination).containsOnly("MEL");
        }

        @Test
        @DisplayName("Should return empty result for non-matching routes")
        void shouldReturnEmptyForNonMatchingRoutes() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Searching for non-existent route
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "PER", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then
            assertThat(results.getContent()).isEmpty();
            assertThat(results.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("Should filter flights by date range")
        void shouldFilterFlightsByDateRange() {
            // Given - Create flight on different day
            Flight nextDayFlight = new Flight(
                    "FL100", "AC100", "SYD", "MEL",
                    LocalDateTime.of(2025, 8, 16, 6, 0), // Next day
                    LocalDateTime.of(2025, 8, 16, 7, 30),
                    "Boeing 737",
                    new BigDecimal("199.99"), new BigDecimal("299.99"),
                    new BigDecimal("599.99"), new BigDecimal("999.99"),
                    120, 24, 16, 4,
                    120, 24, 16, 4,
                    true, 0
            );
            entityManager.persistAndFlush(nextDayFlight);

            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0); // Excludes next day
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then - Should not include next day flight
            assertThat(results.getContent()).hasSize(4);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .doesNotContain("FL100");
        }
    }

    @Nested
    @DisplayName("Price Filter Tests")
    class PriceFilterTests {

        @Test
        @DisplayName("Should filter flights by minimum price")
        void shouldFilterFlightsByMinimumPrice() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter by minimum price of 180 (excludes cheap flight at 129.99 and connecting at 179.99)
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    new BigDecimal("180.00"), null, null, pageable
            );

            // Then
            assertThat(results.getContent()).hasSize(2);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL999");
            assertThat(results.getContent()).allSatisfy(flight ->
                    assertThat(flight.getEconomyPrice()).isGreaterThanOrEqualTo(new BigDecimal("180.00"))
            );
        }

        @Test
        @DisplayName("Should filter flights by maximum price")
        void shouldFilterFlightsByMaximumPrice() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter by maximum price of 200 (excludes expensive flight at 899.99)
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, new BigDecimal("200.00"), null, pageable
            );

            // Then
            assertThat(results.getContent()).hasSize(3);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL006", "FL028");
            assertThat(results.getContent()).allSatisfy(flight ->
                    assertThat(flight.getEconomyPrice()).isLessThanOrEqualTo(new BigDecimal("200.00"))
            );
        }

        @Test
        @DisplayName("Should filter flights by price range")
        void shouldFilterFlightsByPriceRange() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter by price range 175-205 (should include FL001 at 199.99 and FL006 at 179.99)
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    new BigDecimal("175.00"), new BigDecimal("205.00"), null, pageable
            );

            // Then
            assertThat(results.getContent()).hasSize(2);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL006");
        }

        @Test
        @DisplayName("Should return empty result when no flights match price range")
        void shouldReturnEmptyWhenNoPriceMatch() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter by price range that no flights match
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    new BigDecimal("1000.00"), new BigDecimal("1500.00"), null, pageable
            );

            // Then
            assertThat(results.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Should handle null price filters correctly")
        void shouldHandleNullPriceFilters() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Both price filters are null
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then - Should return all matching flights regardless of price
            assertThat(results.getContent()).hasSize(4);
        }
    }

    @Nested
    @DisplayName("Direct Flights Filter Tests")
    class DirectFlightsFilterTests {

        @Test
        @DisplayName("Should filter for direct flights only when requested")
        void shouldFilterForDirectFlightsOnly() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter for direct flights only
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, true, pageable
            );

            // Then - Should exclude connecting flight (FL006)
            assertThat(results.getContent()).hasSize(3);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL999", "FL028");
            assertThat(results.getContent()).allSatisfy(flight ->
                    assertThat(flight.isDirect()).isTrue()
            );
        }

        @Test
        @DisplayName("Should include all flights when direct filter is false")
        void shouldIncludeAllFlightsWhenDirectFilterFalse() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter with directFlightsOnly = false
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, false, pageable
            );

            // Then - Should include all flights (direct and connecting)
            assertThat(results.getContent()).hasSize(4);
            assertThat(results.getContent()).extracting(Flight::getId)
                    .containsExactlyInAnyOrder("FL001", "FL006", "FL999", "FL028");
        }

        @Test
        @DisplayName("Should include all flights when direct filter is null")
        void shouldIncludeAllFlightsWhenDirectFilterNull() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable pageable = PageRequest.of(0, 10);

            // When - Filter with directFlightsOnly = null
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, pageable
            );

            // Then - Should include all flights
            assertThat(results.getContent()).hasSize(4);
        }
    }

    @Nested
    @DisplayName("Pagination and Sorting Tests")
    class PaginationAndSortingTests {

        @Test
        @DisplayName("Should handle pagination correctly")
        void shouldHandlePaginationCorrectly() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);

            // When - First page with size 2
            Pageable firstPage = PageRequest.of(0, 2);
            Page<Flight> firstResults = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, firstPage
            );

            // Then
            assertThat(firstResults.getContent()).hasSize(2);
            assertThat(firstResults.getTotalElements()).isEqualTo(4);
            assertThat(firstResults.getTotalPages()).isEqualTo(2);
            assertThat(firstResults.isFirst()).isTrue();
            assertThat(firstResults.isLast()).isFalse();

            // When - Second page
            Pageable secondPage = PageRequest.of(1, 2);
            Page<Flight> secondResults = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, secondPage
            );

            // Then
            assertThat(secondResults.getContent()).hasSize(2);
            assertThat(secondResults.isFirst()).isFalse();
            assertThat(secondResults.isLast()).isTrue();
        }

        @Test
        @DisplayName("Should handle sorting by departure time")
        void shouldHandleSortingByDepartureTime() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable sortedPageable = PageRequest.of(0, 10, Sort.by("departureTime").ascending());

            // When
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, sortedPageable
            );

            // Then - Results should be sorted by departure time
            assertThat(results.getContent()).hasSize(4);
            LocalDateTime previousDepartureTime = null;
            for (Flight flight : results.getContent()) {
                if (previousDepartureTime != null) {
                    assertThat(flight.getDepartureTime()).isAfterOrEqualTo(previousDepartureTime);
                }
                previousDepartureTime = flight.getDepartureTime();
            }
        }

        @Test
        @DisplayName("Should handle sorting by price")
        void shouldHandleSortingByPrice() {
            // Given
            LocalDateTime startOfDay = LocalDateTime.of(2025, 8, 15, 0, 0);
            LocalDateTime endOfDay = LocalDateTime.of(2025, 8, 16, 0, 0);
            Pageable sortedPageable = PageRequest.of(0, 10, Sort.by("economyPrice").ascending());

            // When
            Page<Flight> results = flightRepository.findFlights(
                    "SYD", "MEL", startOfDay, endOfDay,
                    null, null, null, sortedPageable
            );

            // Then - Results should be sorted by price (ascending)
            assertThat(results.getContent()).hasSize(4);
            assertThat(results.getContent().get(0).getId()).isEqualTo("FL028"); // Cheapest at 129.99
            assertThat(results.getContent().get(3).getId()).isEqualTo("FL999"); // Most expensive at 899.99
        }
    }

    @Nested
    @DisplayName("Basic JPA Repository Tests")
    class BasicJpaRepositoryTests {

        @Test
        @DisplayName("Should find flight by ID")
        void shouldFindFlightById() {
            // When
            Optional<Flight> result = flightRepository.findById("FL001");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getFlightNumber()).isEqualTo("AC101");
            assertThat(result.get().getOrigin()).isEqualTo("SYD");
            assertThat(result.get().getDestination()).isEqualTo("MEL");
        }

        @Test
        @DisplayName("Should return empty for non-existent flight ID")
        void shouldReturnEmptyForNonExistentFlightId() {
            // When
            Optional<Flight> result = flightRepository.findById("NONEXISTENT");

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should save and retrieve flight")
        void shouldSaveAndRetrieveFlight() {
            // Given
            Flight newFlight = new Flight(
                    "FL200", "AC200", "BNE", "PER",
                    LocalDateTime.of(2025, 8, 20, 10, 0),
                    LocalDateTime.of(2025, 8, 20, 14, 30),
                    "Boeing 787",
                    new BigDecimal("599.99"), new BigDecimal("799.99"),
                    new BigDecimal("1499.99"), new BigDecimal("2999.99"),
                    200, 40, 32, 8,
                    200, 40, 32, 8,
                    true, 0
            );

            // When
            Flight saved = flightRepository.save(newFlight);
            Optional<Flight> retrieved = flightRepository.findById("FL200");

            // Then
            assertThat(saved).isNotNull();
            assertThat(retrieved).isPresent();
            assertThat(retrieved.get().getFlightNumber()).isEqualTo("AC200");
            assertThat(retrieved.get().getOrigin()).isEqualTo("BNE");
            assertThat(retrieved.get().getDestination()).isEqualTo("PER");
        }

        @Test
        @DisplayName("Should count all flights")
        void shouldCountAllFlights() {
            // When
            long count = flightRepository.count();

            // Then
            assertThat(count).isEqualTo(6); // All flights created in setUp
        }

        @Test
        @DisplayName("Should delete flight by ID")
        void shouldDeleteFlightById() {
            // Given
            assertThat(flightRepository.existsById("FL001")).isTrue();

            // When
            flightRepository.deleteById("FL001");

            // Then
            assertThat(flightRepository.existsById("FL001")).isFalse();
            assertThat(flightRepository.count()).isEqualTo(5);
        }
    }
}