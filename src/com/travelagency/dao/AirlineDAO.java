package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Airline;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AirlineDAO {

    public boolean addAirline(Airline airline) throws SQLException {
        String sql = "INSERT INTO Airline (AirlineName) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, airline.getAirlineName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                airline.setAirlineId(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public List<Airline> getAllAirlines() throws SQLException {
        List<Airline> airlines = new ArrayList<>();
        String sql = "SELECT * FROM Airline ORDER BY AirlineName";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Airline airline = new Airline(
                    rs.getInt("AirlineID"),
                    rs.getString("AirlineName")
                );
                airlines.add(airline);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return airlines;
    }
    public Airline getAirlineById(int airlineId) throws SQLException {
        String sql = "SELECT * FROM Airline WHERE AirlineID = ?";
        Airline airline = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, airlineId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    airline = new Airline(rs.getInt("AirlineID"), rs.getString("AirlineName"));
                }
            }
        }
        return airline;
    }
    // Functionality 6: Retrieve the list of companies that have a flight working in a specific city.
    public List<Airline> getAirlinesInCity(String cityName) throws SQLException {
        List<Airline> airlines = new ArrayList<>();
        String sql = "SELECT DISTINCT a.AirlineID, a.AirlineName " +
                     "FROM GeneralFlight gf " +
                     "JOIN Airline a ON gf.AirlineID = a.AirlineID " +
                     "JOIN Airport dep_ap ON gf.DepAirport = dep_ap.AirportID " +
                     "JOIN City c ON dep_ap.CityID = c.CityID " +
                     "WHERE c.CityName LIKE ? " + // Using LIKE for flexibility
                     "ORDER BY a.AirlineName DESC"; // As per requirement
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + cityName + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Airline airline = new Airline(
                    rs.getInt("AirlineID"),
                    rs.getString("AirlineName")
                );
                airlines.add(airline);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return airlines;
    }
    // Add getAirlineById, updateAirline, deleteAirline as needed
}