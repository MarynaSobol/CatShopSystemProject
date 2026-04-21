// Name: Maryna Sobol 
// Student ID: C00312784 
// Date: March 2026
// Purpose: This class centralizes all styling elements for a cohesive look across the application, 
// including colors, fonts, and methods to apply consistent styles to components like buttons and input fields.

import java.awt.*; // For Color, Font, Cursor
import javax.swing.*; // For JButton, JComponent, BorderFactory
import javax.swing.border.LineBorder; // For creating borders around components
import javax.swing.text.JTextComponent; // For setting caret color in text components

// Style class to centralize all styling elements for a cohesive look across the application
public class Style 
{
    // Colors: Dark theme with Pink Accent
    public static final Color BG_COLOR = new Color(13, 17, 23); // Dark background #0d1117    
    public static final Color CARD_BG = new Color(22, 27, 34);  // Slightly lighter for cards #161b22 
    public static final Color ACCENT = new Color(255, 102, 178);   // Vibrant Pink #ff66b2
    public static final Color TEXT_MAIN = new Color(240, 246, 252); // Off-white for main text #f0f6fc
    public static final Color TEXT_DIM = new Color(139, 148, 158);  // Dimmer text for details #8b949e
    public static final Color BORDER = new Color(48, 54, 61);   // Border color #30363d

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26); // Bold and larger for titles
    public static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14); // Clean and readable for main content

    // Method to apply consistent styling to input fields (JTextField, JTextArea, etc.)
    public static void applyInputStyle(JComponent c, String title) 
    {
        c.setBackground(BG_COLOR); // Match the main background for a seamless look
        c.setForeground(Color.WHITE); // White text for high contrast
        c.setFont(MAIN_FONT); // Use the main font for inputs
        
        // Set caret color to accent for a pop of color when typing
        if (c instanceof JTextComponent) 
        {
            ((JTextComponent) c).setCaretColor(ACCENT); // Pink cursor
        }

        // Add a subtle border with a title in dim text to indicate the purpose of the input field
        c.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(BORDER), title, 0, 0, null, TEXT_DIM)); // Subtle border with title in dim text
    }

    // Method to create a styled button with primary and secondary options
    public static JButton createStyledBtn(String text, boolean isPrimary) 
    {
        JButton b = new JButton(text); // Create button with text
        b.setFocusPainted(false); // Remove default focus border for a cleaner look
        b.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Use a bold font for buttons
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover for better UX
        // Primary buttons get the accent color, while secondary buttons blend with the card background
        if (isPrimary) 
        {
            b.setBackground(ACCENT); // Vibrant pink for primary actions
            b.setForeground(Color.BLACK); // Black text for contrast on pink
            b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for a larger click area
        } 
        // Secondary buttons get a more subdued style to differentiate them from primary actions
        else 
        {
            b.setBackground(CARD_BG); // Use card background for secondary buttons to differentiate them
            b.setForeground(TEXT_MAIN); // Off-white text for readability
            b.setBorder(new LineBorder(BORDER)); // Border to define the button shape without overwhelming the design
        }
        return b; // Return the styled button
    }
    
}