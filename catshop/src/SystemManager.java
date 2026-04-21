// Name: Maryna Sobol 
// Student ID: C00312784 
// Date: March 2026
// Purpose: This class manages all the core business logic and database interactions for the Cat Shop application. 
// It serves as the central point for handling user authentication, cat management, order processing, and user profile

import java.sql.*; // For managing database connections, executing SQL queries, and handling SQL exceptions
import java.util.ArrayList; // For using ArrayList to store lists of cats and orders
import java.util.List; // For using List interface to define return types for methods that return lists of cats and orders


public class SystemManager 
{
    // --- USER OPERATIONS ---
    public int[] login(String user, String pass) throws SQLException 
    {
        String sql = "SELECT userID, userStatus FROM User WHERE userName=? AND userPassword=?"; // Simple authentication query. In production, use hashed passwords and secure practices.
        try (Connection conn = DatabaseConnection.getConnection(); // Get a connection from the pool
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user); // Set the username parameter
            ps.setString(2, pass); // Set the password parameter
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    return new int[]{rs.getInt("userID"), rs.getInt("userStatus")}; // Return userID and userStatus for session management
                }
            }
        }
        return null;
    }

    // Register a new user with the provided details. The userStatus is set to 0 (customer) by default.
    public boolean registerUser(String name, String email, String pass, String address) throws SQLException 
    {
        String sql = "INSERT INTO User (userName, userEmail, userPassword, userAddress, userStatus) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, address);
            return ps.executeUpdate() > 0;
        }
    }

    // Retrieve a list of cats from the database. If the user is a manager, show all cats,
    // if the user is a customer, show only cats that are available for purchase.
    public List<Object[]> getAllCats(boolean isManager) throws SQLException 
    {
    List<Object[]> list = new ArrayList<>(); // Create a list to hold the cat data. Each Object[] will represent a cat's details.
    
    // Determine the SQL query based on the user's role. Managers see all cats, while customers see only available cats.
    String sql = isManager ? "SELECT * FROM Cat" : "SELECT * FROM Cat WHERE catStatus = 'Available'";
    
    // Use try-with-resources to ensure that the database connection, prepared statement, and result set are properly closed after use
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) 
    {
        // Iterate through the result set and add each cat's details to the list as an Object array
        while (rs.next()) 
        {
            // Each Object[] contains the cat's ID, name, details, age, price, and status. This allows for flexible handling of the cat data in the application.
            list.add(new Object[]
            {
                rs.getInt("catID"), 
                rs.getString("catName"), 
                rs.getString("catDetails"),
                rs.getInt("catAge"), 
                rs.getFloat("catPrice"), 
                rs.getString("catStatus")
            });
        }
    }
    return list;
}

// --- ORDER OPERATIONS ---
// Place an order for a cat. This involves creating a new order, adding an order item for the cat, and updating the cat's status to 'Sold'.
public boolean placeOrder(int userID, int catID, float price) throws SQLException 
{
    Connection conn = null;
    try 
    {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false); // Start transaction

        // Create the Order record 
        String orderSql = "INSERT INTO `Order` (userID, orderDate, orderTotalPrice) VALUES (?, CURDATE(), ?)";
        PreparedStatement psOrder = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
        psOrder.setInt(1, userID);
        psOrder.setFloat(2, price);
        psOrder.executeUpdate();

        // Get the generated orderID
        ResultSet rs = psOrder.getGeneratedKeys();
        if (rs.next()) 
        {
            int orderID = rs.getInt(1); // Retrieve the auto-generated orderID from the database after inserting the new order record

            // Create the OrderItem record 
            String itemSql = "INSERT INTO OrderItem (orderID, catID, orderItemPrice) VALUES (?, ?, ?)";
            PreparedStatement psItem = conn.prepareStatement(itemSql);
            psItem.setInt(1, orderID);
            psItem.setInt(2, catID);
            psItem.setFloat(3, price);
            psItem.executeUpdate();

            //Update Cat status to 'Sold'
            String catSql = "UPDATE Cat SET catStatus = 'Sold' WHERE catID = ?";
            PreparedStatement psCat = conn.prepareStatement(catSql);
            psCat.setInt(1, catID);
            psCat.executeUpdate();

            conn.commit(); // Save all changes
            return true;
        }
    } 
    // If any SQLException occurs during the transaction, roll back all changes to maintain data integrity and throw 
    // the exception to be handled by the calling method
    catch (SQLException e) 
    {
        if (conn != null) conn.rollback(); 
        throw e;
    } 
    // Finally, ensure that the connection's auto-commit mode is reset to true before returning from the method, 4
    // regardless of whether the transaction succeeded or failed
    finally 
    {
        if (conn != null) conn.setAutoCommit(true); // Reset auto-commit to true before returning
    }
    return false;
}


