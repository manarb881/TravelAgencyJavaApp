package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Flight;
import com.travelagency.model.GeneralFlight;
// If you create a DTO for search results that includes Airport/Airline names:
// import com.travelagency.model.dto.FlightSearchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; // For takeOffDate, arrivalDate
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {

    public boolean addFlight(Flight flight) throws SQLException {
        String sql = "INSERT INTO Flight (TakeOffDate, ArrivalDate, State, GeneralFlightID, AircraftID) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setDate(1, flight.getTakeOffDate());
            pstmt.setDate(2, flight.getArrivalDate());
            pstmt.setInt(3, flight.getState());
            pstmt.setInt(4, flight.getGeneralFlightId());
            if (flight.getAircraftId() != null) {
                pstmt.setInt(5, flight.getAircraftId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                flight.setFlightId(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public Flight getFlightById(int flightId) throws SQLException {
        String sql = "SELECT * FROM Flight WHERE FlightID = ?";
        Flight flight = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Integer aircraftId = rs.getInt("AircraftID");
                if (rs.wasNull()) {
                    aircraftId = null;
                }
                flight = new Flight(
                    rs.getInt("FlightID"),
                    rs.getDate("TakeOffDate"),
                    rs.getDate("ArrivalDate"),
                    rs.getInt("State"),
                    rs.getInt("GeneralFlightID"),
                    aircraftId
                );
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return flight;
    }
    
    public List<Flight> getAllFlights() throws SQLException {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM Flight ORDER BY TakeOffDate"; // Add more criteria if needed
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while(rs.next()){
                Integer aircraftId = rs.getInt("AircraftID");
                if (rs.wasNull()) {
                    aircraftId = null;
                }
                Flight flight = new Flight(
                    rs.getInt("FlightID"),
                    rs.getDate("TakeOffDate"),
                    rs.getDate("ArrivalDate"),
                    rs.getInt("State"),
                    rs.getInt("GeneralFlightID"),
                    aircraftId
                );
                flights.add(flight);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return flights;
    }

    // Functionality 2: Retrieve all flights based on the departure and arrival cities
    // This searches GeneralFlight schedules, not specific Flight instances.
    // If you want operational flights, the query needs to join Flight and GeneralFlight.
    // Let's assume this searches for available flight *schedules* matching cities.
    public List<GeneralFlight> getFlightSchedulesByCities(String departureCityName, String arrivalCityName) throws SQLException {
        List<GeneralFlight> flights = new ArrayList<>();
        String sql = "SELECT gf.* " +
                     "FROM GeneralFlight gf " +
                     "JOIN Airport dep_ap ON gf.DepAirport = dep_ap.AirportID " +
                     "JOIN City dep_city ON dep_ap.CityID = dep_city.CityID " +
                     "JOIN Airport arr_ap ON gf.ArrAirport = arr_ap.AirportID " +
                     "JOIN City arr_city ON arr_ap.CityID = arr_city.CityID " +
                     "WHERE dep_city.CityName LIKE ? AND arr_city.CityName LIKE ? " +
                     "ORDER BY gf.FDay, gf.TakeoffTime";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + departureCityName + "%");
            pstmt.setString(2, "%" + arrivalCityName + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                GeneralFlight gf = new GeneralFlight(
                    rs.getInt("GeneralFlightID"), rs.getTime("TakeoffTime"), rs.getTime("ArrivalTime"),
                    rs.getDate("FDay"), rs.getInt("DepAirport"), rs.getInt("ArrAirport"),
                    rs.getInt("AirlineID"), rs.getInt("NumberSeats"), rs.getInt("seats_booked")
                );
                flights.add(gf);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return flights;
    }

    // Functionality 3: Retrieve all the flights that take off from a specific city, in certain period.
    // This searches operational Flight instances.
    public List<Flight> getFlightsFromCityInPeriod(String departureCityName, Date startDate, Date endDate) throws SQLException {
        List<Flight> flights = new ArrayList<>();
        // This query needs joins to get city name and potentially airline/aircraft for display
        // For simplicity, returning just Flight objects. UI can fetch more details if needed.
        String sql = "SELECT fl.* " +
                     "FROM Flight fl " +
                     "JOIN GeneralFlight gf ON fl.GeneralFlightID = gf.GeneralFlightID " +
                     "JOIN Airport dep_ap ON gf.DepAirport = dep_ap.AirportID " +
                     "JOIN City dep_city ON dep_ap.CityID = dep_city.CityID " +
                     "WHERE dep_city.CityName LIKE ? AND fl.TakeOffDate BETWEEN ? AND ? " +
                     "ORDER BY fl.TakeOffDate, gf.TakeoffTime";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + departureCityName + "%");
            pstmt.setDate(2, startDate);
            pstmt.setDate(3, endDate);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                 Integer aircraftId = rs.getInt("AircraftID");
                if (rs.wasNull()) {
                    aircraftId = null;
                }
                Flight flight = new Flight(
                    rs.getInt("FlightID"), rs.getDate("TakeOffDate"), rs.getDate("ArrivalDate"),
                    rs.getInt("State"), rs.getInt("GeneralFlightID"), aircraftId
                );
                flights.add(flight);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return flights;
    }

    // Functionality 8: Update a specific flight by changing departure date/time and/or arrival date/time
    // Note: Time part is on GeneralFlight. This updates dates on operational Flight.
    public boolean updateFlightDatesAndState(Flight flight) throws SQLException {
        String sql = "UPDATE Flight SET TakeOffDate = ?, ArrivalDate = ?, State = ? WHERE FlightID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setDate(1, flight.getTakeOffDate());
            pstmt.setDate(2, flight.getArrivalDate());
            pstmt.setInt(3, flight.getState()); // Also allow updating state
            pstmt.setInt(4, flight.getFlightId());
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }
     // Add deleteFlight as needed (be careful with bookings linked to it)
}