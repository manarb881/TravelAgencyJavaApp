package com.travelagency.model;

import java.sql.Date; // For FlightTakeOffDate, FlightArrivalDate
import java.sql.Timestamp; // For BookingDate

// This class will hold all the fields returned by SP_GetBookingDetails
public class BookingDetail {
    private int bookingNumber;
    private int bookingState; // Mapped to "Pending", "Confirmed", "Cancelled"
    private Timestamp bookingDate;
    private String customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerAddress;
    private String customerPhone;
    private int flightId;
    private Date flightTakeOffDate;
    private Date flightArrivalDate;
    private int flightState; // Mapped to "Scheduled", "On-going", "Cancelled", "Completed"
    private int generalFlightId;
    private String generalFlightScheduledDeparture; // String like "YYYY-MM-DD HH:MM:SS"
    private String generalFlightScheduledArrival;   // String like "YYYY-MM-DD HH:MM:SS"
    private int numberSeats;
    private int seatsBooked;
    private String departureAirport;
    private String departureCity;
    private String arrivalAirport;
    private String arrivalCity;
    private String airlineName;
    private String aircraftName; // Can be null

    public BookingDetail() {}

    // Getters and Setters for ALL fields (generate them in your IDE)
    public int getBookingNumber() { return bookingNumber; }
    public void setBookingNumber(int bookingNumber) { this.bookingNumber = bookingNumber; }
    public int getBookingState() { return bookingState; }
    public void setBookingState(int bookingState) { this.bookingState = bookingState; }
    public Timestamp getBookingDate() { return bookingDate; }
    public void setBookingDate(Timestamp bookingDate) { this.bookingDate = bookingDate; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerFirstName() { return customerFirstName; }
    public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }
    public String getCustomerLastName() { return customerLastName; }
    public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public Date getFlightTakeOffDate() { return flightTakeOffDate; }
    public void setFlightTakeOffDate(Date flightTakeOffDate) { this.flightTakeOffDate = flightTakeOffDate; }
    public Date getFlightArrivalDate() { return flightArrivalDate; }
    public void setFlightArrivalDate(Date flightArrivalDate) { this.flightArrivalDate = flightArrivalDate; }
    public int getFlightState() { return flightState; }
    public void setFlightState(int flightState) { this.flightState = flightState; }
    public int getGeneralFlightId() { return generalFlightId; }
    public void setGeneralFlightId(int generalFlightId) { this.generalFlightId = generalFlightId; }
    public String getGeneralFlightScheduledDeparture() { return generalFlightScheduledDeparture; }
    public void setGeneralFlightScheduledDeparture(String generalFlightScheduledDeparture) { this.generalFlightScheduledDeparture = generalFlightScheduledDeparture; }
    public String getGeneralFlightScheduledArrival() { return generalFlightScheduledArrival; }
    public void setGeneralFlightScheduledArrival(String generalFlightScheduledArrival) { this.generalFlightScheduledArrival = generalFlightScheduledArrival; }
    public int getNumberSeats() { return numberSeats; }
    public void setNumberSeats(int numberSeats) { this.numberSeats = numberSeats; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
    public String getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(String departureAirport) { this.departureAirport = departureAirport; }
    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }
    public String getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(String arrivalAirport) { this.arrivalAirport = arrivalAirport; }
    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    public String getAircraftName() { return aircraftName; }
    public void setAircraftName(String aircraftName) { this.aircraftName = aircraftName; }

    // Helper methods for displaying mapped states
    public String getBookingStateString() {
        switch (bookingState) {
            case 0: return "Pending";
            case 1: return "Confirmed";
            case 2: return "Cancelled";
            default: return "Unknown (" + bookingState + ")";
        }
    }

    public String getFlightStateString() { // As per SP comments: 0=Sched, 1=On-going, 2=Canc, 3=Compl
        switch (flightState) {
            case 0: return "Scheduled";
            case 1: return "On-going";
            case 2: return "Cancelled";
            case 3: return "Completed";
            default: return "Unknown (" + flightState + ")";
        }
    }
}