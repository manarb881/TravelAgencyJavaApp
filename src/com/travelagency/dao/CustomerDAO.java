package com.travelagency.dao;

import com.travelagency.db.DatabaseConnection;
import com.travelagency.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // CustomerID is provided by user, not auto-incremented
    public boolean addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (CustomerID, fname, lname, address, email, phoneNumber) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getPhoneNumber());
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
    }

    public Customer getCustomerById(String customerId) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE CustomerID = ?";
        Customer customer = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, customerId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                    rs.getString("CustomerID"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("address"),
                    rs.getString("email"),
                    rs.getString("phoneNumber")
                );
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return customer;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY lname, fname";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getString("CustomerID"),
                    rs.getString("fname"),
                    rs.getString("lname"),
                    rs.getString("address"),
                    rs.getString("email"),
                    rs.getString("phoneNumber")
                );
                customers.add(customer);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return customers;
    }
    
    // Functionality 5: Retrieve customers traveling on a specific flight.
    public List<Customer> getCustomersByFlight(int flightId) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.CustomerID, c.fname, c.lname, c.email, c.phoneNumber " +
                     "FROM Customer c " +
                     "JOIN Booking b ON c.CustomerID = b.CustomerID " +
                     "WHERE b.FlightID = ? " +
                     "ORDER BY c.lname, c.fname";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, flightId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("CustomerID"));
                customer.setFirstName(rs.getString("fname"));
                customer.setLastName(rs.getString("lname"));
                customer.setEmail(rs.getString("email"));
                customer.setPhoneNumber(rs.getString("phoneNumber"));
                // BookingNumber is not part of Customer object, but could be returned in a different DTO if needed
                customers.add(customer);
            }
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException logOrIgnore) {}
            if (pstmt != null) try { pstmt.close(); } catch (SQLException logOrIgnore) {}
        }
        return customers;
    }
    // Add updateCustomer, deleteCustomer as needed
}