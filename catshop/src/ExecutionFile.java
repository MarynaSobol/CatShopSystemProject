// Name: Maryna Sobol 
// Student ID: C00312784 
// Date: March 2026
// Purpose: This is the main execution file for the Cat Shop application. 
// It initializes the GUI, manages user sessions, and handles navigation between different screens 
// (login, signup, dashboard, etc.) based on user interactions and their role (customer or manager). 
// The SystemManager class is used to perform all business logic and database operations, 
// keeping the GUI code focused on presentation and user experience.

import java.awt.*; // For layout managers, event handling, and other GUI components
import java.sql.SQLException; // For handling SQL exceptions that may occur during database operations
import java.util.List; // For using List collections to manage data retrieved from the database
import javax.swing.*; // For building the GUI components like JFrame, JPanel, JButton, etc.
import javax.swing.table.DefaultTableModel; // For managing the data model of JTable, allowing dynamic updates to the table content

// ExecutionFile is the main entry point of the application, responsible for initializing the GUI and managing user interactions
public class ExecutionFile extends JFrame 
{
    private final CardLayout cardLayout = new CardLayout(); // CardLayout allows us to switch between different panels (like Login, Signup, Dashboard) within the same container
    private final JPanel mainContainer = new JPanel(cardLayout); // The main container that holds all the different screens of the application, managed by CardLayout
    private final SystemManager manager = new SystemManager();   // SystemManager is responsible for handling all business logic and database interactions, 
                                                                 // keeping the GUI code clean and focused on presentation
    private int sessionUserId;      // To store the ID of the logged-in user
    private int sessionUserStatus;  // To store if they are a Customer (0) or Manager (1)

    // Constructor to set up the main JFrame and initialize the first screen (Login)
    public ExecutionFile() 
    {
        setTitle("Cat Shop System"); // Set the title of the application window
        setSize(1000, 750); // Set the initial size of the application window to 1000x750 pixels, providing ample space for all components
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ensure the application exits when the window is closed
        setLocationRelativeTo(null); // Center the application window on the screen for better user experience

        mainContainer.add(createAuthPanel(true), "LOGIN"); // Add the login panel to the main container with the identifier "LOGIN"
        mainContainer.add(createSignUpPanel(), "SIGNUP"); // Add the signup panel to the main container with the identifier "SIGNUP"

        add(mainContainer); // Add the main container to the JFrame, which will display the current active panel based on the CardLayout
        setVisible(true); // Make the JFrame visible to the user, starting the application
    }


    // LOGIN PANEL: Username & Password fields with Login Button
    // Also includes a button to switch to the Signup panel for new users
    private JPanel createAuthPanel(boolean isLogin) 
    {
        JPanel wrapper = new JPanel(new GridBagLayout()); // Wrapper panel to center the login form on the screen using GridBagLayout
        wrapper.setBackground(Style.BG_COLOR); // Set the background color of the wrapper to match the overall theme of the application

        JPanel container = new JPanel(new GridBagLayout()); // Container panel that holds the actual login form components, 
                                                            // also using GridBagLayout for flexible component arrangement
        container.setBackground(Style.CARD_BG); // Set the background color of the container to differentiate it from the wrapper and create a card-like appearance
        container.setBorder(BorderFactory.createLineBorder(Style.BORDER)); // Add a border to the container for better visual separation from the background
        
        GridBagConstraints g = new GridBagConstraints(); // GridBagConstraints to control the layout of components within the container 
        g.insets = new Insets(10, 25, 10, 25); // Set padding around components for better spacing
        g.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally within their grid cell
        g.gridx = 0; // All components will be in the first column (0) since we want a single column layout

        JLabel title = new JLabel(isLogin ? "LOGIN" : "CAT REGISTRATION", SwingConstants.CENTER); // Title label that changes based on whether 
                                                                                                  // it's the login or signup panel, centered horizontally
        title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
        title.setForeground(Style.ACCENT);  // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
        g.gridy = 0; container.add(title, g); // Add the title to the container at grid position (0, 0)

        JTextField uName = new JTextField(20); // Text field for the username input, with a column width of 20 for a reasonable size
        Style.applyInputStyle(uName, "Username"); // Apply consistent styling to the username field using the Style class, 
                                                        // with a title "Username" for the border
        g.gridy = 1; container.add(uName, g); // Add the username field to the container at grid position (0, 1)

        JPasswordField uPass = new JPasswordField(20); // Password field for the password input, 
                                                                // with a column width of 20 to match the username field
        Style.applyInputStyle(uPass, "Password"); // Apply consistent styling to the password field using the Style class, 
                                                        // with a title "Password" for the border
        g.gridy = 2; container.add(uPass, g); // Add the password field to the container at grid position (0, 2)

        JButton loginBtn = Style.createStyledBtn("LOGIN", true); // Create a styled button for the login action using the Style class, 
                                                        // with the text "LOGIN" and marked as a primary button for accent styling
        g.gridy = 3; container.add(loginBtn, g); // Add the login button to the container at grid position (0, 3)
      
        // LOGIN ACTION: Validate credentials and show dashboard on success
        // The login button's action listener will attempt to authenticate the user using the SystemManager's login method.
        loginBtn.addActionListener(e -> 
{
    // Attempt to log in using the provided username and password
    try 
    {
        int[] data = manager.login(uName.getText(), new String(uPass.getPassword())); // Call the login method of SystemManager, passing the username and password. 
                                                        // The password is retrieved as a char array from JPasswordField and converted to String for processing.
        // Check if the login was successful (data is not null)
        if (data != null) 
        {
            // SAVE THE SESSION DATA HERE
            sessionUserId = data[0]; // Store the user ID from the login response in the session variable 
                                    // for later use in the application (e.g., to fetch user-specific data)
            sessionUserStatus = data[1]; // Store the user status (0 for Customer, 1 for Manager) from the login response in the session variable 
                                    // to control access to certain features in the application based on user role

            JOptionPane.showMessageDialog(this, "Logged in successfully!"); // Show a success message to the user upon successful login
            
            // Re-create the menu so it checks the NEW sessionUserStatus
            mainContainer.add(createMainMenu(), "DASH"); // Add the main menu panel to the main container with the identifier "DASH". 
                                                                    // This panel will be created based on the user's status (Customer or Manager).
            cardLayout.show(mainContainer, "DASH"); // Switch to the main menu panel, effectively taking the user to the dashboard after a successful login
        } 
        // If login fails (data is null), show an error message to the user
        else 
        {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE); // Show an error dialog with the message "Invalid credentials" 
                                                                                                // and an error icon to inform the user that the login attempt was unsuccessful
        }
    } 
    // Catch any SQL exceptions that may occur during the login process and print the stack trace for debugging purposes
    catch (SQLException ex) 
    { 
        ex.printStackTrace(); // Print the stack trace of the exception to the console for debugging.  
    }
});

