package com.travelagency.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:8889/airoport_syst"; // Verify your MySQL port
    private static final String USER = "root";
    private static final String PASS = "root"; // Empty for default MAMP/XAMPP root, or your password

    private static Connection connection = null;

    private DatabaseConnection() {} // Private constructor

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Class.forName("com.mysql.cj.jdbc.Driver"); // Generally not needed for JDBC 4.0+
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (SQLException e) {
                System.err.println("Database Connection Failed!");
                e.printStackTrace();
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    connection = null; // Important to allow re-connection
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Main method for quick testing
    public static void main(String[] args) {
        try {
            Connection connTest = DatabaseConnection.getConnection();
            if (connTest != null && !connTest.isClosed()) {
                System.out.println("Successfully connected to the database via DatabaseConnection class!");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            // Error already printed by getConnection() or could print more details here
        } finally {
            DatabaseConnection.closeConnection(); // Close after test
        }
    }
}