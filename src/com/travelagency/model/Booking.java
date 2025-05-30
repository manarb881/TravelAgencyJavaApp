package com.travelagency.model;

import java.sql.Timestamp;

public class Booking {
    private int bookingNumber;
    private int state; // 0=Pending, 1=Confirmed, 2=Cancelled
    private String customerId;
    private int flightId;
    private int generalFlightId; // Denormalized for trigger, but important for linking
    private Timestamp bookingDate;

    public Booking() {
    }

    // Constructor for creating
    public Booking(int state, String customerId, int flightId, int generalFlightId) {
        this.state = state;
        this.customerId = customerId;
        this.flightId = flightId;
        this.generalFlightId = generalFlightId;
        // bookingDate can be set by DB or here: this.bookingDate = new Timestamp(System.currentTimeMillis());
    }


    // Full constructor
    public Booking(int bookingNumber, int state, String customerId, int flightId, int generalFlightId, Timestamp bookingDate) {
        this.bookingNumber = bookingNumber;
        this.state = state;
        this.customerId = customerId;
        this.flightId = flightId;
        this.generalFlightId = generalFlightId;
        this.bookingDate = bookingDate;
    }

    // Getters and Setters
    public int getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(int bookingNumber) { this.bookingNumber = bookingNumber; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public int getGeneralFlightId() { return generalFlightId; }
    public void setGeneralFlightId(int generalFlightId) { this.generalFlightId = generalFlightId; }
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }

    public String getStateString() {
        switch (state) {
            case 0: return "Pending";
            case 1: return "Confirmed";
            case 2: return "Cancelled";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Booking #: " + bookingNumber + " - Cust: " + customerId + " - Flight: " + flightId + " (" + getStateString() + ")";
    }
}