        // SWITCH TO SIGNUP PANEL
        // The button to switch to the signup panel is added below the login button. When clicked, it will show the signup panel using the CardLayout.
        JButton switchBtn = Style.createStyledBtn("No account? Register here", false); // A styled button for switching to the signup panel using the Style class, 
                                                        // with the text "No account? Register here" and marked as a secondary button for subdued styling
        g.gridy = 4; container.add(switchBtn, g); // Add the switch button to the container at grid position (0, 4)
        switchBtn.addActionListener(e -> cardLayout.show(mainContainer, "SIGNUP")); // Add an action listener to the switch button that, 
        // when clicked, will show the signup panel by switching to the "SIGNUP" card in the CardLayout

        wrapper.add(container); // Add the container (which holds all the login components) to the wrapper panel, which centers it on the screen
        return wrapper; // Return the wrapper panel to be added to the main container in the JFrame
    }

    // SIGNUP PANEL: Fields for Username, Email, Password, Address + Register Button
    private JPanel createSignUpPanel() 
    {
        JPanel wrapper = new JPanel(new GridBagLayout()); // Wrapper panel to center the signup form on the screen using GridBagLayout
        wrapper.setBackground(Style.BG_COLOR); // Set the background color of the wrapper to match the overall theme of the application

        JPanel container = new JPanel(new GridBagLayout()); // Container panel that holds the actual signup form components, 
                                                            // also using GridBagLayout for flexible component arrangement
        container.setBackground(Style.CARD_BG); // Set the background color of the container to differentiate it from the background and create a card-like appearance
        container.setPreferredSize(new Dimension(450, 600)); // Set a preferred size for the signup form to ensure it has enough space for all components and looks balanced on the screen
        container.setBorder(BorderFactory.createLineBorder(Style.BORDER)); // Add a border to the container for better visual separation from the background

        GridBagConstraints g = new GridBagConstraints(); // GridBagConstraints to control the layout of components within the container
        g.insets = new Insets(8, 25, 8, 25); // Set padding around components for better spacing, slightly less than the login panel for a more compact look
        g.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontally within their grid cell for a cleaner look
        g.gridx = 0; // All components will be in the first column (0) since we want a single column layout

        JLabel title = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER); // Title label for the signup panel, with the text "CREATE ACCOUNT" and centered horizontally
        title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
        title.setForeground(Style.ACCENT); // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
        g.gridy = 0; container.add(title, g); // Add the title to the container at grid position (0, 0)

        // Required Fields (ID removed as it's now automatic)
        JTextField nameF = new JTextField(); // Text field for the username input in the signup form
        Style.applyInputStyle(nameF, "Username"); // Apply consistent styling to the username field using the Style class, 
                                                        // with a title "Username" for the border to indicate what the field is for
        g.gridy = 1; container.add(nameF, g); // Add the username field to the container at grid position (0, 1)

        JTextField emailF = new JTextField(); // Text field for the email input in the signup form
        Style.applyInputStyle(emailF, "Email Address"); // Apply consistent styling to the email field using the Style class, 
                                                        // with a title "Email Address" for the border to indicate what the field is for
        g.gridy = 2; container.add(emailF, g); // Add the email field to the container at grid position (0, 2)

        JPasswordField passF = new JPasswordField(); // Password field for the password input in the signup form, which hides the input for security
        Style.applyInputStyle(passF, "Password"); // Apply consistent styling to the password field using the Style class, 
                                                        // with a title "Password" for the border to indicate what the field is for
        g.gridy = 3; container.add(passF, g); // Add the password field to the container at grid position (0, 3)

        JTextField addrF = new JTextField(); // Text field for the home address input in the signup form
        Style.applyInputStyle(addrF, "Home Address"); // Apply consistent styling to the address field using the Style class, 
                                                        // with a title "Home Address" for the border to indicate what the field is for
        g.gridy = 4; container.add(addrF, g); // Add the address field to the container at grid position (0, 4)

        JButton regBtn = Style.createStyledBtn("REGISTER NOW", true); // Create a styled button for the registration action using the Style class, 
                                                        // with the text "REGISTER NOW" and marked as a primary button for accent styling
        g.gridy = 5; container.add(regBtn, g); // Add the register button to the container at grid position (0, 5)

        // REGISTER ACTION: Insert new user into database and switch to login on success
        regBtn.addActionListener(e -> 
        {
            // When the register button is clicked, attempt to register the new user using the SystemManager's registerUser method.
            try 
            {
                boolean success = manager.registerUser(nameF.getText(), emailF.getText(), 
                                                       new String(passF.getPassword()), addrF.getText()); 
                                                       // Call the registerUser method of SystemManager, passing the username, email, 
                                                       // password (converted from char array to String), and address from the input fields. 
                                                        // This method will handle the logic of inserting the new user into the database and 
                                                        // return true if successful.
                
                // If registration is successful, show a success message and switch back to the login panel
                //  so the user can log in with their new account
                if (success) 
                {
                    JOptionPane.showMessageDialog(this, "Success! Please login."); // Show a success message to the user upon successful registration, prompting them to log in with their new account
                    cardLayout.show(mainContainer, "LOGIN"); // Switch to the login panel, allowing the user to log in with the account they just created
                }
            } 

            // Catch any SQL exceptions that may occur during the registration process and show an error message to the user
            catch (SQLException ex) 
            {
                JOptionPane.showMessageDialog(this, "Registration Failed: " + ex.getMessage()); // Show an error dialog with the message 
                                                        // "Registration Failed" followed by the specific error message from the exception, 
                                                        // to inform the user that the registration attempt was unsuccessful 
                                                        // and provide details on what went wrong
            }
        });

        // BACK TO LOGIN BUTTON
        JButton backBtn = Style.createStyledBtn("Back to Login", false); // Create a styled button for going 
                                                        // back to the login panel using the Style class, 
                                                        // with the text "Back to Login" and marked as a secondary button for subdued styling
        g.gridy = 6; container.add(backBtn, g); // Add the back button to the container at grid position (0, 6)
        backBtn.addActionListener(e -> cardLayout.show(mainContainer, "LOGIN")); // Add an action listener to the back button that, 
        // when clicked, will show the login panel by switching to the "LOGIN" card in the CardLayout

        wrapper.add(container); // Add the container (which holds all the signup components) to the wrapper panel,
                               //  which centers it on the screen
        return wrapper; // Return the wrapper panel to be added to the main container in the JFrame
    }

    // DASHBOARD PANEL: Displays after successful login, shows user options based on status
