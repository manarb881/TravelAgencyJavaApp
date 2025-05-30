package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.GeneralFlight;
// If you create a DTO (Data Transfer Object) for richer display in JComboBox:
// import com.travelagency.model.dto.GeneralFlightDisplay;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; // For fDay
import java.sql.Time; // For takeoffTime, arrivalTime
import java.util.ArrayList;
import java.util.List;

public class GeneralFlightDAO {

    public boolean addGeneralFlight(GeneralFlight gf) throws SQLException {
        String sql = "INSERT INTO GeneralFlight (TakeoffTime, ArrivalTime, FDay, DepAirport, ArrAirport, AirlineID, NumberSeats, seats_booked) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setTime(1, gf.getTakeoffTime());
            pstmt.setTime(2, gf.getArrivalTime());
            pstmt.setDate(3, gf.getfDay());
            pstmt.setInt(4, gf.getDepAirportId());
            pstmt.setInt(5, gf.getArrAirportId());
            pstmt.setInt(6, gf.getAirlineId());
            pstmt.setInt(7, gf.getNumberSeats());
            pstmt.setInt(8, gf.getSeatsBooked()); // Usually 0 for new, but allow setting

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                gf.setGeneralFlightId(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public GeneralFlight getGeneralFlightById(int generalFlightId) throws SQLException {
        String sql = "SELECT * FROM GeneralFlight WHERE GeneralFlightID = ?";
        GeneralFlight gf = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, generalFlightId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                gf = new GeneralFlight(
                    rs.getInt("GeneralFlightID"),
                    rs.getTime("TakeoffTime"),
                    rs.getTime("ArrivalTime"),
                    rs.getDate("FDay"),
                    rs.getInt("DepAirport"),
                    rs.getInt("ArrAirport"),
                    rs.getInt("AirlineID"),
                    rs.getInt("NumberSeats"),
                    rs.getInt("seats_booked")
                );
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return gf;
    }

    public List<GeneralFlight> getAllGeneralFlights() throws SQLException {
        List<GeneralFlight> generalFlights = new ArrayList<>();
        // Consider joining with Airport and Airline for richer objects if needed directly
        // Or create a separate DTO for display purposes.
        String sql = "SELECT * FROM GeneralFlight ORDER BY FDay, TakeoffTime";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                GeneralFlight gf = new GeneralFlight(
                    rs.getInt("GeneralFlightID"),
                    rs.getTime("TakeoffTime"),
                    rs.getTime("ArrivalTime"),
                    rs.getDate("FDay"),
                    rs.getInt("DepAirport"),
                    rs.getInt("ArrAirport"),
                    rs.getInt("AirlineID"),
                    rs.getInt("NumberSeats"),
                    rs.getInt("seats_booked")
                );
                generalFlights.add(gf);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return generalFlights;
    }

    // Add updateGeneralFlight (e.g., number of seats, times - careful with existing bookings)
    // Add deleteGeneralFlight (careful with FK constraints from Flight table)
}