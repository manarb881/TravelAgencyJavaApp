package com.travelagency.model;

public class Airport {
    private int airportId;
    private String airportName;
    private int cityId; // Foreign key to City

    // Constructors
    public Airport() {
    }

    public Airport(String airportName, int cityId) {
        this.airportName = airportName;
        this.cityId = cityId;
    }

    public Airport(int airportId, String airportName, int cityId) {
        this.airportId = airportId;
        this.airportName = airportName;
        this.cityId = cityId;
    }

    // Getters and Setters
    public int getAirportId() {
        return airportId;
    }

    public void setAirportId(int airportId) {
        this.airportId = airportId;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return airportName; // Adjust as needed for JComboBox
    }
}