private JPanel createMainMenu() 
{
    JPanel menuPanel = new JPanel(new GridBagLayout()); // Main menu panel that will serve as the dashboard after login,
                                                        //  using GridBagLayout to center the content
    menuPanel.setBackground(Style.BG_COLOR); // Set the background color of the menu panel to match the overall theme of the application

    JPanel container = new JPanel(); // Container panel that holds the actual menu options, using a simple BoxLayout to stack buttons vertically
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // Set the layout of the container to BoxLayout with a vertical axis,
                                                                     //  so that components (buttons) will be stacked on top of each other
    container.setBackground(Style.CARD_BG); // Set the background color of the container to differentiate it from the menu panel and create a card-like appearance
    container.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding around the container for better
                                                                                    //  spacing and to make it look more balanced on the screen

    JLabel title = new JLabel("WELCOME TO CAT SHOP"); // Title label for the main menu, with the text "WELCOME TO CAT SHOP"
    title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
    title.setForeground(Style.ACCENT); // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
    title.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title horizontally within the container by setting its alignment to CENTER_ALIGNMENT
    container.add(title); // Add the title to the container
    container.add(Box.createRigidArea(new Dimension(0, 30))); // Add vertical spacing (30 pixels) below the title to 
                                                                        // separate it from the menu options that will be added next

    // --- CUSTOMER ONLY ACTIONS ---
    // Only show "VIEW CAT CATALOG" if the user is NOT a manager (status 0)
    if (sessionUserStatus == 0) 
    {
        JButton btnView = createMenuBtn(" VIEW CAT CATALOG"); // Create a menu button for viewing the cat catalog using the createMenuBtn helper method, 
                                                        // with the text "VIEW CAT CATALOG"
        btnView.addActionListener(e -> 
        {
            mainContainer.add(createCatalogPanel(), "CATALOG"); // When the button is clicked, add the catalog panel to the main container with the identifier "CATALOG". 
                                                        // This panel will display the list of cats available for adoption.
            cardLayout.show(mainContainer, "CATALOG"); // Switch to the catalog panel, allowing the user to view the cat inventory and place orders if they are a customer.
        });
        container.add(btnView); // Add the "VIEW CAT CATALOG" button to the container so it appears on the main menu for customers
        container.add(Box.createRigidArea(new Dimension(0, 15))); // Add vertical spacing (15 pixels) below the "VIEW CAT CATALOG" button to 
                                                                                // separate it from the next menu option for better visual clarity
    }

    // --- SHARED ACTIONS ---
    JButton btnOrders = createMenuBtn(" MANAGE MY ORDERS"); // Create a menu button for managing orders using the createMenuBtn helper method,
    
    // with the text "MANAGE MY ORDERS". This option will be available to both customers and managers, but will show different content based on the user's role.
    btnOrders.addActionListener(e -> 
    {
        mainContainer.add(createOrdersPanel(), "ORDERS"); // When the button is clicked, add the orders panel to the main container with the identifier "ORDERS". 
                                                        // This panel will display the user's past orders if they are a customer, or all orders if they are a manager.
        cardLayout.show(mainContainer, "ORDERS"); // Switch to the orders panel, allowing the user to view and manage their orders. Customers will see only their orders,
        //  while managers will see all orders in the system.
    });
    container.add(btnOrders); // Add the "MANAGE MY ORDERS" button to the container so it appears on the main menu for both customers and managers

    // --- MANAGER ONLY ACTIONS ---
    // Only show "MANAGE CAT INVENTORY" if the user IS a manager (status 1)
    if (sessionUserStatus == 1) 
    {
        JLabel adminLabel = new JLabel("--- MANAGER TOOLS ---"); // A label to separate the manager-specific tools from the shared options, 
                                                                    // with the text "--- MANAGER TOOLS ---"
        adminLabel.setForeground(Style.TEXT_DIM); // Set the color of the admin label to a dimmed text color from the Style class to visually 
                                                // differentiate it from the main title and indicate that it's a section header
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the admin label horizontally within the container by setting its alignment to CENTER_ALIGNMENT
        container.add(Box.createRigidArea(new Dimension(0, 25))); // Add vertical spacing (25 pixels) above the admin label to separate it
                                                            //  from the previous menu options and visually indicate a new section for manager tools
        container.add(adminLabel); // Add the admin label to the container so it appears on the main menu for managers,
                                //  serving as a header for the manager-specific actions
        container.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing (10 pixels) below the admin label to separate it
                                                                        //  from the first manager tool button for better visual clarity

        JButton btnManage = createMenuBtn(" MANAGE CAT INVENTORY"); // Create a menu button for managing the cat inventory using the createMenuBtn helper method
        btnManage.addActionListener(e -> refreshCatalog()); // When the button is clicked, call the refreshCatalog method which will add the catalog panel to the main container and switch to it. 
                                                        // This allows managers to view and manage the cat inventory, including adding, amending, or deleting cats.
        container.add(btnManage); // Add the "MANAGE CAT INVENTORY" button to the container so it appears on the main menu for managers, allowing them to access the inventory management features
    }
   
// --- PROFILE ---
    JButton btnProfile = createMenuBtn(" MY PROFILE"); // Create a menu button for accessing the user's profile using the createMenuBtn helper method, 
                                                        // with the text "MY PROFILE". This option will be available to both customers and managers, 
                                                        // allowing them to view and update their profile information or delete their account if they wish.
    
    // When the profile button is clicked, it will add the profile menu panel to the main container with the identifier "PROFILE_MENU" and switch to it.
    btnProfile.addActionListener(e -> {
        // Navigate to the 'PROFILE_MENU' card
        mainContainer.add(createProfileMenu(), "PROFILE_MENU"); // Add the profile menu panel to the main container with the identifier "PROFILE_MENU".
        //  This panel will allow users to view and update their profile information, or delete their account.
        cardLayout.show(mainContainer, "PROFILE_MENU"); // Switch to the profile menu panel, allowing the user to access their profile options.
        //  Both customers and managers will have access to this panel,
    });
    
    // ADD THIS LINE BELOW
    container.add(btnProfile); // Add the "MY PROFILE" button to the container so it appears on the main menu for both customers and managers,
    //  allowing them to access their profile management features

   // LOGOUT (Bottom)
container.add(Box.createRigidArea(new Dimension(0, 30))); // Add vertical spacing (30 pixels) above the logout button to separate it 
// from the other menu options and visually indicate that it's a different type of action (session management rather than navigation)
JButton btnLogout = createMenuBtn("LOG OUT"); // Create a menu button for logging out using the createMenuBtn helper method, with the text "LOG OUT".

// When the logout button is clicked, it will clear the session data (user ID and status) and switch back to the login panel, 
// effectively logging the user out of the application.
btnLogout.addActionListener(e -> 
{
    //Clear the session data so the app "forgets" the user
    sessionUserId = -1; // Reset the session user ID to -1, indicating that no user is currently logged in. 
    // This effectively clears the user's session data.
    sessionUserStatus = -1; // Reset the session user status to -1, indicating that the user's role is also cleared.

    // Go back to the login screen
    cardLayout.show(mainContainer, "LOGIN");
    
    // Optional: Provide feedback
    System.out.println("User logged out. Session cleared. - ExecutionFile.java:333"); // Print a message to the console for debugging purposes, 
    // confirming that the user has been logged out and the session data has been cleared.
});

container.add(btnLogout); // Add the "LOG OUT" button to the container so it appears on the main menu for both customers and managers,
//  allowing them to log out of their account and return to the login screen
    menuPanel.add(container); // Add the container (which holds all the menu options) to the main menu panel,
    //  which will display it on the screen when the user is logged in
    return menuPanel; // Return the main menu panel to be added to the main container in the JFrame when the user logs in successfully

    
}


