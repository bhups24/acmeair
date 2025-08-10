package com.acmeair.model;

public enum FlightType {
    ONE_WAY("One Way"),
    RETURN("Return");

    private final String displayName;

    FlightType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}