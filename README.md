# Acme Air Flight Search and Booking REST API

A Spring Boot 3 REST API for flight search and booking operations with H2 database, Liquibase migrations, and API key authentication.

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.0**
- **Gradle 8.x**
- **Spring Web** - REST API endpoints
- **Spring Security** - API key authentication
- **Spring Data JPA** - Database access
- **H2 Database** - In-memory database
- **Liquibase** - Database migrations
- **Spring Validation** - Request validation
- **JUnit 5** - Testing framework

## Features

- **Flight Search** with advanced filters (flight type, price range, date, direct flights)
- **Flight Booking** with seat allocation and passenger management
- **Authentication** via API key (X-API-Key header)
- **Database Persistence** with H2 and Liquibase migrations
- **Automatic Seat Management** with real-time availability tracking
- **Comprehensive Error Handling** with standardized responses

## Security

The API uses API key authentication for all endpoints.

**Demo API Key**: `acme-air-demo-2025-secure-token-12345`

Include this key in the `X-API-Key` header for all requests:
```bash
curl -H "X-API-Key: acme-air-demo-2025-secure-token-12345" \
  "http://localhost:8080/api/v1/flights/search?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15"
```

## Prerequisites

- **Java 21** (OpenJDK or Oracle JDK)
- **Gradle 8.x** (or use the included Gradle wrapper)

## Getting Started

### Quick Start with Scripts

**Unix/Linux/macOS:**
```bash
chmod +x run-dev.sh
./run-dev.sh
```

**Windows:**
```batch
run-dev.bat
```

### Manual Start
```bash
./gradlew bootRun
```

## API Endpoints

### Flight Operations

#### Search Flights
```
GET /api/v1/flights/search
```

**Required Parameters:**
- `flightType`: ONE_WAY or RETURN
- `departureAirport`: 3-letter airport code (e.g., SYD)
- `arrivalAirport`: 3-letter airport code (e.g., MEL)
- `departureDate`: Date in YYYY-MM-DD format

**Optional Parameters:**
- `returnDate`: Required for RETURN flights
- `minPrice`: Minimum price filter
- `maxPrice`: Maximum price filter
- `directFlightsOnly`: true/false
- `page`: Page number (default: 0)
- `size`: Results per page (1-100, default: 10)
- `sortBy`: Sort field (departureTime, price, origin, destination)
- `sortDirection`: asc or desc (default: asc)

**Example:**
```bash
curl -H "X-API-Key: acme-air-demo-2025-secure-token-12345" \
  "http://localhost:8080/api/v1/flights/search?flightType=ONE_WAY&departureAirport=SYD&arrivalAirport=MEL&departureDate=2025-08-15"
```

#### Get Flight Details
```
GET /api/v1/flights/{flightId}
```

### Booking Operations

#### Create Booking
```
POST /api/v1/bookings
Content-Type: application/json
```

**Request Body:**
```json
{
  "flightType": "ONE_WAY",
  "flightId": "FL001",
  "seatClass": "ECONOMY",
  "passenger": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@email.com",
    "phoneNumber": "+61412345678",
    "passportNumber": "A1234567",
    "dateOfBirth": "1990-05-15"
  }
}
```

#### Update Passenger Details
```
PUT /api/v1/bookings/{bookingId}/passenger
```

#### Cancel Booking
```
DELETE /api/v1/bookings/{bookingId}
```

#### Get Booking Details
```
GET /api/v1/bookings/{bookingId}
```

## Postman Collection
A Postman collection is available for testing the API endpoints. You can import it into Postman to quickly start testing the API.
Access it in the root folder of the project: AcmeAir_Flight_Booking_API_Postman_Collection.json

## Swagger Documentation
The API includes Swagger documentation for easy exploration of endpoints. Access it under root folder: AcmeAir_Swagger_Doc.yaml

## Sample Data

The application includes sample flights across Australian routes:

### Available Routes:
- **SYD ↔ MEL**: Sydney to Melbourne
- **SYD ↔ BNE**: Sydney to Brisbane
- **MEL ↔ PER**: Melbourne to Perth (long haul)
- **SYD ↔ PER**: Sydney to Perth (long haul)
- **BNE ↔ MEL**: Brisbane to Melbourne

### Seat Classes:
- **Economy**: $129.99 - $619.99
- **Premium Economy**: $229.99 - $819.99
- **Business**: $529.99 - $1519.99
- **First Class**: $929.99 - $2869.99

## Running Tests

```bash
./gradlew test
```

## Error Handling

Standardized error responses:

```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "timestamp": "2025-08-11T14:30:00Z (UTC)"
}
```

**HTTP Status Codes:**
- `200` - Success
- `201` - Created (successful booking)
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid API key)
- `404` - Not Found (flight/booking not found)
- `409` - Conflict (no seats available)
- `500` - Internal Server Error

## Validation Rules

### Flight Search Validation
- `flightType`: Required (ONE_WAY or RETURN)
- `departureAirport`: Required, 3 uppercase letters
- `arrivalAirport`: Required, 3 uppercase letters, different from departure
- `departureDate`: Required, must be in the future
- `returnDate`: Required for RETURN flights, must be after departure date
- Price range: minPrice ≤ maxPrice

### Booking Validation
- `flightId`: Required
- `returnFlightId`: Required for RETURN flights, must be different from outbound
- `seatClass`: Required (ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST_CLASS)
- Passenger details: All fields required with proper formats

## Architecture

- **Controller Layer**: REST endpoints with validation
- **Security Layer**: API key authentication
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: JPA repositories for data access
- **Database Layer**: H2 with Liquibase migrations

## Missing Tests
- **BookingUpdateComponentTest**: One Successful test for updating passenger details
- **BookingDetailsComponentTest**: One Successful test for retrieving booking details