// CATALOG PANEL: Displays the list of cats in a table with options to place an order (for customers) or manage inventory (for managers)
private JPanel createCatalogPanel() 
{
    JPanel panel = new JPanel(new BorderLayout()); // Catalog panel that will display the list of cats in a table, 
    // using BorderLayout to organize the title at the top, the table in the center, and action buttons at the bottom
    panel.setBackground(Style.BG_COLOR); // Set the background color of the catalog panel to match the overall theme of the application
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the catalog panel
    //  for better spacing and to make it look more balanced on the screen

    JLabel title = new JLabel("CAT INVENTORY MANAGEMENT", SwingConstants.CENTER); // Title label for the catalog panel,
    //  with the text "CAT INVENTORY MANAGEMENT" and centered horizontally
    title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
    title.setForeground(Style.ACCENT); // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
    panel.add(title, BorderLayout.NORTH); // Add the title to the top (NORTH) of the catalog panel using BorderLayout

    String[] columns = {"ID", "Name", "Details", "Age", "Price (€)", "Status"}; // Define the column names for the JTable that 
    // will display the cat inventory.

    // Create a DefaultTableModel with the defined columns and 0 rows initially.
    //  Override the isCellEditable method to make the cells non-editable,
    DefaultTableModel model = new DefaultTableModel(columns, 0) 
    {
        // Override the isCellEditable method to prevent users from editing the cells directly in the table.
        @Override
        // This method is called by the JTable to determine if a cell should be editable.
        // By returning false, we ensure that all cells in the table are read-only, which is important for maintaining data integrity since
        public boolean isCellEditable(int row, int column) 
        { return false; } 
    };
    
    JTable table = new JTable(model); // Create a JTable using the defined model, which will display the cat inventory in a tabular format.
    
    // Load the cat data from the database using the SystemManager's getAllCats method and populate the table model with this data.
try 
{
    // Pass 'true' if user is a manager (status 1), 'false' otherwise
    // This allows the getAllCats method to return all cats for managers, while customers will only see available cats.
    List<Object[]> cats = manager.getAllCats(sessionUserStatus == 1); 
    for (Object[] cat : cats) 
    { 
        model.addRow(cat);  // Add each cat's data as a new row in the table model, which will then be displayed in the JTable.
    }
} 
// Catch any SQL exceptions that may occur during the data retrieval process and show an error message to the user
catch (SQLException e) 
{ 
    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());  // Show an error dialog with the message "Error: " followed by the specific 
                                    // error message from the exception, 
                                    // to inform the user that there was an issue loading the cat inventory data from the database
}


    panel.add(new JScrollPane(table), BorderLayout.CENTER); // Add the JTable to the center of the catalog panel wrapped in 
    // a JScrollPane to allow for scrolling if there are many cats in the inventory

    JPanel bottomPanel = new JPanel(new FlowLayout()); // Create a bottom panel to hold the action buttons, 
    // using FlowLayout to arrange them horizontally
    bottomPanel.setOpaque(false); // Set the bottom panel to be transparent so that the background color 
    // of the main panel shows through
    
    JButton btnBack = Style.createStyledBtn("BACK TO MENU", false); // Create a styled button for going back to the main menu using the Style class, 
                                                        // with the text "BACK TO MENU" and marked as a secondary button for subdued styling
    btnBack.addActionListener(e -> cardLayout.show(mainContainer, "DASH")); // Add an action listener to the back button that, when clicked, 
    // will show the main menu panel by switching to the "DASH" card in the CardLayout
    bottomPanel.add(btnBack); // Add the back button to the bottom panel so it appears at the bottom of the catalog screen,
    //  allowing users to easily navigate back to the main menu

   // PLACE ORDER (Customers) - Only show if user is NOT a manager (status 0)
    JButton btnBuy = Style.createStyledBtn("PLACE ORDER", true); // Create a styled button for placing an order
    //  using the Style class, with the text "PLACE ORDER" and marked as a primary button for accent styling
    btnBuy.addActionListener(e -> handleOrderAction(table)); // Add an action listener to the place order button that, 
    // when clicked, will call the handleOrderAction method,
    bottomPanel.add(btnBuy); // Add the place order button to the bottom panel so it appears at the bottom of the catalog screen for customers

    // MANAGE INVENTORY (Managers) - Only show if user IS a manager (status 1)
