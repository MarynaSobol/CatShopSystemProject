// Name: Maryna Sobol 
// Student ID: C00312784 
// Date: March 2026
// Purpose: This class manages the connection to the MySQL database.

import java.sql.Connection; // For managing database connections
import java.sql.DriverManager; // For establishing a connection to the database
import java.sql.SQLException; // For handling SQL exceptions that may occur during database operations

// DatabaseConnection class to manage connections to the MySQL database
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/catshop"; // URL to connect to the local MySQL database named 'catshop'
    private static final String USER = "root"; // Username for the database connection.
    private static final String PASS = "12345678"; // Password for the database connection.

    // Method to establish and return a connection to the database
    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASS); // Establishes a connection using the specified URL, username, and password. Throws SQLException if the connection fails.
    }
}