package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Booking;
import com.travelagency.model.BookingDetail; // For the SP

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement; // For Stored Procedures
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    // Add new booking. Database triggers handle seat counts.
    public boolean addBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO Booking (state, CustomerID, FlightID, GeneralFlightID, BookingDate) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, booking.getState());
            pstmt.setString(2, booking.getCustomerId());
            pstmt.setInt(3, booking.getFlightId());
            pstmt.setInt(4, booking.getGeneralFlightId()); // Crucial for the insert trigger
            if (booking.getBookingDate() != null) {
                pstmt.setTimestamp(5, booking.getBookingDate());
            } else {
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Default to now
            }


            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                booking.setBookingNumber(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public Booking getBookingByNumber(int bookingNumber) throws SQLException {
        String sql = "SELECT * FROM Booking WHERE BookingNumber = ?";
        Booking booking = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookingNumber);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                booking = new Booking(
                    rs.getInt("BookingNumber"),
                    rs.getInt("state"),
                    rs.getString("CustomerID"),
                    rs.getInt("FlightID"),
                    rs.getInt("GeneralFlightID"),
                    rs.getTimestamp("BookingDate")
                );
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return booking;
    }

    // Functionality 4: Retrieve booking detail based on booking number by using a stored procedure
    public BookingDetail retrieveBookingDetailsSP(int bookingNumber) throws SQLException {
        String sql = "{CALL SP_GetBookingDetails(?)}";
        BookingDetail detail = null;
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, bookingNumber);
            rs = cstmt.executeQuery();
            if (rs.next()) {
                detail = new BookingDetail();
                detail.setBookingNumber(rs.getInt("BookingNumber"));
                detail.setBookingState(rs.getInt("BookingState"));
                detail.setBookingDate(rs.getTimestamp("BookingDate"));
                detail.setCustomerId(rs.getString("CustomerID"));
                detail.setCustomerFirstName(rs.getString("CustomerFirstName"));
                detail.setCustomerLastName(rs.getString("CustomerLastName"));
                detail.setCustomerEmail(rs.getString("CustomerEmail"));
                detail.setCustomerAddress(rs.getString("CustomerAddress"));
                detail.setCustomerPhone(rs.getString("CustomerPhone"));
                detail.setFlightId(rs.getInt("FlightID"));
                detail.setFlightTakeOffDate(rs.getDate("FlightTakeOffDate"));
                detail.setFlightArrivalDate(rs.getDate("FlightArrivalDate"));
                detail.setFlightState(rs.getInt("FlightState"));
                detail.setGeneralFlightId(rs.getInt("GeneralFlightID"));
                detail.setGeneralFlightScheduledDeparture(rs.getString("GeneralFlightScheduledDeparture"));
                detail.setGeneralFlightScheduledArrival(rs.getString("GeneralFlightScheduledArrival"));
                detail.setNumberSeats(rs.getInt("NumberSeats"));
                detail.setSeatsBooked(rs.getInt("seats_booked"));
                detail.setDepartureAirport(rs.getString("DepartureAirport"));
                detail.setDepartureCity(rs.getString("DepartureCity"));
                detail.setArrivalAirport(rs.getString("ArrivalAirport"));
                detail.setArrivalCity(rs.getString("ArrivalCity"));
                detail.setAirlineName(rs.getString("AirlineName"));
                detail.setAircraftName(rs.getString("AircraftName")); // This can be null
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* log */ }
            if (cstmt != null) try { cstmt.close(); } catch (SQLException e) { /* log */ }
        }
        return detail;
    }

    // Functionality 7: Update the booking information (state) based on the booking number.
    // Seat adjustments are handled by the database trigger 'trg_booking_update_seats'.
    public boolean updateBookingState(int bookingNumber, int newState) throws SQLException {
        String sql = "UPDATE Booking SET state = ? WHERE BookingNumber = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newState);
            pstmt.setInt(2, bookingNumber);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }
    // Add getAllBookings, deleteBooking etc. as needed.
}