if (sessionUserStatus == 1) 
{
    JButton btnAddNew = Style.createStyledBtn("ADD NEW CAT", true); // Create a styled button for adding a new cat to the inventory using the Style class, 
                                                        // with the text "ADD NEW CAT" and marked as a primary button for accent styling
    JButton btnAmend = Style.createStyledBtn("AMEND SELECTED", true); // Create a styled button for amending the details of a selected cat in the inventory using the Style class, 
                                                        // with the text "AMEND SELECTED" and marked as a primary button for accent styling
    JButton btnDelete = Style.createStyledBtn("DELETE SELECTED", true); // Create a styled button for deleting a selected cat from the inventory using the Style class, 
                                                        // with the text "DELETE SELECTED" and marked as a primary button for accent styling
 //Style.createStyledBtn sets the font, foreground, and border for the buttons, and by passing 'true' we also apply the accent color to make them stand out as primary actions.                                                       
//isPrimary is true for all three buttons to give them accent styling, but we will differentiate them further with custom background colors to indicate their different functions (add, amend, delete).

    // Styling colors to differentiate actions
    btnAddNew.setBackground(new Color(34, 139, 34)); // Forest Green for adding new cats, indicating a positive action
    btnAmend.setBackground(new Color(70, 130, 180));  // Steel Blue for amending cat details, indicating a neutral action that involves modification
    btnDelete.setBackground(new Color(178, 34, 34)); // Firebrick Red for deleting cats, indicating a destructive action that should be used with caution

    // ADD NEW CAT LOGIC
    btnAddNew.addActionListener(e -> 
    {
        // When the "ADD NEW CAT" button is clicked, show a series of input dialogs to gather the 
        // new cat's information (name, age, details, price).
        try 
        {
            String name = JOptionPane.showInputDialog(this, "Cat Name:"); // Show an input dialog to the user asking for the cat's name
            //  and store the input in the variable 'name'.
            if (name == null || name.isEmpty()) return; // If the user cancels the input dialog or enters an empty name, 
            // exit the method to prevent adding a cat with invalid data.
            
            String age = JOptionPane.showInputDialog(this, "Age:"); // Show an input dialog to the user asking for the cat's age
            //  and store the input in the variable 'age'.
            String details = JOptionPane.showInputDialog(this, "Details:"); // Show an input dialog to the user asking for additional
            //  details about the cat and store the input in the variable 'details'.
            String price = JOptionPane.showInputDialog(this, "Price:"); // Show an input dialog to the user asking for the cat's price and store
            //  the input in the variable 'price'.
            
            // Attempt to add the new cat to the inventory using the SystemManager's addCat method, passing the collected information.
            if(manager.addCat(name, details, Integer.parseInt(age), Float.parseFloat(price))) 
            {
                JOptionPane.showMessageDialog(this, "Cat Added Successfully!"); // If the cat is added successfully, 
                // show a success message to the user confirming that the new cat has been added to the inventory.
                refreshCatalog();  // Call the refreshCatalog method to update the catalog panel and show the newly added cat in the table.
            }
        } 
        // Catch any exceptions that may occur during the input parsing or database operation and show an error message to the user.
        catch (Exception ex) 
        {
            JOptionPane.showMessageDialog(this, "Error: Check your numeric inputs."); // Show an error dialog with the message 
            //  to inform the user that there was an issue with the data they entered,
        }
    });

    // AMEND CAT LOGIC
    btnAmend.addActionListener(e -> 
    {
        int row = table.getSelectedRow(); // Get the index of the currently selected row in the table.
        //  This will be used to identify which cat the manager wants to amend.

        // Check if a row is actually selected
        // If no row is selected (row == -1), show a message prompting the user to select a cat first.
        if (row != -1) 
        {
            int id = (int) table.getValueAt(row, 0); // Get the ID of the selected cat from the first column of the table, 
            // which will be used to identify the cat in the database for amendment.
            String[] options = {"Name", "Price", "Details", "Age", "Status"}; // Define the options for which field the manager wants to amend
            //  for the selected cat.
            String choice = (String) JOptionPane.showInputDialog(this, "What info do you want to change?", 
                                 "Amend Cat", JOptionPane.QUESTION_MESSAGE, null, options, options[0]); // Show an input dialog 
                                 // with a dropdown menu to the user, asking "What info do you want to change?" and providing the defined options.
            
            // The user's choice will be stored in the variable 'choice'. If the user cancels the dialog, 'choice' will be null.
            if (choice != null) 
            {
                String newVal = JOptionPane.showInputDialog("Enter new value for " + choice + ":"); // Show another input dialog asking the user
                //  to enter the new value for the field they chose to amend, and store the input in 'newVal'.

                // If the user cancels the input dialog or enters an empty value, do not proceed with the amendment.
                // This check ensures that we don't attempt to update the database with invalid data.
                if (newVal != null && !newVal.isEmpty()) 
                {
                    // Attempt to amend the specific field of the selected cat in the database using the SystemManager's 
                    // amendSpecificField method, passing the cat ID, the field to amend (choice), and the new value.
                    try 
                    {
                        // The amendSpecificField method will handle the logic of updating the specified field
                        //  for the cat with the given ID in the database.
                        if (manager.amendSpecificField(id, choice, newVal)) 
                        {
                            JOptionPane.showMessageDialog(this, "Field Updated!"); // If the amendment is successful, 
                            // show a success message to the user confirming that the cat's information has been updated.
                            refreshCatalog(); // Call the refreshCatalog method to update the catalog panel and reflect the changes made to the cat's information in the table.
                        }
                    } 

                    // Catch any SQL exceptions that may occur during the database update operation and show an error message to the user.
                    catch (SQLException ex) 
                    { JOptionPane.showMessageDialog(this, "Update failed."); // Show an error dialog with the message "Update failed." 
                    // to inform the user that there was an issue updating the cat's information in the database.
                    }
                }
            }
        } 

        // If no row is selected, show a message prompting the user to select a cat first before attempting to amend.
        else 
        { JOptionPane.showMessageDialog(this, "Select a cat first!"); // Show an error dialog with the message "Select a cat first!"
        //  to inform the user that they need to select a cat from the table before they can amend its information.
        }
    });

    // DELETE CAT LOGIC
    btnDelete.addActionListener(e -> 
    {
        int row = table.getSelectedRow(); // Get the index of the currently selected row in the table. This will be used
        //  to identify which cat the manager wants to delete.

        // Check if a row is actually selected        
        // If no row is selected (row == -1), show a message prompting the user to select a cat first.
        if (row != -1) 
        {
            int id = (int) table.getValueAt(row, 0); // Get the ID of the selected cat from the first column of the table, 
            // which will be used to identify the cat in the database for deletion.
            int confirm = JOptionPane.showConfirmDialog(this, "Delete Cat ID: " + id + "?"); // Show a confirmation dialog to the user 
            // asking if they are sure they want to delete the cat with the specified ID.

            // If the user confirms the deletion (confirm == JOptionPane.YES_OPTION),
            //  attempt to delete the cat from the database using the SystemManager's deleteCat method.
            if (confirm == JOptionPane.YES_OPTION) 
            {
                // The deleteCat method will handle the logic of removing the cat with the given ID from the database.
                try 
                {
                    // If the deletion is successful, show a success message to the user confirming that the cat
                    //  has been deleted from the inventory.
                    if (manager.deleteCat(id)) 
                    {
                        JOptionPane.showMessageDialog(this, "Deleted."); // Show a message dialog with the text "Deleted." 
                        // to inform the user that the cat has been successfully removed from the inventory.
                        refreshCatalog(); // Call the refreshCatalog method to update the catalog panel and reflect the removal
                        //  of the cat from the table.
                    }
                } 

                // Catch any SQL exceptions that may occur during the database deletion operation and show an error message to the user.
                catch (SQLException ex) 
                { JOptionPane.showMessageDialog(this, "Delete failed.");
                // Show an error dialog with the message "Delete failed." to inform the user that there was an issue deleting 
                // the cat from the database.
                }
            }
        } 
        // If no row is selected, show a message prompting the user to select a cat first before attempting to delete.
        else 
        { JOptionPane.showMessageDialog(this, "Select a cat first!"); }
    });

    // for managers
    bottomPanel.add(btnAddNew); // Add the "ADD NEW CAT" button to the bottom panel so it appears at the bottom of the catalog screen 
    bottomPanel.add(btnAmend); // Add the "AMEND SELECTED" button to the bottom panel so it appears at the bottom of the catalog screen 
    bottomPanel.add(btnDelete); // Add the "DELETE SELECTED" button to the bottom panel so it appears at the bottom of the catalog screen
}

    panel.add(bottomPanel, BorderLayout.SOUTH); // Add the bottom panel (which holds the action buttons) 
    // to the bottom (SOUTH) of the catalog panel using BorderLayout
    return panel; // Return the catalog panel to be added to the main container in the JFrame 
    // when the user chooses to view the cat catalog from the main menu
}

