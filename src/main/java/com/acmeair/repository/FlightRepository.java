package com.acmeair.repository;

import com.acmeair.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends JpaRepository<Flight, String> {

    @Query("""
        SELECT f FROM Flight f 
        WHERE UPPER(f.origin) = UPPER(:departureAirport)
        AND UPPER(f.destination) = UPPER(:arrivalAirport)
        AND f.departureTime >= :startOfDay
        AND f.departureTime < :endOfDay
        AND (:minPrice IS NULL OR f.economyPrice >= :minPrice)
        AND (:maxPrice IS NULL OR f.economyPrice <= :maxPrice)
        AND (:directFlightsOnly IS NULL OR :directFlightsOnly = false OR f.isDirect = true)
        """)
    Page<Flight> findFlights(
            @Param("departureAirport") String departureAirport,
            @Param("arrivalAirport") String arrivalAirport,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("directFlightsOnly") Boolean directFlightsOnly,
            Pageable pageable
    );
}