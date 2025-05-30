package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Airport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AirportDAO {

    public boolean addAirport(Airport airport) throws SQLException {
        String sql = "INSERT INTO Airport (AirportName, CityID) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, airport.getAirportName());
            pstmt.setInt(2, airport.getCityId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                airport.setAirportId(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public List<Airport> getAllAirports() throws SQLException {
        List<Airport> airports = new ArrayList<>();
        // Join with City to get CityName for better display if needed later,
        // but for pure Airport objects, this is enough.
        String sql = "SELECT * FROM Airport ORDER BY AirportName";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Airport airport = new Airport(
                    rs.getInt("AirportID"),
                    rs.getString("AirportName"),
                    rs.getInt("CityID")
                );
                airports.add(airport);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return airports;
    }

    // You can add getAirportById, updateAirport, deleteAirport similar to CityDAO
    // Example: getAirportById
    public Airport getAirportById(int airportId) throws SQLException {
        String sql = "SELECT * FROM Airport WHERE AirportID = ?";
        Airport airport = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, airportId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                airport = new Airport(
                    rs.getInt("AirportID"),
                    rs.getString("AirportName"),
                    rs.getInt("CityID")
                );
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return airport;
    }
}