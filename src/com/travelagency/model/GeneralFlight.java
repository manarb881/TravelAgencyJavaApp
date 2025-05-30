package com.travelagency.model;

import java.sql.Date;
import java.sql.Time;

public class GeneralFlight {
    private int generalFlightId;
    private Time takeoffTime;
    private Time arrivalTime;
    private Date fDay; // Reference day for the schedule pattern
    private int depAirportId;
    private int arrAirportId;
    private int airlineId;
    private int numberSeats;
    private int seatsBooked;

    public GeneralFlight() {
    }

    // Constructor for creating (ID will be set by DB)
    public GeneralFlight(Time takeoffTime, Time arrivalTime, Date fDay, int depAirportId, int arrAirportId, int airlineId, int numberSeats) {
        this.takeoffTime = takeoffTime;
        this.arrivalTime = arrivalTime;
        this.fDay = fDay;
        this.depAirportId = depAirportId;
        this.arrAirportId = arrAirportId;
        this.airlineId = airlineId;
        this.numberSeats = numberSeats;
        this.seatsBooked = 0; // Default for new
    }


    // Full constructor
    public GeneralFlight(int generalFlightId, Time takeoffTime, Time arrivalTime, Date fDay, int depAirportId, int arrAirportId, int airlineId, int numberSeats, int seatsBooked) {
        this.generalFlightId = generalFlightId;
        this.takeoffTime = takeoffTime;
        this.arrivalTime = arrivalTime;
        this.fDay = fDay;
        this.depAirportId = depAirportId;
        this.arrAirportId = arrAirportId;
        this.airlineId = airlineId;
        this.numberSeats = numberSeats;
        this.seatsBooked = seatsBooked;
    }

    // Getters and Setters
    public int getGeneralFlightId() { return generalFlightId; }
    public void setGeneralFlightId(int generalFlightId) { this.generalFlightId = generalFlightId; }
    public Time getTakeoffTime() { return takeoffTime; }
    public void setTakeoffTime(Time takeoffTime) { this.takeoffTime = takeoffTime; }
    public Time getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public Date getfDay() { return fDay; }
    public void setfDay(Date fDay) { this.fDay = fDay; }
    public int getDepAirportId() { return depAirportId; }
    public void setDepAirportId(int depAirportId) { this.depAirportId = depAirportId; }
    public int getArrAirportId() { return arrAirportId; }
    public void setArrAirportId(int arrAirportId) { this.arrAirportId = arrAirportId; }
    public int getAirlineId() { return airlineId; }
    public void setAirlineId(int airlineId) { this.airlineId = airlineId; }
    public int getNumberSeats() { return numberSeats; }
    public void setNumberSeats(int numberSeats) { this.numberSeats = numberSeats; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }

    @Override
    public String toString() {
        // Example: "GFID: 1 - Dep: [AirportName] Arr: [AirportName] on FDay @ Time"
        // Needs more info for good display, often fetched via joins in DAO for UI model
        return "GFID: " + generalFlightId + " (Seats: " + (numberSeats - seatsBooked) + "/" + numberSeats + ")";
    }
}