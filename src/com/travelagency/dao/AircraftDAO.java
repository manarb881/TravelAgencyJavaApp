package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Aircraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AircraftDAO {

    public boolean addAircraft(Aircraft aircraft) throws SQLException {
        String sql = "INSERT INTO Aircraft (AircraftName) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, aircraft.getAircraftName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                aircraft.setAircraftId(generatedKeys.getInt(1));
            }
            return true;
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public List<Aircraft> getAllAircraft() throws SQLException {
        List<Aircraft> aircraftList = new ArrayList<>();
        String sql = "SELECT * FROM Aircraft ORDER BY AircraftName";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Aircraft aircraft = new Aircraft(
                    rs.getInt("AircraftID"),
                    rs.getString("AircraftName")
                );
                aircraftList.add(aircraft);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return aircraftList;
    }
// In src/com/travelagency/dao/AircraftDAO.java
public Aircraft getAircraftById(int aircraftId) throws SQLException {
    String sql = "SELECT * FROM Aircraft WHERE AircraftID = ?";
    Aircraft aircraft = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
        conn = DatabaseConnection.getConnection();
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, aircraftId);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            aircraft = new Aircraft(
                rs.getInt("AircraftID"),
                rs.getString("AircraftName")
            );
        }
    } finally {
        // Close resources
        if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
        if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
    }
    return aircraft;
} // Add getAircraftById, updateAircraft, deleteAircraft as needed
}