// Helper method to refresh the catalog panel after adding, amending, or deleting a cat.
private void refreshCatalog() 
{
    mainContainer.add(createCatalogPanel(), "CATALOG"); // Recreate the catalog panel with updated data and add
    //  it to the main container with the identifier "CATALOG".
    cardLayout.show(mainContainer, "CATALOG"); // Switch to the newly created catalog panel to 
    // reflect the changes made to the cat inventory
}

// ORDERS PANEL: Displays the user's past orders (for customers) or all orders (for managers) in a table format
    private JPanel createOrdersPanel() 
    {
        JPanel panel = new JPanel(new BorderLayout()); // Orders panel that will display the user's past orders in a table format,
        panel.setBackground(Style.BG_COLOR); // Set the background color of the orders panel to match the overall theme of the application
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding around the orders panel 
        // for better spacing and to make it look more balanced on the screen`

        JLabel title = new JLabel("MY ORDER HISTORY", SwingConstants.CENTER); // Title label for the orders panel, 
        // with the text "MY ORDER HISTORY" and centered horizontally
        title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
        title.setForeground(Style.ACCENT); // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
        panel.add(title, BorderLayout.NORTH); // Add the title to the top (NORTH) of the orders panel using BorderLayout

        String[] columns = {"Order ID", "Cat Name", "Purchase Date", "Price Paid"}; // Define the column names for the JTable that will display 
        // the user's order history.
        DefaultTableModel model = new DefaultTableModel(columns, 0); // Create a DefaultTableModel with the defined columns
        //  and 0 rows initially. This model will be used to populate the JTable with order data.
        JTable table = new JTable(model); // Create a JTable using the defined model, which will display 
        // the user's order history in a tabular format.

        // Load the order data from the database using the SystemManager's 
        // getUserOrders method and populate the table model with this data.
        try 
        {
            List<Object[]> orders = manager.getUserOrders(sessionUserId); // Retrieve the list of orders for the current user\
            //  from the database using the SystemManager's getUserOrders method, passing the sessionUserId to identify which user's 4
            // orders to fetch. This method will return all orders for managers and only the user's own orders for customers.

            //  Iterate through the list of orders and add each order's data as a new row in the table model, 
            // which will then be displayed in the JTable.
            for (Object[] row : orders) 
            {
                model.addRow(row); // Add each order's data as a new row in the table model, which will then be displayed in the JTable.
            }
        } 
        // Catch any SQL exceptions that may occur during the data retrieval process and show an error message to the user.
        catch (SQLException e) 
        {
            JOptionPane.showMessageDialog(this, "Error loading order history.");
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER); // Add the JTable to the center of the orders panel wrapped 
        // in a JScrollPane to allow for scrolling if there are many orders in the user's history

        JButton btnBack = Style.createStyledBtn("BACK TO MENU", false); // Create a styled button for going back to the 
        // main menu using the Style class
        btnBack.addActionListener(e -> cardLayout.show(mainContainer, "DASH")); // Add an action listener to the back button that,
        //  when clicked, will show the main menu panel by switching to the "DASH" card in the CardLayout
        panel.add(btnBack, BorderLayout.SOUTH); // Add the back button to the bottom (SOUTH) of the orders panel 
        // using BorderLayout so it appears at the bottom of the screen,

        return panel; // Return the orders panel to be added to the main container in the JFrame
        //  when the user chooses to manage their orders from the main menu
    }

    // Helper method to create styled menu buttons for the main menu and profile menu, 
    // to avoid code duplication and maintain consistent styling across the application.
    private JButton createMenuBtn(String text) 
    {
        JButton b = Style.createStyledBtn(text, false); // Create a styled button using the Style class with 
        // the specified text and marked as a secondary button for subdued styling
        b.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally within its container 
        // by setting its alignment to CENTER_ALIGNMENT
        b.setMaximumSize(new Dimension(300, 50)); // Set a maximum size for the button to ensure consistency 
        // in the main menu layout,
        return b; // Return the created button to be added to the menu panels
    }

    // Logic for handling the "PLACE ORDER" action when a customer selects a cat from the catalog and clicks the "PLACE ORDER" button.
 private void handleOrderAction(JTable table) 
 {
        int row = table.getSelectedRow(); // Get the index of the currently selected row in the table. 
        // This will be used to identify which cat the customer wants to order.
        
        // Check if a row is actually selected
        if (row == -1) 
        {
            JOptionPane.showMessageDialog(this, "Please select a cat from the table first."); // If no row is selected (row == -1), 
            // show a message prompting the user to select a cat first.
            return; // Exit the method to prevent further execution since we need a selected cat to place an order.
        }

        // Check if the selected cat is already sold by looking at the "Status" column (index 5) of the selected row in the table.
        String status = (String) table.getValueAt(row, 5); 
        // If the status of the selected cat is "Sold", show a message informing the user that this cat 
        // has already been adopted and cannot be ordered again,
        if ("Sold".equalsIgnoreCase(status)) 
        {
            JOptionPane.showMessageDialog(this, 
                "This cat has already been adopted and cannot be ordered again.", 
                "Notice", JOptionPane.WARNING_MESSAGE); // Show a warning dialog with the message to 
                // inform the user that the selected cat is not available for order because it has already been adopted.
            return;
        }

        /// If a row is selected and the cat is available, retrieve the necessary
        //  information about the cat from the table to place the order.
        int catID = (int) table.getValueAt(row, 0); // Get the ID of the selected cat from the first column of the table
        String name = (String) table.getValueAt(row, 1); // Get the name of the selected cat from the second column of the table
        float price = (float) table.getValueAt(row, 4); // Get the price of the selected cat from the fifth column of the table,
        // which will be used to process the order and charge the customer accordingly.

        // Show a confirmation dialog to the user asking if they are sure they want to place the order for the selected cat
        int confirm = JOptionPane.showConfirmDialog(this, "Confirm purchase of " + name + " for €" + price + "?");
        // If the user confirms the order (confirm == JOptionPane.YES_OPTION),
        //  attempt to place the order using the SystemManager's placeOrder method,
        if (confirm == JOptionPane.YES_OPTION) 
        {
            // The placeOrder method will handle the logic of creating a new order in the database, updating the cat's status to "Sold",
            try 
            {
                // If the order is placed successfully, show a success message to the user confirming that
                //  the order has been completed and the cat is now theirs.
                if (manager.placeOrder(sessionUserId, catID, price)) 
                {
                    JOptionPane.showMessageDialog(this, "Order Successful! " + name + " is yours.");
                    refreshCatalog(); 
                }
            } 
            // Catch any SQL exceptions that may occur during the order placement process and show an error message to the user.
            catch (SQLException ex) 
            {
                JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
            }
        }
    }