// Retrieve a list of orders for a specific user. This involves joining the Order, OrderItem, 
// and Cat tables to show the details of each order, including the cat's name, order date, and price.
public List<Object[]> getUserOrders(int userID) throws SQLException 
{
    List<Object[]> list = new ArrayList<>();
    // Join Order, OrderItem, and Cat to show the user what they bought 
    String sql = "SELECT o.orderID, c.catName, o.orderDate, oi.orderItemPrice " +
                 "FROM `Order` o " +
                 "JOIN OrderItem oi ON o.orderID = oi.orderID " +
                 "JOIN Cat c ON oi.catID = c.catID " +
                 "WHERE o.userID = ?"; // SQL query to retrieve the order details for a specific user by joining 
                 // the Order, OrderItem, and Cat tables.

    // Use try-with-resources to ensure that the database connection, prepared statement, and result set are properly closed after use
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) 
    {
        ps.setInt(1, userID);

        // Execute the query and iterate through the result set to add each order's details to the list as an Object array
        try (ResultSet rs = ps.executeQuery()) 
        {

            // Each Object[] contains the order ID, cat name, order date, and item price.
            // This allows for flexible handling of the order data in the application.
            while (rs.next()) {
                list.add(new Object[]
                {
                    rs.getInt("orderID"),
                    rs.getString("catName"),
                    rs.getDate("orderDate"),
                    rs.getFloat("orderItemPrice")
                });
            }
        }
    }
    return list;
}


// --- CAT OPERATIONS ---
    public boolean addCat(String name, String details, int age, float price) throws SQLException 
    {
        String sql = "INSERT INTO Cat (catName, catDetails, catAge, catPrice, catStatus) VALUES (?, ?, ?, ?, 'Available')";
        // Use try-with-resources to ensure that the database connection and prepared statement are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, name);
            ps.setString(2, details);
            ps.setInt(3, age);
            ps.setFloat(4, price);
            return ps.executeUpdate() > 0;
        }
    }

    // Update a specific field of a cat's record based on the provided catID, field name, and new value.
   public boolean amendSpecificField(int catID, String fieldName, String newValue) throws SQLException 
   {
        String columnName = ""; // Map the user-friendly field name to the actual database column name. 
        // This allows the method to be flexible and handle updates to different fields based on user input.

        // Use a switch statement to determine which database column corresponds to the provided field name.
        switch (fieldName) 
        {
            case "Name":    columnName = "catName"; break;
            case "Price":   columnName = "catPrice"; break;
            case "Details": columnName = "catDetails"; break;
            case "Age":     columnName = "catAge"; break;
            case "Status":  columnName = "catStatus"; break;
            default: throw new IllegalArgumentException("Invalid field name: " + fieldName); 
        }

        String sql = "UPDATE Cat SET " + columnName + " = ? WHERE catID = ?"; 
        // Construct the SQL query to update the specified field for the cat with the given catID.

        // Use try-with-resources to ensure that the database connection and prepared statement are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            
            // Set the appropriate parameter in the prepared statement based on the field being updated.
            try 
            {
                // If the field being updated is "Age" or "Price", parse the new value to the correct data type (int for Age, float for Price).
                if (fieldName.equals("Age")) 
                {
                    ps.setInt(1, Integer.parseInt(newValue));
                } 
                // If the field being updated is "Price", parse the new value to a float.
                else if (fieldName.equals("Price")) 
                {
                    ps.setFloat(1, Float.parseFloat(newValue));
                } 
                // For other fields (Name, Details, Status), set the new value as a string.
                else 
                {
                    ps.setString(1, newValue);
                }
            } 
            // If a NumberFormatException occurs while parsing the new value for Age or Price, 
            // throw a SQLException with a user-friendly error message.
            catch (NumberFormatException e) 
            {
                throw new SQLException("Error: Please enter a valid number for Age or Price.");
            }
            
            ps.setInt(2, catID); // Set the catID parameter to specify which cat record to update
            return ps.executeUpdate() > 0; // Execute the update and return true if at least one record was updated, indicating success
        }
    }

    // Delete a cat from the database based on the provided catID. This will remove the cat's record entirely from the Cat table
    public boolean deleteCat(int catID) throws SQLException 
    {
        String sql = "DELETE FROM Cat WHERE catID = ?";
        // Use try-with-resources to ensure that the database connection and prepared statement are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, catID);
            return ps.executeUpdate() > 0;
        }
    }

    
    // --- USER PROFILE OPERATIONS ---
    public boolean updateProfile(int userID, String name, String email, String address) throws SQLException 
    {
        String sql = "UPDATE User SET userName = ?, userEmail = ?, userAddress = ? WHERE userID = ?";
        // Use try-with-resources to ensure that the database connection and prepared statement are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, address);
            ps.setInt(4, userID);
            return ps.executeUpdate() > 0;
        }
    }

    // Delete a user's account from the database based on the provided userID. This will remove the user's record entirely from the User table
    public boolean deleteAccount(int userID) throws SQLException 
    {
        String sql = "DELETE FROM User WHERE userID = ?";
        // Use try-with-resources to ensure that the database connection and prepared statement are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, userID);
            return ps.executeUpdate() > 0;
        }
    }

    // Retrieve the details of a user based on the provided userID. This will return an Object array containing 
    // the user's name, email, and address.
    public Object[] getUserDetails(int userID) throws SQLException 
    {
        String sql = "SELECT userName, userEmail, userAddress FROM User WHERE userID = ?";
        // Use try-with-resources to ensure that the database connection, prepared statement, and result set are properly closed after use
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, userID); // Set the userID parameter to specify which user's details to retrieve

            // Execute the query and check if a result is returned. If so, create and return an Object array 
            // containing the user's name, email, and address.
            try (ResultSet rs = ps.executeQuery()) 
            {
                // If the result set has a record (i.e., the user exists), return an Object array with the user's details.
                if (rs.next()) 
                {
                    return new Object[]{rs.getString("userName"), rs.getString("userEmail"), rs.getString("userAddress")};
                }
            }
        }
        return null;
    }
}