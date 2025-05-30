package com.travelagency.model;

import java.sql.Date;

public class Flight { // Represents an operational flight instance
    private int flightId;
    private Date takeOffDate;
    private Date arrivalDate;
    private int state; // 0=Scheduled, 1=In Progress, 2=Completed, 3=Cancelled
    private int generalFlightId;
    private Integer aircraftId; // Use Integer to allow null

    public Flight() {
    }

    // Constructor for creating
    public Flight(Date takeOffDate, Date arrivalDate, int state, int generalFlightId, Integer aircraftId) {
        this.takeOffDate = takeOffDate;
        this.arrivalDate = arrivalDate;
        this.state = state;
        this.generalFlightId = generalFlightId;
        this.aircraftId = aircraftId;
    }

    // Full constructor
    public Flight(int flightId, Date takeOffDate, Date arrivalDate, int state, int generalFlightId, Integer aircraftId) {
        this.flightId = flightId;
        this.takeOffDate = takeOffDate;
        this.arrivalDate = arrivalDate;
        this.state = state;
        this.generalFlightId = generalFlightId;
        this.aircraftId = aircraftId;
    }

    // Getters and Setters
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public Date getTakeOffDate() { return takeOffDate; }
    public void setTakeOffDate(Date takeOffDate) { this.takeOffDate = takeOffDate; }
    public Date getArrivalDate() { return arrivalDate; }
    public void setArrivalDate(Date arrivalDate) { this.arrivalDate = arrivalDate; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public int getGeneralFlightId() { return generalFlightId; }
    public void setGeneralFlightId(int generalFlightId) { this.generalFlightId = generalFlightId; }
    public Integer getAircraftId() { return aircraftId; }
    public void setAircraftId(Integer aircraftId) { this.aircraftId = aircraftId; }

    // Helper method for UI display
    public String getStateString() {
        switch (state) {
            case 0: return "Scheduled";
            case 1: return "In Progress";
            case 2: return "Completed";
            case 3: return "Cancelled";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
         // Needs more info for good display, often fetched via joins in DAO for UI model
        return "Flight ID: " + flightId + " on " + takeOffDate + " (State: " + getStateString() + ")";
    }
}