// PROFILE PANEL: Allows users to view their profile information, update it, or delete their account.
private JPanel createProfileMenu() 
{
    JPanel panel = new JPanel(new GridBagLayout()); // Profile menu panel that will allow users to view and update their profile information,
    //  or delete their account.
    panel.setBackground(Style.BG_COLOR); // Set the background color of the profile menu panel to match the overall theme of the application
    
    JPanel container = new JPanel(); // Create a container panel to hold the profile options and buttons, 
    // which will be centered within the profile menu panel using GridBagLayout
    container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // Use BoxLayout with Y_AXIS to arrange the components vertically
    //  within the container
    container.setBackground(Style.CARD_BG); // Set the background color of the container to match the card background style defined 
    // in the Style class
    container.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding around the container for better
    //  spacing and to make it look more balanced on the screen

    JLabel title = new JLabel("MY PROFILE"); // Title label for the profile menu, with the text "MY PROFILE"
    title.setFont(Style.TITLE_FONT); // Set the font of the title to the predefined TITLE_FONT for consistency across the application
    title.setForeground(Style.ACCENT); // Set the color of the title to the predefined ACCENT color to make it stand out and match the theme
    title.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title horizontally within the container by setting its alignment to CENTER_ALIGNMENT
    container.add(title); // Add the title to the container so it appears at the top of the profile menu
    container.add(Box.createRigidArea(new Dimension(0, 30))); // Add vertical spacing (30 pixels) below the title to separate
    //  it from the profile options and buttons

    // Create buttons for showing profile info, updating profile, deleting account, and going back to the main menu,
    //  using the createMenuBtn helper method to maintain consistent styling across the application.
    JButton btnShow = createMenuBtn(" SHOW PROFILE INFO");
    btnShow.addActionListener(e -> showProfileInfo());

    JButton btnUpdate = createMenuBtn(" UPDATE PROFILE");
    btnUpdate.addActionListener(e -> showUpdateForm());

    JButton btnDelete = createMenuBtn(" DELETE ACCOUNT");
    btnDelete.setForeground(Color.RED); // Keep delete distinct
    btnDelete.addActionListener(e -> deleteAccountAction());

    JButton btnBack = createMenuBtn(" BACK TO MENU");
    btnBack.addActionListener(e -> cardLayout.show(mainContainer, "DASH"));

    container.add(btnShow); // Add the "SHOW PROFILE INFO" button to the container so it appears in the profile menu,
    //  allowing users to view their current profile information
    container.add(Box.createRigidArea(new Dimension(0, 15))); // Add vertical spacing (15 pixels) between the profile info button
    //  and the update profile button for better visual separation
    container.add(btnUpdate);
    container.add(Box.createRigidArea(new Dimension(0, 15)));
    container.add(btnDelete);
    container.add(Box.createRigidArea(new Dimension(0, 15)));
    container.add(btnBack);

    panel.add(container); // Add the container (which holds all the profile options and buttons) to the profile menu panel
    return panel; // Return the profile menu panel
}
// Logic for Showing Profile Info
private void showProfileInfo() 
{
    // When the "SHOW PROFILE INFO" button is clicked, 
    // fetch the user's profile details from the database using the SystemManager's getUserDetails method,
    try 
    {
        Object[] info = manager.getUserDetails(sessionUserId); // Retrieve the user's profile details from the database using the SystemManager's 
        // getUserDetails method, passing the sessionUserId to identify which user's details to fetch
        // This method will return an array of objects containing the user's information

        // If the info is successfully retrieved (info != null), show a message dialog displaying the user's profile information (username, email, address)
        if (info != null) 
        {
            JOptionPane.showMessageDialog(this, "Username: " + info[0] + "\nEmail: " + info[1] + "\nAddress: " + info[2]);
        }
    } 
    // Catch any SQL exceptions that may occur during the data retrieval process and show an error message to the user
    catch (SQLException ex) 
    { JOptionPane.showMessageDialog(this, "Error fetching data."); }
}

