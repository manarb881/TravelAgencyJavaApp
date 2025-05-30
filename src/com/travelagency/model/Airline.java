package com.travelagency.model;

public class Airline {
    private int airlineId;
    private String airlineName;

    public Airline() {
    }

    public Airline(String airlineName) {
        this.airlineName = airlineName;
    }

    public Airline(int airlineId, String airlineName) {
        this.airlineId = airlineId;
        this.airlineName = airlineName;
    }

    public int getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    @Override
    public String toString() {
        return airlineName;
    }
}