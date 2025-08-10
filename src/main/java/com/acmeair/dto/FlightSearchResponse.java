package com.acmeair.dto;

import com.acmeair.model.FlightType;

import java.util.List;
import java.util.stream.Collectors;

public class FlightSearchResponse {
    private FlightType flightType;
    private List<FlightResponseDto> outboundFlights;
    private List<FlightResponseDto> returnFlights;
    private int totalResults;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;

    public FlightSearchResponse() {}

    public FlightSearchResponse(FlightType flightType, List<com.acmeair.model.Flight> outboundFlights, int totalResults,
                                int currentPage, int pageSize, int totalPages, boolean isFirst, boolean isLast) {
        this.flightType = flightType;
        this.outboundFlights = outboundFlights.stream()
                .map(FlightResponseDto::new)
                .collect(Collectors.toList());
        this.returnFlights = null;
        this.totalResults = totalResults;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.isFirst = isFirst;
        this.isLast = isLast;
    }

    public FlightSearchResponse(FlightType flightType, List<com.acmeair.model.Flight> outboundFlights,
                                List<com.acmeair.model.Flight> returnFlights,
                                int totalResults, int currentPage, int pageSize, int totalPages,
                                boolean isFirst, boolean isLast) {
        this.flightType = flightType;
        this.outboundFlights = outboundFlights.stream()
                .map(FlightResponseDto::new)
                .collect(Collectors.toList());
        this.returnFlights = (returnFlights != null) ?
                returnFlights.stream()
                        .map(FlightResponseDto::new)
                        .collect(Collectors.toList()) : null;
        this.totalResults = totalResults;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.isFirst = isFirst;
        this.isLast = isLast;
    }

    public FlightType getFlightType() { return flightType; }
    public void setFlightType(FlightType flightType) { this.flightType = flightType; }

    public List<FlightResponseDto> getOutboundFlights() { return outboundFlights; }
    public void setOutboundFlights(List<FlightResponseDto> outboundFlights) { this.outboundFlights = outboundFlights; }

    public List<FlightResponseDto> getReturnFlights() { return returnFlights; }
    public void setReturnFlights(List<FlightResponseDto> returnFlights) { this.returnFlights = returnFlights; }

    public int getTotalResults() { return totalResults; }
    public void setTotalResults(int totalResults) { this.totalResults = totalResults; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isFirst() { return isFirst; }
    public void setFirst(boolean first) { isFirst = first; }

    public boolean isLast() { return isLast; }
    public void setLast(boolean last) { isLast = last; }
}