// Logic for Updating Profile
private void showUpdateForm() 
{
    // When the "UPDATE PROFILE" button is clicked, fetch the user's current profile details from the database
    //  using the SystemManager's getUserDetails method
    try {
        Object[] currentData = manager.getUserDetails(sessionUserId); // Retrieve the user's current profile details from the database
        //  using the SystemManager's getUserDetails method

        // If the current data is successfully retrieved (currentData != null), pre-fill the fields in the update form with the current values
        if (currentData == null) {
            JOptionPane.showMessageDialog(this, "Could not load current profile.");
            return;
        }

        // Create text fields for the update form and pre-fill them with the current profile information (username, email, address)
        JTextField nameF = new JTextField((String) currentData[0]);
        JTextField emailF = new JTextField((String) currentData[1]);
        JTextField addrF = new JTextField((String) currentData[2]);

        // Show a dialog with the pre-filled fields allowing the user to update their profile information.
        Object[] message = 
        {
            "Update your details:", 
            "Username:", nameF, 
            "Email:", emailF, 
            "Address:", addrF
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION); 
        // Show a confirmation dialog with the pre-filled fields and the title "Update Profile"
        
        // If the user clicks OK in the dialog, attempt to update the user's profile information in 
        // the database using the SystemManager's updateProfile method,
        if (option == JOptionPane.OK_OPTION) 
        {
            // The updateProfile method will handle the logic of updating the user's profile information 
            // in the database with the new values entered in the text fields.
            if(manager.updateProfile(sessionUserId, nameF.getText(), emailF.getText(), addrF.getText())) 
            {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } 
            // If the update is successful, show a success message to the user confirming that their profile information has been updated
            else 
            {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        }
    } 
    // Catch any SQL exceptions that may occur during the data retrieval or update process and show an error message to the user
    catch (SQLException ex) 
    { 
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage()); 
    }
}

// Logic for Deleting Account
private void deleteAccountAction() 
{
    int confirm = JOptionPane.showConfirmDialog(this, "Permanently delete account?", "Confirm", JOptionPane.YES_NO_OPTION); 
    // Show a confirmation dialog asking the user if they are sure they want to permanently delete their account,
    //  with the title "Confirm" and options for YES and NO

    // If the user confirms the deletion (confirm == JOptionPane.YES_OPTION),
    //  attempt to delete the user's account from the database using the SystemManager's deleteAccount method
    if (confirm == JOptionPane.YES_OPTION) 
    {
        // The deleteAccount method will handle the logic of removing the user's account from the database
        try 
        {
            manager.deleteAccount(sessionUserId);
            cardLayout.show(mainContainer, "LOGIN");
        } 
        
        catch (SQLException ex) 
        { JOptionPane.showMessageDialog(this, "Delete failed."); }
    }
}

// Main method to launch the application
     public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(ExecutionFile::new); // Use SwingUtilities.invokeLater to ensure that the GUI is created and 
        // updated on the Event Dispatch Thread (EDT), which is the proper way to handle GUI operations in Java Swing. 
        // This will create a new instance of the ExecutionFile class,  which will initialize the application and display the main window 
        // to the user
    }
} 