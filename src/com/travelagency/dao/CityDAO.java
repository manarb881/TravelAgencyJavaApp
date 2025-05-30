package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.City;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CityDAO {

    public boolean addCity(City city) throws SQLException {
        String sql = "INSERT INTO City (CityName) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Start transaction if this operation should be atomic with others not handled by DB trigger
            // conn.setAutoCommit(false); 
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, city.getCityName());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                // conn.rollback(); // if using transactions
                return false;
            }

            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                city.setCityId(generatedKeys.getInt(1));
            }
            // conn.commit(); // if using transactions
            return true;
        } catch (SQLException e) {
            // if (conn != null) conn.rollback(); // if using transactions
            throw e; // Re-throw to be handled by caller
        } finally {
            if (generatedKeys != null) try { generatedKeys.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
            // Connection closing is handled by DatabaseConnection's closeConnection or by the calling service layer
        }
    }

    public City getCityById(int cityId) throws SQLException {
        String sql = "SELECT * FROM City WHERE CityID = ?";
        City city = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cityId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                city = new City();
                city.setCityId(rs.getInt("CityID"));
                city.setCityName(rs.getString("CityName"));
            }
        } finally {
             if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
             if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return city;
    }

    public List<City> getAllCities() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = "SELECT * FROM City ORDER BY CityName";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                City city = new City(rs.getInt("CityID"), rs.getString("CityName"));
                cities.add(city);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return cities;
    }

    public boolean updateCity(City city) throws SQLException {
        String sql = "UPDATE City SET CityName = ? WHERE CityID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city.getCityName());
            pstmt.setInt(2, city.getCityId());
            return pstmt.executeUpdate() > 0;
        } finally {
             if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public boolean deleteCity(int cityId) throws SQLException {
        String sql = "DELETE FROM City WHERE CityID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cityId);
            return pstmt.executeUpdate() > 0;
        } finally {
             if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }
}